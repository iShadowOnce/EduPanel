package com.edupanel.auth;

import com.edupanel.model.Rol;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionUsuario implements Serializable {

    private String uid;
    private String nombre;
    private String apellido;
    private String email;
    private Rol rol;
}
