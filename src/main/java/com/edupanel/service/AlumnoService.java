package com.edupanel.service;

import com.edupanel.exception.AlumnoInvalidoException;
import com.edupanel.model.Alumno;
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

}
