package com.edupanel.dto;

import com.edupanel.model.Asignatura;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnuncioDTO {

    private String titulo;
    private String mensaje;
    private Asignatura asignatura;
}
