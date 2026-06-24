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
    private final DatabaseReference alumnos = referencia("alumnos");

    @Override
    public void guardar(Alumno alumno) {
        esperar(alumnos.child(alumno.getUid()).setValueAsync(alumno));
    }

    @Override
    public Alumno buscarPorId(String id) {
        return buscarPorId(alumnos, id, Alumno.class);
    }

    @Override
    public List<Alumno> listarTodos() {
        return listarTodos(alumnos, Alumno.class);
    }

    @Override
    public void actualizar(Alumno alumno) {
        guardar(alumno);
    }

    @Override
    public void eliminar(String id) {
        esperar(alumnos.child(id).removeValueAsync());
    }
}
