package com.edupanel.exception;

public class UsuarioDuplicadoException extends RuntimeException {

    public UsuarioDuplicadoException(String email) {
        super("Ya existe un usuario registrado con el correo " + email + ".");
    }
}
