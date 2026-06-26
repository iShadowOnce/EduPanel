package com.edupanel.repository.firebase;

import com.edupanel.model.Asignatura;
import com.edupanel.model.Calificacion;
import com.edupanel.repository.CalificacionRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.List;
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
                .setValueAsync(calificacion));
    }

    @Override
    public List<Calificacion> listarPorAlumno(String alumnoId) {
        DataSnapshot snapshot = leer(calificaciones().child(alumnoId));
        List<Calificacion> calificacionesAlumno = new ArrayList<>();

        for (DataSnapshot hijo : snapshot.getChildren()) {
            Calificacion calificacion = hijo.getValue(Calificacion.class);

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
                Calificacion calificacion = hijo.getValue(Calificacion.class);

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
                .setValueAsync(calificacionActualizada));
    }

    @Override
    public void eliminar(String id) {
        DataSnapshot snapshot = leer(calificaciones());

        for (DataSnapshot porAlumno : snapshot.getChildren()) {
            if (porAlumno.hasChild(id)) {
                esperar(calificaciones().child(porAlumno.getKey()).child(id).removeValueAsync());
                return;
            }
        }
    }
}