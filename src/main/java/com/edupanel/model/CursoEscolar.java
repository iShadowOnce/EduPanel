package com.edupanel.model;

public enum CursoEscolar {
    PRIMERO_BASICO("1 Basico"),
    SEGUNDO_BASICO("2 Basico"),
    TERCERO_BASICO("3 Basico"),
    CUARTO_BASICO("4 Basico"),
    QUINTO_BASICO("5 Basico"),
    SEXTO_BASICO("6 Basico"),
    SEPTIMO_BASICO("7 Basico"),
    OCTAVO_BASICO("8 Basico"),
    PRIMERO_MEDIO("1 Medio"),
    SEGUNDO_MEDIO("2 Medio"),
    TERCERO_MEDIO("3 Medio"),
    CUARTO_MEDIO("4 Medio");

    private final String nombre;

    CursoEscolar(String nombre) {
        this.nombre = nombre;
    }

    public String getId() {
        return name();
    }

    public String getNombre() {
        return nombre;
    }

    public static CursoEscolar buscarPorId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }

        for (CursoEscolar curso : values()) {
            if (curso.name().equals(id)) {
                return curso;
            }
        }

        return null;
    }
}
