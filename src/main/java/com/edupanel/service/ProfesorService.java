package com.edupanel.service;

import com.edupanel.model.Asignatura;
import com.edupanel.model.Profesor;
import com.edupanel.model.Rol;
import com.edupanel.repository.ProfesorRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ProfesorService {

    private final ProfesorRepository profesorRepository;

    public ProfesorService(ProfesorRepository profesorRepository) {
        this.profesorRepository = profesorRepository;
    }

    public void guardarProfesor(Profesor profesor) {
        if (profesor.getUid() == null || profesor.getUid().isBlank()) {
            profesor.setUid(UUID.randomUUID().toString());
        }

        profesor.setRol(Rol.PROFESOR);

        if (profesor.getAsignaturas() == null) {
            profesor.setAsignaturas(new ArrayList<>());
        }

        profesorRepository.guardar(profesor);
    }

    public List<Profesor> listarProfesores() {
        return profesorRepository.listarTodos();
    }

    public Profesor buscarPorId(String uid) {
        return profesorRepository.buscarPorId(uid);
    }

    public Profesor buscarPorEmail(String email) {
        if (email == null) {
            return null;
        }

        for (Profesor profesor : profesorRepository.listarTodos()) {
            if (profesor.getEmail() != null && profesor.getEmail().equalsIgnoreCase(email)) {
                return profesor;
            }
        }

        return null;
    }

    public void asignarAsignatura(String profesorId, Asignatura asignatura) {
        Profesor profesor = buscarPorId(profesorId);

        if (profesor != null) {
            if (profesor.getAsignaturas() == null) {
                profesor.setAsignaturas(new ArrayList<>());
            }

            if (!profesor.getAsignaturas().contains(asignatura)) {
                profesor.getAsignaturas().add(asignatura);
            }

            profesorRepository.actualizar(profesor);
        }
    }

    public void quitarAsignatura(String profesorId, Asignatura asignatura) {
        Profesor profesor = buscarPorId(profesorId);

        if (profesor != null && profesor.getAsignaturas() != null) {
            profesor.getAsignaturas().remove(asignatura);
            profesorRepository.actualizar(profesor);
        }
    }
}
