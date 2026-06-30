package com.edupanel.service;

import com.edupanel.exception.CalificacionInvalidaException;
import com.edupanel.model.Asignatura;
import com.edupanel.model.Calificacion;
import com.edupanel.repository.AlumnoRepository;
import com.edupanel.repository.CalificacionRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final AlumnoRepository alumnoRepository;

    public CalificacionService(CalificacionRepository calificacionRepository,
            AlumnoRepository alumnoRepository) {
        this.calificacionRepository = calificacionRepository;
        this.alumnoRepository = alumnoRepository;
    }

    public List<Calificacion> listarPorAlumno(String alumnoId) {
        validarAlumnoExistente(alumnoId);
        return calificacionRepository.listarPorAlumno(alumnoId);
    }

    public List<Calificacion> listarPorAsignatura(Asignatura asignatura) {
        if (asignatura == null) {
            throw new CalificacionInvalidaException("Debe seleccionar una asignatura.");
        }
        return calificacionRepository.listarPorAsignatura(asignatura);
    }

    public Calificacion buscarPorId(String alumnoId, String notaId) {
        validarAlumnoExistente(alumnoId);
        validarIdentificador(notaId);
        return calificacionRepository.buscarPorId(alumnoId, notaId);
    }

    public void guardar(String alumnoId, Calificacion calificacion) {
        validarAlumnoExistente(alumnoId);
        validarCalificacion(calificacion);

        calificacion.setId(UUID.randomUUID().toString());
        calificacion.setAlumnoId(alumnoId);
        calificacion.setDescripcion(calificacion.getDescripcion().trim());
        calificacionRepository.guardar(calificacion);
    }

    public void actualizar(String alumnoId, String notaId, Calificacion datosActualizados) {
        validarAlumnoExistente(alumnoId);
        validarIdentificador(notaId);
        validarCalificacion(datosActualizados);

        Calificacion existente = calificacionRepository.buscarPorId(alumnoId, notaId);
        if (existente == null) {
            throw new CalificacionInvalidaException("La nota indicada no existe.");
        }

        existente.setAsignatura(datosActualizados.getAsignatura());
        existente.setNota(datosActualizados.getNota());
        existente.setDescripcion(datosActualizados.getDescripcion().trim());
        calificacionRepository.actualizar(existente);
    }

    public void eliminar(String alumnoId, String notaId) {
        validarAlumnoExistente(alumnoId);
        validarIdentificador(notaId);

        if (calificacionRepository.buscarPorId(alumnoId, notaId) == null) {
            throw new CalificacionInvalidaException("La nota indicada no existe.");
        }
        calificacionRepository.eliminar(alumnoId, notaId);
    }

    private void validarAlumnoExistente(String alumnoId) {
        if (alumnoId == null || alumnoId.isBlank() || alumnoRepository.buscarPorId(alumnoId) == null) {
            throw new CalificacionInvalidaException("El alumno indicado no existe.");
        }
    }

    private void validarIdentificador(String notaId) {
        if (notaId == null || notaId.isBlank()) {
            throw new CalificacionInvalidaException("El identificador de la nota es obligatorio.");
        }
    }

    private void validarCalificacion(Calificacion calificacion) {
        if (calificacion == null) {
            throw new CalificacionInvalidaException("Los datos de la nota son obligatorios.");
        }
        if (calificacion.getAsignatura() == null) {
            throw new CalificacionInvalidaException("Debe seleccionar una asignatura.");
        }
        if (!Double.isFinite(calificacion.getNota())
                || calificacion.getNota() < 1.0
                || calificacion.getNota() > 7.0) {
            throw new CalificacionInvalidaException("La nota debe estar entre 1.0 y 7.0.");
        }
        if (calificacion.getDescripcion() == null || calificacion.getDescripcion().isBlank()) {
            throw new CalificacionInvalidaException("La descripcion de la nota es obligatoria.");
        }
    }
}
