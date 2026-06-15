package com.edupanel.service;

import com.edupanel.model.Anuncio;
import com.edupanel.model.Asignatura;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AnuncioService {

    private List<Anuncio> anuncios = new ArrayList<>();

    public AnuncioService() {
        Anuncio anuncio1 = new Anuncio();
        anuncio1.setId("1");
        anuncio1.setTitulo("Bienvenidos a EduPanel");
        anuncio1.setMensaje("Este es el primer anuncio del sistema.");
        anuncio1.setAsignatura(Asignatura.MATEMATICAS);
        anuncio1.setProfesorId("profesor-demo");
        anuncio1.setFechaPublicacion(LocalDateTime.now());

        anuncios.add(anuncio1);
    }

    public List<Anuncio> listarAnuncios() {
        return anuncios;
    }

    public void guardarAnuncio(Anuncio anuncio) {
        anuncio.setId(String.valueOf(anuncios.size() + 1));
        anuncio.setProfesorId("profesor-demo");
        anuncio.setFechaPublicacion(LocalDateTime.now());

        anuncios.add(anuncio);
    }

    public List<Anuncio> listarPorAsignatura(Asignatura asignatura) {
        List<Anuncio> anunciosFiltrados = new ArrayList<>();

        for (Anuncio anuncio : anuncios) {
            if (anuncio.getAsignatura() == asignatura) {
                anunciosFiltrados.add(anuncio);
            }
        }

        return anunciosFiltrados;
    }

    public void eliminarAnuncio(String id) {
        anuncios.removeIf(anuncio -> anuncio.getId().equals(id));
    }

    public Anuncio buscarPorId(String id) {
        for (Anuncio anuncio : anuncios) {
            if (anuncio.getId().equals(id)) {
                return anuncio;
            }
        }

        return null;
    }

    public void actualizarAnuncio(String id, Anuncio datosActualizados) {
        Anuncio anuncioExistente = buscarPorId(id);

        if (anuncioExistente != null) {
            anuncioExistente.setTitulo(datosActualizados.getTitulo());
            anuncioExistente.setMensaje(datosActualizados.getMensaje());
            anuncioExistente.setAsignatura(datosActualizados.getAsignatura());
        }
    }
}