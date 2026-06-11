package com.edupanel.dto;

import com.edupanel.model.Asignatura;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalificacionDTO {

    private String alumnoId;
    private Asignatura asignatura;
    private double nota;
    private String descripcion;

}
