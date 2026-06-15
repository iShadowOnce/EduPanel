package com.edupanel.controller;

import com.edupanel.model.Anuncio;
import com.edupanel.model.Asignatura;
import com.edupanel.service.AnuncioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfesorController {

    private final AnuncioService anuncioService;

    public ProfesorController(AnuncioService anuncioService) {
        this.anuncioService = anuncioService;
    }

    @GetMapping("/profesor/dashboard")
    public String dashboardProfesor() {
        return "profesor-dashboard";
    }

    @GetMapping("/profesor/anuncios")
    public String verAnuncios(Model model) {
        model.addAttribute("anuncios", anuncioService.listarAnuncios());
        model.addAttribute("nuevoAnuncio", new Anuncio());
        model.addAttribute("asignaturas", Asignatura.values());

        return "profesor-anuncios";
    }

    @PostMapping("/profesor/anuncios/guardar")
    public String guardarAnuncio(@ModelAttribute Anuncio nuevoAnuncio) {
        anuncioService.guardarAnuncio(nuevoAnuncio);
        return "redirect:/profesor/anuncios";
    }
}