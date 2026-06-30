package com.edupanel.repository;

import java.util.List;

import com.edupanel.model.Asignatura;
import com.edupanel.model.Calificacion;

public interface CalificacionRepository {

    void guardar(Calificacion calificacion);

    Calificacion buscarPorId(String alumnoId, String id);

    List<Calificacion> listarPorAlumno(String alumnoId);

    List<Calificacion> listarPorAsignatura(Asignatura asignatura);

    void actualizar(Calificacion calificacion);

    void eliminar(String alumnoId, String id);

}
