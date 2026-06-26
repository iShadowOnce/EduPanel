package com.edupanel.repository.firebase;

import com.edupanel.model.Alumno;
import com.edupanel.model.Profesor;
import com.edupanel.model.Rol;
import com.edupanel.model.Usuario;
import com.edupanel.model.UsuarioPendiente;
import com.edupanel.repository.UsuarioRepository;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import java.util.ArrayList;
import java.util.List;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("firebase")
public class UsuarioFirebaseRepository extends FirebaseRepositorySupport implements UsuarioRepository {

    private DatabaseReference usuarios() {
        return referencia("usuarios");
    }

    @Override
    public void guardar(Usuario usuario) {
        esperar(usuarios().child(usuario.getUid()).setValueAsync(usuario));
    }

    @Override
    public Usuario buscarPorId(String id) {
        DataSnapshot snapshot = leer(usuarios().child(id));
        if (!snapshot.exists()) {
            return null;
        }

        return convertirUsuario(snapshot);
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        if (email == null) {
            return null;
        }

        for (Usuario usuario : listarTodos()) {
            if (usuario.getEmail() != null && usuario.getEmail().equalsIgnoreCase(email)) {
                return usuario;
            }
        }

        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        DataSnapshot snapshot = leer(usuarios());
        List<Usuario> resultados = new ArrayList<>();

        for (DataSnapshot hijo : snapshot.getChildren()) {
            Usuario usuario = convertirUsuario(hijo);
            if (usuario != null) {
                resultados.add(usuario);
            }
        }

        return resultados;
    }

    @Override
    public List<Usuario> listarPendientes() {
        List<Usuario> pendientes = new ArrayList<>();

        for (Usuario usuario : listarTodos()) {
            if (usuario.getRol() == Rol.PENDIENTE) {
                pendientes.add(usuario);
            }
        }

        return pendientes;
    }

    @Override
    public void actualizar(Usuario usuarioActualizado) {
        Usuario usuarioExistente = buscarPorId(usuarioActualizado.getUid());

        if (usuarioExistente != null) {
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
            usuarioExistente.setApellido(usuarioActualizado.getApellido());
            usuarioExistente.setRut(usuarioActualizado.getRut());
            usuarioExistente.setEmail(usuarioActualizado.getEmail());
            usuarioExistente.setPassword(usuarioActualizado.getPassword());
            usuarioExistente.setRol(usuarioActualizado.getRol());

            guardar(usuarioExistente);
        }
    }

    @Override
    public void eliminar(String id) {
        esperar(usuarios().child(id).removeValueAsync());
    }

    private Usuario convertirUsuario(DataSnapshot snapshot) {
        Rol rol = snapshot.child("rol").getValue(Rol.class);

        if (rol == Rol.ALUMNO) {
            return snapshot.getValue(Alumno.class);
        }

        if (rol == Rol.PROFESOR || rol == Rol.PROFESOR_JEFE) {
            return snapshot.getValue(Profesor.class);
        }

        return snapshot.getValue(UsuarioPendiente.class);
    }
}