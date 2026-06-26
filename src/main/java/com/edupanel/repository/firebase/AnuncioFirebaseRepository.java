package com.edupanel.repository.firebase;

import com.edupanel.model.Anuncio;
import com.edupanel.model.Asignatura;
import com.edupanel.repository.AnuncioRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("firebase")
public class AnuncioFirebaseRepository extends FirebaseRepositorySupport implements AnuncioRepository {

    private DatabaseReference anuncios() {
        return referencia("anuncios");
    }

    @Override
    public void guardar(Anuncio anuncio) {
        esperar(anuncios().child(anuncio.getId()).setValueAsync(convertirParaFirebase(anuncio)));
    }

    @Override
    public Anuncio buscarPorId(String id) {
        DataSnapshot snapshot = leer(anuncios().child(id));

        if (!snapshot.exists()) {
            return null;
        }

        return convertirDesdeFirebase(snapshot);
    }

    @Override
    public List<Anuncio> listarTodos() {
        DataSnapshot snapshot = leer(anuncios());
        List<Anuncio> anuncios = new ArrayList<>();

        for (DataSnapshot hijo : snapshot.getChildren()) {
            Anuncio anuncio = convertirDesdeFirebase(hijo);

            if (anuncio != null) {
                anuncios.add(anuncio);
            }
        }

        return anuncios;
    }

    @Override
    public List<Anuncio> listarPorAsignatura(Asignatura asignatura) {
        List<Anuncio> anunciosFiltrados = new ArrayList<>();

        for (Anuncio anuncio : listarTodos()) {
            if (anuncio.getAsignatura() == asignatura) {
                anunciosFiltrados.add(anuncio);
            }
        }

        return anunciosFiltrados;
    }

    @Override
    public List<Anuncio> listarPorProfesorId(String profesorId) {
        List<Anuncio> anunciosFiltrados = new ArrayList<>();

        for (Anuncio anuncio : listarTodos()) {
            if (anuncio.getProfesorId().equals(profesorId)) {
                anunciosFiltrados.add(anuncio);
            }
        }

        return anunciosFiltrados;
    }

    @Override
    public void actualizar(Anuncio anuncioActualizado) {
        Anuncio anuncioExistente = buscarPorId(anuncioActualizado.getId());

        if (anuncioExistente != null) {
            anuncioExistente.setTitulo(anuncioActualizado.getTitulo());
            anuncioExistente.setMensaje(anuncioActualizado.getMensaje());
            anuncioExistente.setAsignatura(anuncioActualizado.getAsignatura());
            anuncioExistente.setProfesorId(anuncioActualizado.getProfesorId());
            anuncioExistente.setProfesorNombre(anuncioActualizado.getProfesorNombre());
            anuncioExistente.setFechaPublicacion(anuncioActualizado.getFechaPublicacion());

            guardar(anuncioExistente);
        }
    }

    @Override
    public void eliminar(String id) {
        esperar(anuncios().child(id).removeValueAsync());
    }

    private Map<String, Object> convertirParaFirebase(Anuncio anuncio) {
        Map<String, Object> datos = new HashMap<>();
        datos.put("id", anuncio.getId());
        datos.put("titulo", anuncio.getTitulo());
        datos.put("mensaje", anuncio.getMensaje());
        datos.put("asignatura", anuncio.getAsignatura() != null ? anuncio.getAsignatura().name() : null);
        datos.put("profesorId", anuncio.getProfesorId());
        datos.put("profesorNombre", anuncio.getProfesorNombre());
        datos.put("fechaPublicacion", anuncio.getFechaPublicacion() != null
                ? anuncio.getFechaPublicacion().toString()
                : null);
        return datos;
    }

    private Anuncio convertirDesdeFirebase(DataSnapshot snapshot) {
        Anuncio anuncio = new Anuncio();
        anuncio.setId(snapshot.child("id").getValue(String.class));
        anuncio.setTitulo(snapshot.child("titulo").getValue(String.class));
        anuncio.setMensaje(snapshot.child("mensaje").getValue(String.class));
        anuncio.setProfesorId(snapshot.child("profesorId").getValue(String.class));
        anuncio.setProfesorNombre(snapshot.child("profesorNombre").getValue(String.class));

        String asignaturaStr = snapshot.child("asignatura").getValue(String.class);
        if (asignaturaStr != null) {
            anuncio.setAsignatura(Asignatura.valueOf(asignaturaStr));
        }

        String fechaStr = snapshot.child("fechaPublicacion").getValue(String.class);
        if (fechaStr != null) {
            anuncio.setFechaPublicacion(LocalDateTime.parse(fechaStr));
        }

        return anuncio;
    }
}