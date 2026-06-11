package com.edupanel.repository;

import java.util.List;

import com.edupanel.model.Profesor;

public interface ProfesorRepository {

    void guardar(Profesor profesor);

    Profesor buscarPorId(String id);

    List<Profesor> listarTodos();

    void actualizar(Profesor profesor);

    void eliminar(String id);

}
