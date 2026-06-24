package com.edupanel.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Pack completo incluye @Getter @Setter @ToString @EqualsAndHashCode @RequiredArgsConstructor
@AllArgsConstructor // Constructor con argumentos
@NoArgsConstructor // Constructor sin argumentos
public abstract class Usuario {

    private String uid;
    private String nombre;
    private String apellido;
    private String rut;
    private String email;
    private String password;
    private Rol rol = Rol.PENDIENTE;

    public abstract String obtenerRutaDashboard();
}
