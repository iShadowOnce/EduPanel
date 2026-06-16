package com.edupanel.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.edupanel.model.Alumno;

@Repository
public class AlumnoRepositoryMemoria implements AlumnoRepository {

    private List<Alumno> alumnos = new ArrayList<>();

    @Override
    public void guardar(Alumno alumno) {
        alumnos.add(alumno);
    }

    @Override
    public Alumno buscarPorId(String id) {
        for (Alumno alumno : alumnos) {
            if (alumno.getUid().equals(id)) {
                return alumno;
            }
        }

        return null;
    }

    @Override
    public List<Alumno> listarTodos() {
        return alumnos;
    }

    @Override
    public void actualizar(Alumno alumnoActualizado) {
        Alumno alumnoExistente = buscarPorId(alumnoActualizado.getUid());

        if (alumnoExistente != null) {
            alumnoExistente.setNombre(alumnoActualizado.getNombre());
            alumnoExistente.setApellido(alumnoActualizado.getApellido());
            alumnoExistente.setRut(alumnoActualizado.getRut());
            alumnoExistente.setEmail(alumnoActualizado.getEmail());
            alumnoExistente.setNotas(alumnoActualizado.getNotas());
            alumnoExistente.setRol(alumnoActualizado.getRol());
        }
    }

    @Override
    public void eliminar(String id) {
        alumnos.removeIf(alumno -> alumno.getUid().equals(id));
    }

}
