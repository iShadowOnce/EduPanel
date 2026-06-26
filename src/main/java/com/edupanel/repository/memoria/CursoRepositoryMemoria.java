package com.edupanel.repository.memoria;

import com.edupanel.model.Curso;
import com.edupanel.repository.CursoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;

@Repository
@Profile("!firebase")
public class CursoRepositoryMemoria implements CursoRepository {

    /*
    NOTA JOACO: otro repositorio temporal en memoria.
    Reemplazar por CursoFirebaseRepository usando Realtime Database.
    */
    private List<Curso> cursos = new ArrayList<>();

    @Override
    public void guardar(Curso curso) {
        cursos.add(curso);
    }

    @Override
    public Curso buscarPorId(String id) {
        for (Curso curso : cursos) {
            if (curso.getId().equals(id)) {
                return curso;
            }
        }

        return null;
    }

    @Override
    public List<Curso> listarTodos() {
        return cursos;
    }

    @Override
    public void actualizar(Curso cursoActualizado) {
        Curso cursoExistente = buscarPorId(cursoActualizado.getId());

        if (cursoExistente != null) {
            cursoExistente.setNombre(cursoActualizado.getNombre());
            cursoExistente.setAlumnosIds(cursoActualizado.getAlumnosIds());
        }
    }

    @Override
    public void eliminar(String id) {
        cursos.removeIf(curso -> curso.getId().equals(id));
    }
}
