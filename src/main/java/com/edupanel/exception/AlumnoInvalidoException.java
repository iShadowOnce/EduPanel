package com.edupanel.exception;

public class AlumnoInvalidoException extends RuntimeException {

    public AlumnoInvalidoException(String mensaje) {
        super(mensaje);
    }
}