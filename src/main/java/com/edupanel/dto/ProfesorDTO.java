package com.edupanel.dto;

import java.util.List;

import com.edupanel.model.Asignatura;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfesorDTO {

    private String nombre;
    private String email;
    private List<Asignatura> asignaturas;

}
