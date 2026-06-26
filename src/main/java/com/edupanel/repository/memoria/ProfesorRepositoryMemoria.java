package com.edupanel.repository.memoria;

import com.edupanel.model.Profesor;
import com.edupanel.repository.ProfesorRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("!firebase")
public class ProfesorRepositoryMemoria implements ProfesorRepository {

    /*
    NOTA JOACO: repositorio temporal en memoria.
    Reemplazar por ProfesorFirebaseRepository usando Realtime Database.
   */

    private List<Profesor> profesores = new ArrayList<>();

    @Override
    public void guardar(Profesor profesor) {
        profesores.add(profesor);
    }

    @Override
    public Profesor buscarPorId(String id) {
        for (Profesor profesor : profesores) {
            if (profesor.getUid().equals(id)) {
                return profesor;
            }
        }

        return null;
    }

    @Override
    public List<Profesor> listarTodos() {
        return profesores;
    }

    @Override
    public void actualizar(Profesor profesorActualizado) {
        Profesor profesorExistente = buscarPorId(profesorActualizado.getUid());

        if (profesorExistente != null) {
            profesorExistente.setNombre(profesorActualizado.getNombre());
            profesorExistente.setApellido(profesorActualizado.getApellido());
            profesorExistente.setRut(profesorActualizado.getRut());
            profesorExistente.setEmail(profesorActualizado.getEmail());
            profesorExistente.setPassword(profesorActualizado.getPassword());
            profesorExistente.setRol(profesorActualizado.getRol());
            profesorExistente.setAsignaturas(profesorActualizado.getAsignaturas());
        }
    }

    @Override
    public void eliminar(String id) {
        profesores.removeIf(profesor -> profesor.getUid().equals(id));
    }
}
