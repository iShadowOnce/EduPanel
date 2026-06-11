package com.edupanel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadoAsignatura {

    private String id;
    private String AlumnoId;
    private Asignatura asignatura;
    private boolean aprobado;

}
