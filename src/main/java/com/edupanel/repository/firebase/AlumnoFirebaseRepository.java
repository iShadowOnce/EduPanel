package com.edupanel.repository.firebase;

import com.edupanel.model.Alumno;
import com.edupanel.model.Asignatura;
import com.edupanel.model.Calificacion;
import com.edupanel.model.Rol;
import com.edupanel.repository.AlumnoRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("firebase")
public class AlumnoFirebaseRepository extends FirebaseRepositorySupport implements AlumnoRepository {

    private DatabaseReference alumnos() {
        return referencia("alumnos");
    }

    @Override
    public void guardar(Alumno alumno) {
        esperar(alumnos().child(alumno.getUid()).setValueAsync(convertirParaFirebase(alumno)));
    }

    @Override
    public Alumno buscarPorId(String id) {
        DataSnapshot snapshot = leer(alumnos().child(id));
        return snapshot.exists() ? convertirDesdeFirebase(snapshot) : null;
    }

    @Override
    public List<Alumno> listarTodos() {
        DataSnapshot snapshot = leer(alumnos());
        List<Alumno> alumnosEncontrados = new ArrayList<>();

        for (DataSnapshot hijo : snapshot.getChildren()) {
            Alumno alumno = convertirDesdeFirebase(hijo);
            if (alumno != null) {
                alumnosEncontrados.add(alumno);
            }
        }

        return alumnosEncontrados;
    }

    @Override
    public void actualizar(Alumno alumnoActualizado) {
        Alumno alumnoExistente = buscarPorId(alumnoActualizado.getUid());

        if (alumnoExistente != null) {
            alumnoExistente.setNombre(alumnoActualizado.getNombre());
            alumnoExistente.setApellido(alumnoActualizado.getApellido());
            alumnoExistente.setRut(alumnoActualizado.getRut());
            alumnoExistente.setEmail(alumnoActualizado.getEmail());
            alumnoExistente.setPassword(alumnoActualizado.getPassword());
            alumnoExistente.setRol(alumnoActualizado.getRol());
            alumnoExistente.setNotas(alumnoActualizado.getNotas());

            guardar(alumnoExistente);
        }
    }

    @Override
    public void eliminar(String id) {
        esperar(alumnos().child(id).removeValueAsync());
    }

    private Map<String, Object> convertirParaFirebase(Alumno alumno) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("uid", alumno.getUid());
        datos.put("nombre", alumno.getNombre());
        datos.put("apellido", alumno.getApellido());
        datos.put("rut", alumno.getRut());
        datos.put("email", alumno.getEmail());
        datos.put("password", alumno.getPassword());
        datos.put("rol", alumno.getRol() != null ? alumno.getRol().name() : Rol.ALUMNO.name());
        datos.put("notas", agruparNotasPorAsignatura(alumno.getNotas()));
        return datos;
    }

    private Map<String, List<Map<String, Object>>> agruparNotasPorAsignatura(List<Calificacion> notas) {
        Map<String, List<Map<String, Object>>> notasAgrupadas = new HashMap<>();

        if (notas == null) {
            return notasAgrupadas;
        }

        for (Calificacion nota : notas) {
            if (nota.getAsignatura() == null) {
                continue;
            }

            notasAgrupadas
                    .computeIfAbsent(nota.getAsignatura().name(), clave -> new ArrayList<>())
                    .add(convertirNotaParaFirebase(nota));
        }

        return notasAgrupadas;
    }

    private Map<String, Object> convertirNotaParaFirebase(Calificacion nota) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("id", nota.getId());
        datos.put("alumnoId", nota.getAlumnoId());
        datos.put("nombre", nota.getNombre());
        datos.put("rut", nota.getRut());
        datos.put("asignatura", nota.getAsignatura().name());
        datos.put("nota", nota.getNota());
        datos.put("descripcion", nota.getDescripcion());
        return datos;
    }

    private Alumno convertirDesdeFirebase(DataSnapshot snapshot) {
        Alumno alumno = new Alumno();
        alumno.setUid(valorTexto(snapshot, "uid", snapshot.getKey()));
        alumno.setNombre(valorTexto(snapshot, "nombre", null));
        alumno.setApellido(valorTexto(snapshot, "apellido", null));
        alumno.setRut(valorTexto(snapshot, "rut", null));
        alumno.setEmail(valorTexto(snapshot, "email", null));
        alumno.setPassword(valorTexto(snapshot, "password", null));
        alumno.setRol(convertirRol(valorTexto(snapshot, "rol", Rol.ALUMNO.name())));
        alumno.setNotas(leerNotas(snapshot.child("notas"), alumno));
        return alumno;
    }

    private List<Calificacion> leerNotas(DataSnapshot notasSnapshot, Alumno alumno) {
        List<Calificacion> notas = new ArrayList<>();

        for (DataSnapshot grupoONota : notasSnapshot.getChildren()) {
            if (grupoONota.child("asignatura").exists()) {
                agregarNota(notas, grupoONota, null, alumno);
                continue;
            }

            for (DataSnapshot notaSnapshot : grupoONota.getChildren()) {
                agregarNota(notas, notaSnapshot, grupoONota.getKey(), alumno);
            }
        }

        return notas;
    }

    private void agregarNota(List<Calificacion> notas,
            DataSnapshot snapshot,
            String asignaturaDelGrupo,
            Alumno alumno) {
        String asignaturaTexto = valorTexto(snapshot, "asignatura", asignaturaDelGrupo);
        Object notaGuardada = snapshot.child("nota").getValue();

        if (asignaturaTexto == null || !(notaGuardada instanceof Number numero)) {
            return;
        }

        try {
            Calificacion nota = new Calificacion();
            nota.setId(valorTexto(snapshot, "id", snapshot.getKey()));
            nota.setAlumnoId(valorTexto(snapshot, "alumnoId", alumno.getUid()));
            nota.setNombre(valorTexto(snapshot, "nombre", alumno.getNombre()));
            nota.setRut(valorTexto(snapshot, "rut", alumno.getRut()));
            nota.setAsignatura(Asignatura.valueOf(asignaturaTexto));
            nota.setNota(numero.doubleValue());
            nota.setDescripcion(valorTexto(snapshot, "descripcion", null));
            notas.add(nota);
        } catch (IllegalArgumentException e) {

        }
    }

    private String valorTexto(DataSnapshot snapshot, String campo, String valorPorDefecto) {
        String valor = snapshot.child(campo).getValue(String.class);
        return valor != null ? valor : valorPorDefecto;
    }

    private Rol convertirRol(String rol) {
        try {
            return Rol.valueOf(rol);
        } catch (IllegalArgumentException e) {
            return Rol.ALUMNO;
        }
    }
}
