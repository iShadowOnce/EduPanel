package com.edupanel.model;

public enum Asignatura {
    LENGUAJE("Lenguaje"),
    MATEMATICAS("Matematicas"),
    CIENCIAS_NATURALES("Ciencias Naturales"),
    INGLES("Ingles"),
    HISTORIA("Historia"),
    ARTES("Artes"),
    EDUCACION_FISICA("Educacion Fisica"),
    MUSICA("Musica");

    private final String nombre;

    Asignatura(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }
}
