package com.edupanel.repository.firebase;

import com.edupanel.model.Alumno;
import com.edupanel.repository.AlumnoRepository;
import com.google.firebase.database.DatabaseReference;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("firebase")
public class AlumnoFirebaseRepository extends FirebaseRepositorySupport implements AlumnoRepository {

    private DatabaseReference alumnos() {
        return referencia("alumnos");
    }

    @Override
    public void guardar(Alumno alumno) {
        esperar(alumnos().child(alumno.getUid()).setValueAsync(alumno));
    }

    @Override
    public Alumno buscarPorId(String id) {
        return buscarPorId(alumnos(), id, Alumno.class);
    }

    @Override
    public List<Alumno> listarTodos() {
        return listarTodos(alumnos(), Alumno.class);
    }

    @Override
    public void actualizar(Alumno alumnoActualizado) {
        Alumno alumnoExistente = buscarPorId(alumnoActualizado.getUid());

        if (alumnoExistente != null) {
            alumnoExistente.setNombre(alumnoActualizado.getNombre());
            alumnoExistente.setApellido(alumnoActualizado.getApellido());
            alumnoExistente.setRut(alumnoActualizado.getRut());
            alumnoExistente.setEmail(alumnoActualizado.getEmail());
            alumnoExistente.setPassword(alumnoActualizado.getPassword());
            alumnoExistente.setRol(alumnoActualizado.getRol());
            alumnoExistente.setNotas(alumnoActualizado.getNotas());

            guardar(alumnoExistente);
        }
    }

    @Override
    public void eliminar(String id) {
        esperar(alumnos().child(id).removeValueAsync());
    }
}