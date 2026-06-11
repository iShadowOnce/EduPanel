package com.edupanel.repository;

import java.util.List;

import com.edupanel.model.Anuncio;
import com.edupanel.model.Asignatura;

public interface AnuncioRepository {

    void guardar(Anuncio anuncio);

    List<Anuncio> listarTodos();

    List<Anuncio> listarPorAsignatura(Asignatura asignatura);

    void eliminar(String id);
    
}
