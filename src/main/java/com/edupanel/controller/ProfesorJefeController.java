package com.edupanel.controller;

import com.edupanel.model.Rol;
import com.edupanel.service.CursoService;
import com.edupanel.service.ProfesorService;
import com.edupanel.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.edupanel.model.Asignatura;
import com.edupanel.model.Profesor;

@Controller
public class ProfesorJefeController {

    private final UsuarioService usuarioService;
    private final ProfesorService profesorService;
    private final CursoService cursoService;

    public ProfesorJefeController(UsuarioService usuarioService,
            ProfesorService profesorService,
            CursoService cursoService) {
        this.usuarioService = usuarioService;
        this.profesorService = profesorService;
        this.cursoService = cursoService;
    }

    @GetMapping("/profesor-jefe/dashboard")
    public String dashboardProfesorJefe(Model model) {
        model.addAttribute("usuariosPendientesTotal", usuarioService.listarUsuariosPendientes().size());
        model.addAttribute("usuariosTotal", usuarioService.listarUsuarios().size());
        model.addAttribute("cursosTotal", cursoService.listarCursos().size());
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
}
