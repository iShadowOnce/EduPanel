package com.edupanel.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("firebase")
public class FirebaseAuthService implements AuthService {

    @Override
    public SesionUsuario autenticar(String email, String password) {
        /*
        NOTA JOACO: Aca debes implementar login real con Firebase authentication bro
        1. Validar email/contrasena contra Firebase Auth.
        2. Obtener uid del usuario autenticado.
        3. Leer el rol y datos basicos desde Realtime Database.
        4. Retornar SesionUsuario para que AuthInterceptor siga funcionando. 
        */
        throw new UnsupportedOperationException("implementar autenticacion con Firebase Auth.");
    }
}
