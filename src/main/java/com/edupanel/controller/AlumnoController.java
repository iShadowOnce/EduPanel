package com.edupanel.controller;

import com.edupanel.service.AlumnoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.edupanel.model.Alumno;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.edupanel.model.Calificacion;
import com.edupanel.service.AnuncioService;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AlumnoController {

    private final AlumnoService alumnoService;
    private final AnuncioService anuncioService;

    public AlumnoController(AlumnoService alumnoService, AnuncioService anuncioService) {
        this.alumnoService = alumnoService;
        this.anuncioService = anuncioService;
    }

    @GetMapping("/profesor/alumnos")
    public String verAlumnos(Model model) {
        model.addAttribute("alumnos", alumnoService.listarAlumnos());
        model.addAttribute("nuevoAlumno", new Alumno());
        return "profesor-alumnos";
    }

    @PostMapping("/profesor/alumnos/guardar")
    public String guardarAlumno(@ModelAttribute Alumno nuevoAlumno) {
        alumnoService.guardarAlumno(nuevoAlumno);
        return "redirect:/profesor/alumnos";
    }

    @GetMapping("/profesor/alumnos/{id}/notas")
    public String verNotasAlumno(@PathVariable String id, Model model) {
        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        model.addAttribute("nuevaCalificacion", new Calificacion());
        return "profesor-alumno-notas";
    }

    @PostMapping("/profesor/alumnos/{id}/notas/guardar")
    public String guardarNota(@PathVariable String id,
            @ModelAttribute Calificacion nuevaCalificacion) {
        alumnoService.agregarCalificacion(id, nuevaCalificacion);
        return "redirect:/profesor/alumnos/" + id + "/notas";
    }

    @GetMapping("/alumno/{id}/dashboard")
    public String dashboardAlumno(@PathVariable String id, Model model) {
        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        return "alumno-dashboard";
    }

    @GetMapping("/alumno/{id}/notas")
    public String verNotasAlumnoComoAlumno(@PathVariable String id, Model model) {
        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        return "alumno-notas";
    }

    @GetMapping("/alumno/{id}/anuncios")
    public String verAnunciosAlumno(@PathVariable String id, Model model) {
        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        model.addAttribute("anuncios", anuncioService.listarAnuncios());
        return "alumno-anuncios";
    }

    @PostMapping("/profesor/alumnos/{id}/eliminar")
    public String eliminarAlumno(@PathVariable String id) {
        alumnoService.eliminarAlumno(id);
        return "redirect:/profesor/alumnos";
    }

}