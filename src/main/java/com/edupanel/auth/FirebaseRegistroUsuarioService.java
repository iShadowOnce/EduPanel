package com.edupanel.auth;

import com.edupanel.exception.UsuarioDuplicadoException;
import com.edupanel.model.Rol;
import com.edupanel.model.UsuarioPendiente;
import com.edupanel.repository.UsuarioRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("firebase")
public class FirebaseRegistroUsuarioService implements RegistroUsuarioService {

    private final UsuarioRepository usuarioRepository;

    public FirebaseRegistroUsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public void registrarUsuarioPendiente(UsuarioPendiente usuarioPendiente) {
        RegistroUsuarioValidator.validar(usuarioPendiente);

        String uid = crearUsuarioEnFirebaseAuth(usuarioPendiente);

        usuarioPendiente.setUid(uid);
        usuarioPendiente.setRol(Rol.PENDIENTE);
        usuarioPendiente.setPassword(null);

        usuarioRepository.guardar(usuarioPendiente);
    }

    private String crearUsuarioEnFirebaseAuth(UsuarioPendiente usuarioPendiente) {
        UserRecord.CreateRequest solicitudCreacion = new UserRecord.CreateRequest()
                .setEmail(usuarioPendiente.getEmail())
                .setPassword(usuarioPendiente.getPassword())
                .setDisplayName(usuarioPendiente.getNombre() + " " + usuarioPendiente.getApellido());

        try {
            UserRecord usuarioCreado = FirebaseAuth.getInstance().createUser(solicitudCreacion);
            return usuarioCreado.getUid();

        } catch (FirebaseAuthException e) {
            if ("EMAIL_EXISTS".equals(e.getErrorCode().toString())) {
                throw new UsuarioDuplicadoException("Ya existe un usuario registrado con ese email.");
            }
            throw new RuntimeException("Error al registrar el usuario en Firebase: " + e.getMessage(), e);
        }
    }
}
