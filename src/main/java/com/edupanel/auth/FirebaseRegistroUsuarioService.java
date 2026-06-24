package com.edupanel.auth;

import com.edupanel.model.UsuarioPendiente;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("firebase")
public class FirebaseRegistroUsuarioService implements RegistroUsuarioService {

    @Override
    public void registrarUsuarioPendiente(UsuarioPendiente usuario) {
        RegistroUsuarioValidator.validar(usuario);

        /*
        NOTA JOACO: este metodo es pa implementar registro real con Firebase Auth.
        1. Crear usuario en Firebase Auth con email/contrasena.
        2. Usar el uid que devuelve Firebase, no UUID.randomUUID().
        3. Guardar perfil en /usuarios/{uid} con rol PENDIENTE.
        4. No guardar la contrasena en Realtime Database. 
        */
        throw new UnsupportedOperationException("TODO Firebase: implementar registro con Firebase Auth.");
    }
}
