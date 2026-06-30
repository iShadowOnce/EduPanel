package com.edupanel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Calificacion {

    private String id;
    private String alumnoId;
    private String nombre;
    private String rut;
    private Asignatura asignatura;
    private double nota;
    private String descripcion;

}
