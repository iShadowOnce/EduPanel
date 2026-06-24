package com.edupanel.auth;

import com.edupanel.model.Rol;

public interface AuthService {

    String SESION_USUARIO = "usuarioSesion";

    SesionUsuario autenticar(String email, String password);

    default String obtenerRutaPrincipal(SesionUsuario sesionUsuario) {
        if (sesionUsuario.getRol() == Rol.PROFESOR_JEFE) {
            return "/profesor-jefe/dashboard";
        }

        if (sesionUsuario.getRol() == Rol.PROFESOR) {
            return "/profesor/" + sesionUsuario.getUid() + "/dashboard";
        }

        if (sesionUsuario.getRol() == Rol.ALUMNO) {
            return "/alumno/" + sesionUsuario.getUid() + "/dashboard";
        }

        return "/pendiente";
    }
}
