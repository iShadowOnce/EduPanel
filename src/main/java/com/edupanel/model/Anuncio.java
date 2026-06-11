package com.edupanel.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Anuncio {

    private String id;
    private String titulo;
    private String mensaje;
    private Asignatura asignatura;
    private String profesorId;
    private LocalDateTime fechaPublicacion;

}
