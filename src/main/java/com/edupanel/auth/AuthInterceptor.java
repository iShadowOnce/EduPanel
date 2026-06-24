package com.edupanel.auth;

import com.edupanel.model.Rol;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    private final AuthService authService;

    public AuthInterceptor(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        SesionUsuario sesionUsuario = obtenerSesionUsuario(request);

        if (sesionUsuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }

        String ruta = obtenerRutaSinContexto(request);

        if (puedeAcceder(ruta, sesionUsuario)) {
            return true;
        }

        response.sendRedirect(request.getContextPath() + authService.obtenerRutaPrincipal(sesionUsuario));
        return false;
    }

    private SesionUsuario obtenerSesionUsuario(HttpServletRequest request) {
        HttpSession session = request.getSession(false);

        if (session == null) {
            return null;
        }

        Object usuarioSesion = session.getAttribute(AuthService.SESION_USUARIO);

        if (usuarioSesion instanceof SesionUsuario sesionUsuario) {
            return sesionUsuario;
        }

        return null;
    }

    private String obtenerRutaSinContexto(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        String uri = request.getRequestURI();

        if (contextPath != null && !contextPath.isBlank() && uri.startsWith(contextPath)) {
            return uri.substring(contextPath.length());
        }

        return uri;
    }

    private boolean puedeAcceder(String ruta, SesionUsuario sesionUsuario) {
        if (sesionUsuario.getRol() == Rol.PROFESOR_JEFE) {
            return true;
        }

        if (ruta.startsWith("/profesor-jefe")) {
            return false;
        }

        if (ruta.startsWith("/alumno")) {
            return puedeAccederComoAlumno(ruta, sesionUsuario);
        }

        if (ruta.startsWith("/profesor")) {
            return puedeAccederComoProfesor(ruta, sesionUsuario);
        }

        return true;
    }

    private boolean puedeAccederComoAlumno(String ruta, SesionUsuario sesionUsuario) {
        if (sesionUsuario.getRol() != Rol.ALUMNO) {
            return false;
        }

        String[] partes = ruta.split("/");

        if (partes.length > 2 && !partes[2].isBlank()) {
            return sesionUsuario.getUid().equals(partes[2]);
        }

        return true;
    }

    private boolean puedeAccederComoProfesor(String ruta, SesionUsuario sesionUsuario) {
        if (sesionUsuario.getRol() != Rol.PROFESOR) {
            return false;
        }

        String[] partes = ruta.split("/");

        if (partes.length > 2 && !partes[2].isBlank() && !"alumnos".equals(partes[2])) {
            return sesionUsuario.getUid().equals(partes[2]);
        }

        return true;
    }
}
