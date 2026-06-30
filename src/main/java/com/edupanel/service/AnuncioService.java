package com.edupanel.service;

import com.edupanel.exception.AnuncioInvalidoException;
import com.edupanel.model.Anuncio;
import com.edupanel.model.Asignatura;
import com.edupanel.model.Profesor;
import com.edupanel.repository.AnuncioRepository;
import com.edupanel.repository.ProfesorRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AnuncioService {

    private final AnuncioRepository anuncioRepository;
    private final ProfesorRepository profesorRepository;

    public AnuncioService(AnuncioRepository anuncioRepository, ProfesorRepository profesorRepository) {
        this.anuncioRepository = anuncioRepository;
        this.profesorRepository = profesorRepository;
    }

    public List<Anuncio> listarAnuncios() {
        return anuncioRepository.listarTodos();
    }

    public Anuncio buscarPorId(String id) {
        return anuncioRepository.buscarPorId(id);
    }

    public void guardarAnuncio(Anuncio anuncio, String profesorId, String profesorNombre) {
        validarAnuncio(anuncio);
        validarProfesorPuedeGestionar(profesorId, anuncio.getAsignatura());

        anuncio.setId(UUID.randomUUID().toString());
        anuncio.setProfesorId(profesorId);
        anuncio.setProfesorNombre(profesorNombre);
        anuncio.setFechaPublicacion(LocalDateTime.now());

        anuncioRepository.guardar(anuncio);
    }

    public void actualizarAnuncio(String profesorId, String id, Anuncio datosActualizados) {
        validarAnuncio(datosActualizados);

        Anuncio anuncioExistente = buscarPorId(id);

        if (anuncioExistente != null) {
            validarProfesorEsAutor(profesorId, anuncioExistente);
            validarProfesorPuedeGestionar(profesorId, datosActualizados.getAsignatura());

            anuncioExistente.setTitulo(datosActualizados.getTitulo());
            anuncioExistente.setMensaje(datosActualizados.getMensaje());
            anuncioExistente.setAsignatura(datosActualizados.getAsignatura());

            anuncioRepository.actualizar(anuncioExistente);
        }
    }

    public void eliminarAnuncio(String profesorId, String id) {
        Anuncio anuncioExistente = buscarPorId(id);

        if (anuncioExistente != null) {
            validarProfesorEsAutor(profesorId, anuncioExistente);
            anuncioRepository.eliminar(id);
        }
    }

    public void guardarAnuncioComoAdmin(Anuncio anuncio, String administradorId, String administradorNombre) {
        validarAnuncio(anuncio);

        anuncio.setId(UUID.randomUUID().toString());
        anuncio.setProfesorId(administradorId);
        anuncio.setProfesorNombre(administradorNombre);
        anuncio.setFechaPublicacion(LocalDateTime.now());

        anuncioRepository.guardar(anuncio);
    }

    public void actualizarAnuncioComoAdmin(String id, Anuncio datosActualizados) {
        validarAnuncio(datosActualizados);

        Anuncio anuncioExistente = buscarPorId(id);

        if (anuncioExistente != null) {
            anuncioExistente.setTitulo(datosActualizados.getTitulo());
            anuncioExistente.setMensaje(datosActualizados.getMensaje());
            anuncioExistente.setAsignatura(datosActualizados.getAsignatura());

            anuncioRepository.actualizar(anuncioExistente);
        }
    }

    public void eliminarAnuncioComoAdmin(String id) {
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

    private void validarProfesorPuedeGestionar(String profesorId, Asignatura asignatura) {
        Profesor profesor = profesorId == null || profesorId.isBlank()
                ? null
                : profesorRepository.buscarPorId(profesorId);

        if (profesor == null) {
            throw new AnuncioInvalidoException("El profesor indicado no existe.");
        }

        if (profesor.getAsignaturas() == null || !profesor.getAsignaturas().contains(asignatura)) {
            throw new AnuncioInvalidoException(
                    "No puede publicar anuncios de una asignatura que no tiene asignada.");
        }
    }

    private void validarProfesorEsAutor(String profesorId, Anuncio anuncio) {
        if (!Objects.equals(profesorId, anuncio.getProfesorId())) {
            throw new AnuncioInvalidoException("Solo puede modificar sus propios anuncios.");
        }
    }
}
