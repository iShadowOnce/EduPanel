package com.edupanel.controller;

import com.edupanel.model.Rol;
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

    public ProfesorJefeController(UsuarioService usuarioService, ProfesorService profesorService) {
        this.usuarioService = usuarioService;
        this.profesorService = profesorService;
    }

    @GetMapping("/profesor-jefe/usuarios-pendientes")
    public String verUsuariosPendientes(Model model) {
        model.addAttribute("usuariosPendientes", usuarioService.listarUsuariosPendientes());
        return "profesor-jefe-usuarios";
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
        return "profesor-jefe-profesores";
    }

    @GetMapping("/profesor-jefe/profesores/{id}/asignaturas")
    public String gestionarAsignaturasProfesor(@PathVariable String id, Model model) {
        Profesor profesor = profesorService.buscarPorId(id);

        model.addAttribute("profesor", profesor);
        model.addAttribute("asignaturasDisponibles", Asignatura.values());

        return "profesor-jefe-asignaturas";
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