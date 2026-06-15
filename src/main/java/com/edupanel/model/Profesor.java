package com.edupanel.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class Profesor extends Usuario {

    private List<Asignatura> asignaturas;

    public Profesor() {
        super();
        setRol(Rol.PROFESOR);
    }

    @Override
    public String obtenerRutaDashboard() {
        return "/profesor/dashboard";
    }

}
