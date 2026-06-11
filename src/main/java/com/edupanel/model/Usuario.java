package com.edupanel.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data // Pack completo incluye @Getter @Setter @ToString @EqualsAndHashCode @RequiredArgsConstructor
@AllArgsConstructor // Constructor con argumentos
public class Usuario {

    private String id;
    private String nombre;
    private String email;
    private Rol rol;

    public Usuario() {
        this.rol = Rol.PENDIENTE;
    }
}
