package com.edupanel.model;

import com.edupanel.util.RutUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class Usuario {

    private String uid;
    private String nombre;
    private String apellido;
    private String rut;
    private String email;
    /* 
    NOTA JOACO: eliminar este campo cuando Firebase Auth gestione las contrasenas.
    Ya que segun estuve viendo en Firebase la contrasena no debe guardarse en Realtime 
    Database ni en modelos propios y en vola nos bajan la nota no se.
    */
    private String password;
    private Rol rol = Rol.PENDIENTE;

    public String getRutFormateado() {
        return RutUtils.formatear(rut);
    }

    public abstract String obtenerRutaDashboard();
}
