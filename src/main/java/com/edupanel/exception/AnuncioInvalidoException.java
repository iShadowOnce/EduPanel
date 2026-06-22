package com.edupanel.exception;

public class AnuncioInvalidoException extends RuntimeException {

    public AnuncioInvalidoException(String mensaje) {
        super(mensaje);
    }
}