package com.edupanel.service;

import com.edupanel.exception.AnuncioInvalidoException;
import com.edupanel.model.Anuncio;
import com.edupanel.model.Asignatura;
import com.edupanel.repository.AnuncioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;

    public AnuncioService(AnuncioRepository anuncioRepository) {
        this.anuncioRepository = anuncioRepository;
    }

    public List<Anuncio> listarAnuncios() {
        return anuncioRepository.listarTodos();
    }

    public Anuncio buscarPorId(String id) {
        return anuncioRepository.buscarPorId(id);
    }

    public void guardarAnuncio(Anuncio anuncio, String profesorId, String profesorNombre) {
        validarAnuncio(anuncio);

        anuncio.setId(UUID.randomUUID().toString());
        anuncio.setProfesorId(profesorId);
        anuncio.setProfesorNombre(profesorNombre);
        anuncio.setFechaPublicacion(LocalDateTime.now());

        anuncioRepository.guardar(anuncio);
    }

    public void actualizarAnuncio(String id, Anuncio datosActualizados) {
        validarAnuncio(datosActualizados);

        Anuncio anuncioExistente = buscarPorId(id);

        if (anuncioExistente != null) {
            anuncioExistente.setTitulo(datosActualizados.getTitulo());
            anuncioExistente.setMensaje(datosActualizados.getMensaje());
            anuncioExistente.setAsignatura(datosActualizados.getAsignatura());

            anuncioRepository.actualizar(anuncioExistente);
        }
    }

    public void eliminarAnuncio(String id) {
        anuncioRepository.eliminar(id);
    }

    public List<Anuncio> listarPorAsignatura(Asignatura asignatura) {
        return anuncioRepository.listarPorAsignatura(asignatura);
    }

    public List<Anuncio> listarPorProfesor(String profesorId) {
        return anuncioRepository.listarPorProfesorId(profesorId);
    }

    private void validarAnuncio(Anuncio anuncio) {
        if (anuncio.getTitulo() == null || anuncio.getTitulo().isBlank()) {
            throw new AnuncioInvalidoException("El titulo del anuncio es obligatorio.");
        }

        if (anuncio.getMensaje() == null || anuncio.getMensaje().isBlank()) {
            throw new AnuncioInvalidoException("El mensaje del anuncio es obligatorio.");
        }

        if (anuncio.getAsignatura() == null) {
            throw new AnuncioInvalidoException("Debe seleccionar una asignatura.");
        }
    }
}
