package com.edupanel.model;

public class UsuarioPendiente extends Usuario {

    public UsuarioPendiente() {
        super();
        setRol(Rol.PENDIENTE);
    }

    @Override
    public String obtenerRutaDashboard() {
        return "/pendiente";
    }
}