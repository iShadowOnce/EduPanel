package com.edupanel.model;

import lombok.Data;

@Data
public class UsuarioPendiente extends Usuario {

    public UsuarioPendiente() {
        super();
        setRol(Rol.PENDIENTE);
    }

    public UsuarioPendiente(String uid, String nombre,
            String apellido, String rut, String email) {
        super(uid, nombre, apellido, rut, email, Rol.PENDIENTE);
    }

    @Override
    public String obtenerRutaDashboard() {
        return "/pendiente";
    }

}
