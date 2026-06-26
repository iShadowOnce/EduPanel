package com.edupanel.repository.memoria;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import com.edupanel.model.Alumno;
import com.edupanel.repository.AlumnoRepository;

@Repository
@Profile("!firebase")
public class AlumnoRepositoryMemoria implements AlumnoRepository {

    /*
    NOTA JOACO: estos son repositorios temporales en memoria.
    Cuando AlumnoFirebaseRepository funcione, esta clase debe dejar de usarse.
    */
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
            alumnoExistente.setPassword(alumnoActualizado.getPassword());
            alumnoExistente.setRol(alumnoActualizado.getRol());
            alumnoExistente.setNotas(alumnoActualizado.getNotas());
        }
    }

    @Override
    public void eliminar(String id) {
        alumnos.removeIf(alumno -> alumno.getUid().equals(id));
    }

}
