package com.edupanel.repository.firebase;

import com.edupanel.model.Curso;
import com.edupanel.repository.CursoRepository;
import com.google.firebase.database.DatabaseReference;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("firebase")
public class CursoFirebaseRepository extends FirebaseRepositorySupport implements CursoRepository {

    private DatabaseReference cursos() {
        return referencia("cursos");
    }

    @Override
    public void guardar(Curso curso) {
        esperar(cursos().child(curso.getId()).setValueAsync(curso));
    }

    @Override
    public Curso buscarPorId(String id) {
        return buscarPorId(cursos(), id, Curso.class);
    }

    @Override
    public List<Curso> listarTodos() {
        return listarTodos(cursos(), Curso.class);
    }

    @Override
    public void actualizar(Curso cursoActualizado) {
        Curso cursoExistente = buscarPorId(cursoActualizado.getId());

        if (cursoExistente != null) {
            cursoExistente.setNombre(cursoActualizado.getNombre());
            cursoExistente.setAlumnosIds(cursoActualizado.getAlumnosIds());

            guardar(cursoExistente);
        }
    }

    @Override
    public void eliminar(String id) {
        esperar(cursos().child(id).removeValueAsync());
    }
}