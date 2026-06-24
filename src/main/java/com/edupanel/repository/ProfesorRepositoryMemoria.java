package com.edupanel.repository;

import com.edupanel.model.Profesor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProfesorRepositoryMemoria implements ProfesorRepository {

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
