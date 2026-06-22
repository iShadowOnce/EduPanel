package com.edupanel.exception;

public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String uid) {
        super("No existe un usuario con uid " + uid + ".");
    }
}
