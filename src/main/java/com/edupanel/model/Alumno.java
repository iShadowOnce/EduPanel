package com.edupanel.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor 
public class Alumno extends Usuario {

    private String rut;
    private String curso;

    public Alumno() {
        super();
        setRol(Rol.ALUMNO);
    }

    @Override
    public String obtenerRutaDashboard() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'obtenerRutaDashboard'");
    }
}
