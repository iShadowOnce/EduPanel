package com.edupanel.repository;

import com.edupanel.model.Curso;

import java.util.List;

public interface CursoRepository {

    void guardar(Curso curso);

    Curso buscarPorId(String id);

    List<Curso> listarTodos();

    void actualizar(Curso curso);

    void eliminar(String id);
}