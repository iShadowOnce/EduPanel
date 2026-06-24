package com.edupanel.service;

import com.edupanel.exception.AlumnoInvalidoException;
import com.edupanel.exception.CalificacionInvalidaException;
import com.edupanel.model.Alumno;
import com.edupanel.model.Calificacion;
import com.edupanel.model.Rol;
import com.edupanel.repository.AlumnoRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;

    public AlumnoService(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    public List<Alumno> listarAlumnos() {
        return alumnoRepository.listarTodos();
    }

    public void guardarAlumno(Alumno alumno) {
        validarAlumno(alumno);

        if (alumno.getUid() == null || alumno.getUid().isBlank()) {
            alumno.setUid(UUID.randomUUID().toString());
        }

        alumno.setRol(Rol.ALUMNO);

        if (alumno.getNotas() == null) {
            alumno.setNotas(new ArrayList<>());
        }

        alumnoRepository.guardar(alumno);
    }

    public Alumno buscarPorId(String uid) {
        return alumnoRepository.buscarPorId(uid);
    }

    public Alumno buscarPorEmail(String email) {
        if (email == null) {
            return null;
        }

        for (Alumno alumno : alumnoRepository.listarTodos()) {
            if (alumno.getEmail() != null && alumno.getEmail().equalsIgnoreCase(email)) {
                return alumno;
            }
        }

        return null;
    }

    public void agregarCalificacion(String alumnoId, Calificacion calificacion) {
        validarCalificacion(calificacion);

        Alumno alumno = buscarPorId(alumnoId);

        if (alumno != null) {
            if (alumno.getNotas() == null) {
                alumno.setNotas(new ArrayList<>());
            }

            calificacion.setId(UUID.randomUUID().toString());
            calificacion.setAlumnoId(alumnoId);

            alumno.getNotas().add(calificacion);
            alumnoRepository.actualizar(alumno);
        }
    }

    public void eliminarAlumno(String uid) {
        alumnoRepository.eliminar(uid);
    }

    public void actualizarAlumno(String uid, Alumno datosActualizados) {
        validarAlumno(datosActualizados);

        Alumno alumnoExistente = buscarPorId(uid);

        if (alumnoExistente != null) {
            alumnoExistente.setNombre(datosActualizados.getNombre());
            alumnoExistente.setApellido(datosActualizados.getApellido());
            alumnoExistente.setRut(datosActualizados.getRut());
            alumnoExistente.setEmail(datosActualizados.getEmail());

            if (datosActualizados.getPassword() != null && !datosActualizados.getPassword().isBlank()) {
                alumnoExistente.setPassword(datosActualizados.getPassword());
            }

            alumnoRepository.actualizar(alumnoExistente);
        }
    }

    public Calificacion buscarCalificacionPorId(String alumnoId, String notaId) {
        Alumno alumno = buscarPorId(alumnoId);

        if (alumno != null && alumno.getNotas() != null) {
            for (Calificacion calificacion : alumno.getNotas()) {
                if (calificacion.getId().equals(notaId)) {
                    return calificacion;
                }
            }
        }

        return null;
    }

    public void actualizarCalificacion(String alumnoId, String notaId, Calificacion datosActualizados) {
        validarCalificacion(datosActualizados);

        Calificacion calificacionExistente = buscarCalificacionPorId(alumnoId, notaId);

        if (calificacionExistente != null) {
            calificacionExistente.setAsignatura(datosActualizados.getAsignatura());
            calificacionExistente.setNota(datosActualizados.getNota());
            calificacionExistente.setDescripcion(datosActualizados.getDescripcion());

            Alumno alumno = buscarPorId(alumnoId);
            alumnoRepository.actualizar(alumno);
        }
    }

    public void eliminarCalificacion(String alumnoId, String notaId) {
        Alumno alumno = buscarPorId(alumnoId);

        if (alumno != null && alumno.getNotas() != null) {
            alumno.getNotas().removeIf(calificacion -> calificacion.getId().equals(notaId));
            alumnoRepository.actualizar(alumno);
        }
    }

    private void validarAlumno(Alumno alumno) {
        if (alumno.getNombre() == null || alumno.getNombre().isBlank()) {
            throw new AlumnoInvalidoException("El nombre del alumno es obligatorio.");
        }

        if (alumno.getApellido() == null || alumno.getApellido().isBlank()) {
            throw new AlumnoInvalidoException("El apellido del alumno es obligatorio.");
        }

        if (alumno.getRut() == null || alumno.getRut().isBlank()) {
            throw new AlumnoInvalidoException("El RUT del alumno es obligatorio.");
        }

        if (alumno.getEmail() == null || alumno.getEmail().isBlank()) {
            throw new AlumnoInvalidoException("El email del alumno es obligatorio.");
        }
    }

    private void validarCalificacion(Calificacion calificacion) {
        if (calificacion.getAsignatura() == null) {
            throw new CalificacionInvalidaException("Debe seleccionar una asignatura.");
        }

        if (calificacion.getNota() < 1.0 || calificacion.getNota() > 7.0) {
            throw new CalificacionInvalidaException("La nota debe estar entre 1.0 y 7.0.");
        }

        if (calificacion.getDescripcion() == null || calificacion.getDescripcion().isBlank()) {
            throw new CalificacionInvalidaException("La descripcion de la nota es obligatoria.");
        }
    }
}
