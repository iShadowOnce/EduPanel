package com.edupanel.repository.memoria;

import com.edupanel.model.Anuncio;
import com.edupanel.model.Asignatura;
import com.edupanel.repository.AnuncioRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("!firebase")
public class AnuncioRepositoryMemoria implements AnuncioRepository {

    /* 
    NOTA JOACO: otro repositorio temporal en memoria.
    Reemplazar por AnuncioFirebaseRepository usando Realtime Database.
    */
    private List<Anuncio> anuncios = new ArrayList<>();

    @Override
    public void guardar(Anuncio anuncio) {
        anuncios.add(anuncio);
    }

    @Override
    public Anuncio buscarPorId(String id) {
        for (Anuncio anuncio : anuncios) {
            if (anuncio.getId().equals(id)) {
                return anuncio;
            }
        }

        return null;
    }

    @Override
    public List<Anuncio> listarTodos() {
        return anuncios;
    }

    @Override
    public List<Anuncio> listarPorAsignatura(Asignatura asignatura) {
        List<Anuncio> anunciosFiltrados = new ArrayList<>();

        for (Anuncio anuncio : anuncios) {
            if (anuncio.getAsignatura() == asignatura) {
                anunciosFiltrados.add(anuncio);
            }
        }

        return anunciosFiltrados;
    }

    @Override
    public void actualizar(Anuncio anuncioActualizado) {
        Anuncio anuncioExistente = buscarPorId(anuncioActualizado.getId());

        if (anuncioExistente != null) {
            anuncioExistente.setTitulo(anuncioActualizado.getTitulo());
            anuncioExistente.setMensaje(anuncioActualizado.getMensaje());
            anuncioExistente.setAsignatura(anuncioActualizado.getAsignatura());
            anuncioExistente.setProfesorId(anuncioActualizado.getProfesorId());
            anuncioExistente.setProfesorNombre(anuncioActualizado.getProfesorNombre());
            anuncioExistente.setFechaPublicacion(anuncioActualizado.getFechaPublicacion());
        }
    }

    @Override
    public void eliminar(String id) {
        anuncios.removeIf(anuncio -> anuncio.getId().equals(id));
    }

    @Override
    public List<Anuncio> listarPorProfesorId(String profesorId) {
        List<Anuncio> anunciosFiltrados = new ArrayList<>();

        for (Anuncio anuncio : anuncios) {
            if (anuncio.getProfesorId().equals(profesorId)) {
                anunciosFiltrados.add(anuncio);
            }
        }

        return anunciosFiltrados;
    }
}
