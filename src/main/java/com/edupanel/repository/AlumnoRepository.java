package com.edupanel.repository;

import java.util.List;

import com.edupanel.model.Alumno;

public interface AlumnoRepository {

    void guardar(Alumno alumno);

    Alumno buscarPorId(String id);

    List<Alumno> listarTodos();

    void actualizar(Alumno alumno);

    void eliminar(String id);

}
