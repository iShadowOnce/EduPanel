package com.edupanel.service;

import com.edupanel.exception.CalificacionInvalidaException;
import com.edupanel.model.Alumno;
import com.edupanel.model.Asignatura;
import com.edupanel.model.Calificacion;
import com.edupanel.model.Profesor;
import com.edupanel.model.ResumenNotasAsignatura;
import com.edupanel.repository.AlumnoRepository;
import com.edupanel.repository.CalificacionRepository;
import com.edupanel.repository.ProfesorRepository;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final AlumnoRepository alumnoRepository;
    private final ProfesorRepository profesorRepository;

    public CalificacionService(CalificacionRepository calificacionRepository,
            AlumnoRepository alumnoRepository,
            ProfesorRepository profesorRepository) {
        this.calificacionRepository = calificacionRepository;
        this.alumnoRepository = alumnoRepository;
        this.profesorRepository = profesorRepository;
    }

    public List<Calificacion> listarPorAlumno(String alumnoId) {
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        List<Calificacion> calificaciones = calificacionRepository.listarPorAlumno(alumnoId);

        completarDatosAlumno(calificaciones, alumno);
        sincronizarNotasAlumno(alumno, calificaciones);
        return calificaciones;
    }

    public List<ResumenNotasAsignatura> resumirPorAsignatura(List<Calificacion> calificaciones) {
        Map<Asignatura, ResumenNotasAsignatura> resumenes = new EnumMap<>(Asignatura.class);

        for (Asignatura asignatura : Asignatura.values()) {
            resumenes.put(asignatura, new ResumenNotasAsignatura(asignatura));
        }

        if (calificaciones != null) {
            for (Calificacion calificacion : calificaciones) {
                if (calificacion.getAsignatura() != null && resumenes.containsKey(calificacion.getAsignatura())) {
                    resumenes.get(calificacion.getAsignatura()).agregar(calificacion);
                }
            }
        }

        return new ArrayList<>(resumenes.values());
    }

    public List<Calificacion> listarPorAsignatura(Asignatura asignatura) {
        if (asignatura == null) {
            throw new CalificacionInvalidaException("Debe seleccionar una asignatura.");
        }
        return calificacionRepository.listarPorAsignatura(asignatura);
    }

    public Calificacion buscarPorId(String profesorId, String alumnoId, String notaId) {
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        validarIdentificador(notaId);
        Calificacion calificacion = calificacionRepository.buscarPorId(alumnoId, notaId);

        if (calificacion != null) {
            validarProfesorPuedeGestionar(profesorId, calificacion.getAsignatura());
            completarDatosAlumno(calificacion, alumno);
        }

        return calificacion;
    }

    public Calificacion buscarPorIdComoAdmin(String alumnoId, String notaId) {
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        validarIdentificador(notaId);
        Calificacion calificacion = calificacionRepository.buscarPorId(alumnoId, notaId);

        if (calificacion != null) {
            completarDatosAlumno(calificacion, alumno);
        }

        return calificacion;
    }

    public void guardar(String profesorId, String alumnoId, Calificacion calificacion) {
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        validarCalificacion(calificacion);
        validarProfesorPuedeGestionar(profesorId, calificacion.getAsignatura());

        calificacion.setId(UUID.randomUUID().toString());
        calificacion.setAlumnoId(alumnoId);
        calificacion.setNombre(alumno.getNombre());
        calificacion.setRut(alumno.getRut());
        calificacion.setDescripcion(calificacion.getDescripcion().trim());
        calificacionRepository.guardar(calificacion);
        sincronizarNotasAlumno(alumno, calificacionRepository.listarPorAlumno(alumnoId));
    }

    public void guardarComoAdmin(String alumnoId, Calificacion calificacion) {
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

    public void actualizar(String profesorId,
            String alumnoId,
            String notaId,
            Calificacion datosActualizados) {
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        validarIdentificador(notaId);
        validarCalificacion(datosActualizados);

        Calificacion existente = calificacionRepository.buscarPorId(alumnoId, notaId);
        if (existente == null) {
            throw new CalificacionInvalidaException("La nota indicada no existe.");
        }

        validarProfesorPuedeGestionar(profesorId, existente.getAsignatura());
        validarProfesorPuedeGestionar(profesorId, datosActualizados.getAsignatura());

        existente.setAsignatura(datosActualizados.getAsignatura());
        existente.setNota(datosActualizados.getNota());
        existente.setDescripcion(datosActualizados.getDescripcion().trim());
        existente.setNombre(alumno.getNombre());
        existente.setRut(alumno.getRut());
        calificacionRepository.actualizar(existente);
        sincronizarNotasAlumno(alumno, calificacionRepository.listarPorAlumno(alumnoId));
    }

    public void actualizarComoAdmin(String alumnoId,
            String notaId,
            Calificacion datosActualizados) {
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

    public void eliminar(String profesorId, String alumnoId, String notaId) {
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        validarIdentificador(notaId);

        Calificacion existente = calificacionRepository.buscarPorId(alumnoId, notaId);
        if (existente == null) {
            throw new CalificacionInvalidaException("La nota indicada no existe.");
        }

        validarProfesorPuedeGestionar(profesorId, existente.getAsignatura());
        calificacionRepository.eliminar(alumnoId, notaId);
        sincronizarNotasAlumno(alumno, calificacionRepository.listarPorAlumno(alumnoId));
    }

    public void eliminarComoAdmin(String alumnoId, String notaId) {
        Alumno alumno = obtenerAlumnoExistente(alumnoId);
        validarIdentificador(notaId);

        Calificacion existente = calificacionRepository.buscarPorId(alumnoId, notaId);
        if (existente == null) {
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

    private void validarProfesorPuedeGestionar(String profesorId, Asignatura asignatura) {
        Profesor profesor = profesorId == null || profesorId.isBlank()
                ? null
                : profesorRepository.buscarPorId(profesorId);

        if (profesor == null) {
            throw new CalificacionInvalidaException("El profesor indicado no existe.");
        }

        if (profesor.getAsignaturas() == null || !profesor.getAsignaturas().contains(asignatura)) {
            throw new CalificacionInvalidaException(
                    "No puede modificar una nota de una asignatura que no tiene asignada.");
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
