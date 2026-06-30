package com.edupanel.service;

import com.edupanel.exception.CursoInvalidoException;
import com.edupanel.model.Curso;
import com.edupanel.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<Curso> listarCursos() {
        return cursoRepository.listarTodos();
    }

    public Curso buscarPorId(String id) {
        return cursoRepository.buscarPorId(id);
    }

    public void guardarCurso(Curso curso) {
        validarCurso(curso);

        if (curso.getId() == null || curso.getId().isBlank()) {
            curso.setId(UUID.randomUUID().toString());
        }

        if (curso.getAlumnosIds() == null) {
            curso.setAlumnosIds(new ArrayList<>());
        }

        cursoRepository.guardar(curso);
    }

    public void eliminarCurso(String id) {
        cursoRepository.eliminar(id);
    }

    private void validarCurso(Curso curso) {
        if (curso.getNombre() == null || curso.getNombre().isBlank()) {
            throw new CursoInvalidoException("El nombre del curso es obligatorio.");
        }
    }

    public void asignarAlumnoACurso(String cursoId, String alumnoId) {
        Curso curso = buscarPorId(cursoId);

        if (curso == null) {
            return;
        }

        // Un alumno solo puede pertenecer a un curso. Al reasignarlo, se eliminan
        for (Curso otroCurso : cursoRepository.listarTodos()) {
            if (Objects.equals(otroCurso.getId(), cursoId)
                    || otroCurso.getAlumnosIds() == null) {
                continue;
            }

            if (otroCurso.getAlumnosIds().removeIf(alumnoId::equals)) {
                cursoRepository.actualizar(otroCurso);
            }
        }

        if (curso.getAlumnosIds() == null) {
            curso.setAlumnosIds(new ArrayList<>());
        }

        curso.getAlumnosIds().removeIf(alumnoId::equals);
        curso.getAlumnosIds().add(alumnoId);
        cursoRepository.actualizar(curso);
    }

    public void quitarAlumnoDeCurso(String cursoId, String alumnoId) {
        Curso curso = buscarPorId(cursoId);

        if (curso != null && curso.getAlumnosIds() != null) {
            if (curso.getAlumnosIds().removeIf(alumnoId::equals)) {
                cursoRepository.actualizar(curso);
            }
        }
    }

    public Curso buscarCursoPorAlumnoId(String alumnoId) {
        for (Curso curso : cursoRepository.listarTodos()) {
            if (curso.getAlumnosIds() != null && curso.getAlumnosIds().contains(alumnoId)) {
                return curso;
            }
        }

        return null;
    }
}
