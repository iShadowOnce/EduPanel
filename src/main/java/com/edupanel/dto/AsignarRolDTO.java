package com.edupanel.dto;

import java.util.List;
import com.edupanel.model.Asignatura;
import com.edupanel.model.Rol;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data   
@NoArgsConstructor
@AllArgsConstructor
public class AsignarRolDTO {

    private String usuarioId;
    private Rol rol;
    private String curso;
    private List<Asignatura> asignaturas;    

}

/* 
Esto me podria servir para que el profesor jefe tome a un usuario 
pendiente y le puedaa asignar un rol.

Joaquin -> PROFESOR -> Matematicas
Joaquin -> ALUMNO -> 4A 
*/