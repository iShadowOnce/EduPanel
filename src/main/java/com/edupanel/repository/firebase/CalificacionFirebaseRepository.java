package com.edupanel.repository.firebase;

import com.edupanel.model.Asignatura;
import com.edupanel.model.Calificacion;
import com.edupanel.repository.CalificacionRepository;
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
public class CalificacionFirebaseRepository extends FirebaseRepositorySupport implements CalificacionRepository {

    // se guardan como calificaciones/{alumnoId}/{calificacionId}
    private DatabaseReference calificaciones() {
        return referencia("calificaciones");
    }

    @Override
    public void guardar(Calificacion calificacion) {
        esperar(calificaciones()
                .child(calificacion.getAlumnoId())
                .child(calificacion.getId())
                .setValueAsync(convertirParaFirebase(calificacion)));
    }

    @Override
    public Calificacion buscarPorId(String alumnoId, String id) {
        DataSnapshot snapshot = leer(calificaciones().child(alumnoId).child(id));
        return snapshot.exists() ? convertirDesdeFirebase(snapshot) : null;
    }

    @Override
    public List<Calificacion> listarPorAlumno(String alumnoId) {
        DataSnapshot snapshot = leer(calificaciones().child(alumnoId));
        List<Calificacion> calificacionesAlumno = new ArrayList<>();

        for (DataSnapshot hijo : snapshot.getChildren()) {
            Calificacion calificacion = convertirDesdeFirebase(hijo);

            if (calificacion != null) {
                calificacionesAlumno.add(calificacion);
            }
        }

        return calificacionesAlumno;
    }

    @Override
    public List<Calificacion> listarPorAsignatura(Asignatura asignatura) {
        DataSnapshot snapshot = leer(calificaciones());
        List<Calificacion> calificacionesFiltradas = new ArrayList<>();

        for (DataSnapshot porAlumno : snapshot.getChildren()) {
            for (DataSnapshot hijo : porAlumno.getChildren()) {
                Calificacion calificacion = convertirDesdeFirebase(hijo);

                if (calificacion != null && calificacion.getAsignatura() == asignatura) {
                    calificacionesFiltradas.add(calificacion);
                }
            }
        }

        return calificacionesFiltradas;
    }

    @Override
    public void actualizar(Calificacion calificacionActualizada) {
        esperar(calificaciones()
                .child(calificacionActualizada.getAlumnoId())
                .child(calificacionActualizada.getId())
                .setValueAsync(convertirParaFirebase(calificacionActualizada)));
    }

    @Override
    public void eliminar(String alumnoId, String id) {
        esperar(calificaciones().child(alumnoId).child(id).removeValueAsync());
    }

    private Map<String, Object> convertirParaFirebase(Calificacion calificacion) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("id", calificacion.getId());
        datos.put("alumnoId", calificacion.getAlumnoId());
        datos.put("asignatura", calificacion.getAsignatura().name());
        datos.put("nota", calificacion.getNota());
        datos.put("descripcion", calificacion.getDescripcion());
        return datos;
    }

    private Calificacion convertirDesdeFirebase(DataSnapshot snapshot) {
        String asignatura = snapshot.child("asignatura").getValue(String.class);
        Double nota = snapshot.child("nota").getValue(Double.class);

        if (asignatura == null || nota == null) {
            return null;
        }

        try {
            Calificacion calificacion = new Calificacion();
            calificacion.setId(snapshot.child("id").getValue(String.class));
            calificacion.setAlumnoId(snapshot.child("alumnoId").getValue(String.class));
            calificacion.setAsignatura(Asignatura.valueOf(asignatura));
            calificacion.setNota(nota);
            calificacion.setDescripcion(snapshot.child("descripcion").getValue(String.class));
            return calificacion;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
