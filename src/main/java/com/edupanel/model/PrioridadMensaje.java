package com.edupanel.model;

public enum PrioridadMensaje {

    ALTA("Alta"),
    MEDIA("Media"),
    BAJA("Baja");

    private final String nombre;

    PrioridadMensaje(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
