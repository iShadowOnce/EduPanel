package com.edupanel.repository.firebase;

import com.edupanel.model.Profesor;
import com.edupanel.repository.ProfesorRepository;
import com.google.firebase.database.DatabaseReference;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("firebase")
public class ProfesorFirebaseRepository extends FirebaseRepositorySupport implements ProfesorRepository {

    private DatabaseReference profesores() {
        return referencia("profesores");
    }

    @Override
    public void guardar(Profesor profesor) {
        esperar(profesores().child(profesor.getUid()).setValueAsync(profesor));
    }

    @Override
    public Profesor buscarPorId(String id) {
        return buscarPorId(profesores(), id, Profesor.class);
    }

    @Override
    public List<Profesor> listarTodos() {
        return listarTodos(profesores(), Profesor.class);
    }

    @Override
    public void actualizar(Profesor profesorActualizado) {
        Profesor profesorExistente = buscarPorId(profesorActualizado.getUid());

        if (profesorExistente != null) {
            profesorExistente.setNombre(profesorActualizado.getNombre());
            profesorExistente.setApellido(profesorActualizado.getApellido());
            profesorExistente.setRut(profesorActualizado.getRut());
            profesorExistente.setEmail(profesorActualizado.getEmail());
            profesorExistente.setPassword(profesorActualizado.getPassword());
            profesorExistente.setRol(profesorActualizado.getRol());
            profesorExistente.setAsignaturas(profesorActualizado.getAsignaturas());

            guardar(profesorExistente);
        }
    }

    @Override
    public void eliminar(String id) {
        esperar(profesores().child(id).removeValueAsync());
    }
}