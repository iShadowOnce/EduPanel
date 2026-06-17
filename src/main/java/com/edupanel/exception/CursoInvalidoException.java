package com.edupanel.exception;

public class CursoInvalidoException extends RuntimeException {

    public CursoInvalidoException(String mensaje) {
        super(mensaje);
    }
}