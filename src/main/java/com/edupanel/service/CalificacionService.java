package com.edupanel.service;

import com.edupanel.exception.CalificacionInvalidaException;
import com.edupanel.model.Alumno;
import com.edupanel.model.Asignatura;
import com.edupanel.model.Calificacion;
import com.edupanel.repository.AlumnoRepository;
import com.edupanel.repository.CalificacionRepository;
import java.util.List;
import java.util.Objects;
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
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        List<Calificacion> calificaciones = calificacionRepository.listarPorAlumno(alumnoId);

        completarDatosAlumno(calificaciones, alumno);
        sincronizarNotasAlumno(alumno, calificaciones);
        return calificaciones;
    }

    public List<Calificacion> listarPorAsignatura(Asignatura asignatura) {
        if (asignatura == null) {
            throw new CalificacionInvalidaException("Debe seleccionar una asignatura.");
        }
        return calificacionRepository.listarPorAsignatura(asignatura);
    }

    public Calificacion buscarPorId(String alumnoId, String notaId) {
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        validarIdentificador(notaId);
        Calificacion calificacion = calificacionRepository.buscarPorId(alumnoId, notaId);

        if (calificacion != null) {
            completarDatosAlumno(calificacion, alumno);
        }

        return calificacion;
    }

    public void guardar(String alumnoId, Calificacion calificacion) {
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        validarCalificacion(calificacion);

        calificacion.setId(UUID.randomUUID().toString());
        calificacion.setAlumnoId(alumnoId);
        calificacion.setNombre(alumno.getNombre());
        calificacion.setRut(alumno.getRut());
        calificacion.setDescripcion(calificacion.getDescripcion().trim());
        calificacionRepository.guardar(calificacion);
        sincronizarNotasAlumno(alumno, calificacionRepository.listarPorAlumno(alumnoId));
    }

    public void actualizar(String alumnoId, String notaId, Calificacion datosActualizados) {
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        validarIdentificador(notaId);
        validarCalificacion(datosActualizados);

        Calificacion existente = calificacionRepository.buscarPorId(alumnoId, notaId);
        if (existente == null) {
            throw new CalificacionInvalidaException("La nota indicada no existe.");
        }

        existente.setAsignatura(datosActualizados.getAsignatura());
        existente.setNota(datosActualizados.getNota());
        existente.setDescripcion(datosActualizados.getDescripcion().trim());
        existente.setNombre(alumno.getNombre());
        existente.setRut(alumno.getRut());
        calificacionRepository.actualizar(existente);
        sincronizarNotasAlumno(alumno, calificacionRepository.listarPorAlumno(alumnoId));
    }

    public void eliminar(String alumnoId, String notaId) {
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        validarIdentificador(notaId);

        if (calificacionRepository.buscarPorId(alumnoId, notaId) == null) {
            throw new CalificacionInvalidaException("La nota indicada no existe.");
        }
        calificacionRepository.eliminar(alumnoId, notaId);
        sincronizarNotasAlumno(alumno, calificacionRepository.listarPorAlumno(alumnoId));
    }

    private Alumno obtenerAlumnoExistente(String alumnoId) {
        Alumno alumno = alumnoId == null || alumnoId.isBlank()
                ? null
                : alumnoRepository.buscarPorId(alumnoId);

        if (alumno == null) {
            throw new CalificacionInvalidaException("El alumno indicado no existe.");
        }

        return alumno;
    }

    private void completarDatosAlumno(List<Calificacion> calificaciones, Alumno alumno) {
        for (Calificacion calificacion : calificaciones) {
            completarDatosAlumno(calificacion, alumno);
        }
    }

    private void completarDatosAlumno(Calificacion calificacion, Alumno alumno) {
        boolean requiereActualizacion = !Objects.equals(calificacion.getNombre(), alumno.getNombre())
                || !Objects.equals(calificacion.getRut(), alumno.getRut());

        calificacion.setNombre(alumno.getNombre());
        calificacion.setRut(alumno.getRut());

        if (requiereActualizacion) {
            calificacionRepository.actualizar(calificacion);
        }
    }

    private void sincronizarNotasAlumno(Alumno alumno, List<Calificacion> calificaciones) {
        alumno.setNotas(calificaciones);
        alumnoRepository.actualizar(alumno);
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
