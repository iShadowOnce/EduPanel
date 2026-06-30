package com.edupanel.controller;

import com.edupanel.auth.AuthService;
import com.edupanel.auth.SesionUsuario;
import com.edupanel.exception.AnuncioInvalidoException;
import com.edupanel.model.Anuncio;
import com.edupanel.model.MensajeContacto;
import com.edupanel.model.Rol;
import com.edupanel.service.AnuncioService;
import com.edupanel.service.CursoService;
import com.edupanel.service.MensajeContactoService;
import com.edupanel.service.ProfesorService;
import com.edupanel.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import com.edupanel.model.Asignatura;
import com.edupanel.model.Profesor;

@Controller
public class ProfesorJefeController {

    private final UsuarioService usuarioService;
    private final ProfesorService profesorService;
    private final CursoService cursoService;
    private final AnuncioService anuncioService;
    private final MensajeContactoService mensajeContactoService;

    public ProfesorJefeController(UsuarioService usuarioService,
            ProfesorService profesorService,
            CursoService cursoService,
            AnuncioService anuncioService,
            MensajeContactoService mensajeContactoService) {
        this.usuarioService = usuarioService;
        this.profesorService = profesorService;
        this.cursoService = cursoService;
        this.anuncioService = anuncioService;
        this.mensajeContactoService = mensajeContactoService;
    }

    @GetMapping("/profesor-jefe/dashboard")
    public String dashboardProfesorJefe(Model model) {
        List<MensajeContacto> mensajesContacto = mensajeContactoService.listarMensajes();

        model.addAttribute("usuariosPendientesTotal", usuarioService.listarUsuariosPendientes().size());
        model.addAttribute("usuariosTotal", usuarioService.listarUsuarios().size());
        model.addAttribute("cursosTotal", cursoService.listarCursos().size());
        model.addAttribute("anuncios", anuncioService.listarAnuncios());
        model.addAttribute("mensajesContacto", mensajesContacto);
        model.addAttribute("mensajesContactoTotal", mensajesContacto.size());
        return "admin/dashboard";
    }

    @GetMapping("/profesor-jefe/usuarios-pendientes")
    public String verUsuariosPendientes(Model model) {
        model.addAttribute("usuariosPendientes", usuarioService.listarUsuariosPendientes());
        return "admin/usuarios";
    }

    @PostMapping("/profesor-jefe/usuarios/{id}/asignar-alumno")
    public String asignarAlumno(@PathVariable String id) {
        usuarioService.asignarRol(id, Rol.ALUMNO);
        return "redirect:/profesor-jefe/usuarios-pendientes";
    }

    @PostMapping("/profesor-jefe/usuarios/{id}/asignar-profesor")
    public String asignarProfesor(@PathVariable String id) {
        usuarioService.asignarRol(id, Rol.PROFESOR);
        return "redirect:/profesor-jefe/usuarios-pendientes";
    }

    @GetMapping("/profesor-jefe/profesores")
    public String verProfesores(Model model) {
        model.addAttribute("profesores", profesorService.listarProfesores());
        return "admin/profesores";
    }

    @GetMapping("/profesor-jefe/profesores/{id}/asignaturas")
    public String gestionarAsignaturasProfesor(@PathVariable String id, Model model) {
        Profesor profesor = profesorService.buscarPorId(id);

        model.addAttribute("profesor", profesor);
        model.addAttribute("asignaturasDisponibles", Asignatura.values());

        return "admin/asignaturas";
    }

    @PostMapping("/profesor-jefe/profesores/{id}/asignaturas/asignar")
    public String asignarAsignatura(@PathVariable String id,
            @ModelAttribute("asignatura") Asignatura asignatura) {
        profesorService.asignarAsignatura(id, asignatura);

        return "redirect:/profesor-jefe/profesores/" + id + "/asignaturas";
    }

    @PostMapping("/profesor-jefe/profesores/{id}/asignaturas/{asignatura}/quitar")
    public String quitarAsignatura(@PathVariable String id,
            @PathVariable Asignatura asignatura) {
        profesorService.quitarAsignatura(id, asignatura);

        return "redirect:/profesor-jefe/profesores/" + id + "/asignaturas";
    }

    @GetMapping("/profesor-jefe/anuncios")
    public String verAnuncios(Model model) {
        model.addAttribute("anuncios", anuncioService.listarAnuncios());
        model.addAttribute("nuevoAnuncio", new Anuncio());
        model.addAttribute("asignaturas", Asignatura.values());

        return "admin/anuncios";
    }

    @PostMapping("/profesor-jefe/anuncios/guardar")
    public String guardarAnuncio(@ModelAttribute Anuncio nuevoAnuncio,
            HttpSession session,
            Model model) {
        try {
            anuncioService.guardarAnuncioComoAdmin(
                    nuevoAnuncio,
                    obtenerAdministradorId(session),
                    obtenerAdministradorNombre(session));

            return "redirect:/profesor-jefe/anuncios";

        } catch (AnuncioInvalidoException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("anuncios", anuncioService.listarAnuncios());
            model.addAttribute("nuevoAnuncio", nuevoAnuncio);
            model.addAttribute("asignaturas", Asignatura.values());

            return "admin/anuncios";
        }
    }

    @GetMapping("/profesor-jefe/anuncios/{anuncioId}/editar")
    public String editarAnuncio(@PathVariable String anuncioId, Model model) {
        Anuncio anuncio = anuncioService.buscarPorId(anuncioId);

        if (anuncio == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El anuncio indicado no existe.");
        }

        model.addAttribute("anuncio", anuncio);
        model.addAttribute("asignaturas", Asignatura.values());

        return "admin/editar-anuncio";
    }

    @PostMapping("/profesor-jefe/anuncios/{anuncioId}/actualizar")
    public String actualizarAnuncio(@PathVariable String anuncioId,
            @ModelAttribute Anuncio anuncioActualizado,
            Model model) {
        try {
            anuncioService.actualizarAnuncioComoAdmin(anuncioId, anuncioActualizado);

            return "redirect:/profesor-jefe/anuncios";

        } catch (AnuncioInvalidoException e) {
            anuncioActualizado.setId(anuncioId);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("anuncio", anuncioActualizado);
            model.addAttribute("asignaturas", Asignatura.values());

            return "admin/editar-anuncio";
        }
    }

    @PostMapping("/profesor-jefe/anuncios/{anuncioId}/eliminar")
    public String eliminarAnuncio(@PathVariable String anuncioId) {
        anuncioService.eliminarAnuncioComoAdmin(anuncioId);

        return "redirect:/profesor-jefe/anuncios";
    }

    @GetMapping("/profesor-jefe/mensajes")
    public String verMensajes(Model model) {
        model.addAttribute("mensajesContacto", mensajeContactoService.listarMensajes());

        return "admin/mensajes";
    }

    private String obtenerAdministradorId(HttpSession session) {
        SesionUsuario sesionUsuario = obtenerSesionUsuario(session);
        return sesionUsuario == null ? "PROFESOR_JEFE" : sesionUsuario.getUid();
    }

    private String obtenerAdministradorNombre(HttpSession session) {
        SesionUsuario sesionUsuario = obtenerSesionUsuario(session);

        if (sesionUsuario == null) {
            return "Profesor Jefe";
        }

        return sesionUsuario.getNombre() + " " + sesionUsuario.getApellido();
    }

    private SesionUsuario obtenerSesionUsuario(HttpSession session) {
        Object usuarioSesion = session.getAttribute(AuthService.SESION_USUARIO);

        if (usuarioSesion instanceof SesionUsuario sesionUsuario) {
            return sesionUsuario;
        }

        return null;
    }
}
