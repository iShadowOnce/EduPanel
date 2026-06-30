package com.edupanel.auth;

import com.edupanel.model.Usuario;
import com.edupanel.util.RutUtils;

public final class RegistroUsuarioValidator {

    private RegistroUsuarioValidator() {
    }

    public static void validar(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (usuario.getApellido() == null || usuario.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }

        usuario.setRut(RutUtils.normalizar(usuario.getRut()));

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contrasena es obligatoria.");
        }

        if (usuario.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contrasena debe tener al menos 6 caracteres.");
        }
    }
}
