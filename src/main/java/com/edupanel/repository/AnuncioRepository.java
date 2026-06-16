package com.edupanel.repository;

import java.util.List;

import com.edupanel.model.Anuncio;
import com.edupanel.model.Asignatura;

public interface AnuncioRepository {

    void guardar(Anuncio anuncio);

    Anuncio buscarPorId(String id);

    List<Anuncio> listarTodos();

    List<Anuncio> listarPorAsignatura(Asignatura asignatura);

    void actualizar(Anuncio anuncio);

    void eliminar(String id);
}