package com.edupanel.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Profesor extends Usuario {

    private List<Asignatura> asignaturas = new ArrayList<>();

    public Profesor() {
        super();
        setRol(Rol.PROFESOR);
    }

    @Override
    public String obtenerRutaDashboard() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerRutaDashboard'");
    }

}
