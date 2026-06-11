package com.edupanel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoDTO {

    private String nombre;
    private String email;
    private String rut;
    private String curso;

}
