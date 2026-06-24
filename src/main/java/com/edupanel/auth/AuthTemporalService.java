package com.edupanel.auth;

import com.edupanel.model.Alumno;
import com.edupanel.model.Profesor;
import com.edupanel.model.Rol;
import com.edupanel.model.Usuario;
import com.edupanel.service.AlumnoService;
import com.edupanel.service.ProfesorService;
import com.edupanel.service.UsuarioService;
import org.springframework.stereotype.Service;

@Service
public class AuthTemporalService {

    public static final String SESION_USUARIO = "usuarioSesion";
    private static final String PROFESOR_JEFE_UID = "profesor-jefe-demo";
    private static final String PROFESOR_JEFE_EMAIL = "jefe@edupanel.cl";
    private static final String PROFESOR_JEFE_PASSWORD = "admin123";

    private final UsuarioService usuarioService;
    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;

    public AuthTemporalService(UsuarioService usuarioService,
            AlumnoService alumnoService,
            ProfesorService profesorService) {
        this.usuarioService = usuarioService;
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
    }

    public SesionUsuario autenticar(String email, String password) {
        validarCredenciales(email, password);

        if (esProfesorJefe(email, password)) {
            return new SesionUsuario(
                    PROFESOR_JEFE_UID,
                    "Profesor",
                    "Jefe",
                    PROFESOR_JEFE_EMAIL,
                    Rol.PROFESOR_JEFE);
        }

        Usuario usuarioPendiente = usuarioService.buscarPorEmail(email);

        if (usuarioPendiente != null && passwordCoincide(usuarioPendiente, password)) {
            return crearSesion(usuarioPendiente);
        }

        Alumno alumno = alumnoService.buscarPorEmail(email);

        if (alumno != null && passwordCoincide(alumno, password)) {
            return crearSesion(alumno);
        }

        Profesor profesor = profesorService.buscarPorEmail(email);

        if (profesor != null && passwordCoincide(profesor, password)) {
            return crearSesion(profesor);
        }

        throw new IllegalArgumentException("Email o contrasena incorrectos.");
    }

    public String obtenerRutaPrincipal(SesionUsuario sesionUsuario) {
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

    private void validarCredenciales(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contrasena es obligatoria.");
        }
    }

    private boolean esProfesorJefe(String email, String password) {
        return PROFESOR_JEFE_EMAIL.equalsIgnoreCase(email)
                && PROFESOR_JEFE_PASSWORD.equals(password);
    }

    private boolean passwordCoincide(Usuario usuario, String password) {
        return usuario.getPassword() != null && usuario.getPassword().equals(password);
    }

    private SesionUsuario crearSesion(Usuario usuario) {
        return new SesionUsuario(
                usuario.getUid(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol());
    }
}
