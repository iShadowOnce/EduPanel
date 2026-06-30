package com.edupanel.service;

import com.edupanel.exception.CursoInvalidoException;
import com.edupanel.model.Curso;
import com.edupanel.model.CursoEscolar;
import com.edupanel.repository.CursoRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    public List<Curso> listarCursos() {
        List<Curso> cursos = new ArrayList<>();

        for (CursoEscolar cursoEscolar : CursoEscolar.values()) {
            cursos.add(buscarOCrearCursoFijo(cursoEscolar));
        }

        return cursos;
    }

    public Curso buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        Curso curso = cursoRepository.buscarPorId(id);
        if (curso != null) {
            normalizarAlumnos(curso);
            return curso;
        }

        CursoEscolar cursoEscolar = CursoEscolar.buscarPorId(id);
        return cursoEscolar == null ? null : crearCursoFijo(cursoEscolar);
    }

    public void guardarCurso(Curso curso) {
        validarCursoFijo(curso);

        CursoEscolar cursoEscolar = CursoEscolar.buscarPorId(curso.getId());
        Curso cursoGuardado = buscarOCrearCursoFijo(cursoEscolar);
        cursoGuardado.setAlumnosIds(curso.getAlumnosIds());
        normalizarAlumnos(cursoGuardado);

        guardarOActualizar(cursoGuardado);
    }

    public void eliminarCurso(String id) {
        throw new CursoInvalidoException("Los cursos oficiales no se eliminan.");
    }

    private void validarCursoFijo(Curso curso) {
        if (curso == null || curso.getId() == null || curso.getId().isBlank()) {
            throw new CursoInvalidoException("Debe seleccionar un curso oficial.");
        }

        if (CursoEscolar.buscarPorId(curso.getId()) == null) {
            throw new CursoInvalidoException("Solo se permiten cursos oficiales de 1 Basico a 4 Medio.");
        }
    }

    public void asignarAlumnoACurso(String cursoId, String alumnoId) {
        Curso curso = buscarPorId(cursoId);

        if (curso == null) {
            return;
        }

        // Un alumno solo puede pertenecer a un curso.
        for (Curso otroCurso : listarCursosPersistidos()) {
            if (Objects.equals(otroCurso.getId(), cursoId)
                    || otroCurso.getAlumnosIds() == null) {
                continue;
            }

            if (otroCurso.getAlumnosIds().removeIf(alumnoId::equals)) {
                guardarOActualizar(otroCurso);
            }
        }

        if (curso.getAlumnosIds() == null) {
            curso.setAlumnosIds(new ArrayList<>());
        }

        curso.getAlumnosIds().removeIf(alumnoId::equals);
        curso.getAlumnosIds().add(alumnoId);
        guardarOActualizar(curso);
    }

    public void quitarAlumnoDeCurso(String cursoId, String alumnoId) {
        Curso curso = buscarPorId(cursoId);

        if (curso != null && curso.getAlumnosIds() != null) {
            if (curso.getAlumnosIds().removeIf(alumnoId::equals)) {
                guardarOActualizar(curso);
            }
        }
    }

    public Curso buscarCursoPorAlumnoId(String alumnoId) {
        for (Curso curso : listarCursos()) {
            if (curso.getAlumnosIds() != null && curso.getAlumnosIds().contains(alumnoId)) {
                return curso;
            }
        }

        for (Curso curso : listarCursosPersistidos()) {
            if (curso.getAlumnosIds() != null && curso.getAlumnosIds().contains(alumnoId)) {
                return curso;
            }
        }

        return null;
    }

    private Curso buscarOCrearCursoFijo(CursoEscolar cursoEscolar) {
        Curso curso = cursoRepository.buscarPorId(cursoEscolar.getId());

        if (curso == null) {
            return crearCursoFijo(cursoEscolar);
        }

        curso.setNombre(cursoEscolar.getNombre());
        normalizarAlumnos(curso);
        return curso;
    }

    private Curso crearCursoFijo(CursoEscolar cursoEscolar) {
        Curso curso = new Curso();
        curso.setId(cursoEscolar.getId());
        curso.setNombre(cursoEscolar.getNombre());
        curso.setAlumnosIds(new ArrayList<>());
        return curso;
    }

    private void normalizarAlumnos(Curso curso) {
        if (curso.getAlumnosIds() == null) {
            curso.setAlumnosIds(new ArrayList<>());
        }
    }

    private List<Curso> listarCursosPersistidos() {
        List<Curso> cursos = cursoRepository.listarTodos();
        return cursos == null ? new ArrayList<>() : cursos;
    }

    private void guardarOActualizar(Curso curso) {
        if (cursoRepository.buscarPorId(curso.getId()) == null) {
            cursoRepository.guardar(curso);
            return;
        }

        cursoRepository.actualizar(curso);
    }
}
