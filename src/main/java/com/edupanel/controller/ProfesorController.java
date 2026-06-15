package com.edupanel.controller;

import com.edupanel.model.Anuncio;
import com.edupanel.model.Asignatura;
import com.edupanel.service.AnuncioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.edupanel.exception.AnuncioInvalidoException;

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
    public String guardarAnuncio(@ModelAttribute Anuncio nuevoAnuncio, Model model) {
        try {
            anuncioService.guardarAnuncio(nuevoAnuncio);
            return "redirect:/profesor/anuncios";

        } catch (AnuncioInvalidoException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("anuncios", anuncioService.listarAnuncios());
            model.addAttribute("nuevoAnuncio", nuevoAnuncio);
            model.addAttribute("asignaturas", Asignatura.values());
            return "profesor-anuncios";
        }
    }

    @PostMapping("/profesor/anuncios/{id}/eliminar")
    public String eliminarAnuncio(@PathVariable String id) {
        anuncioService.eliminarAnuncio(id);
        return "redirect:/profesor/anuncios";
    }

    @GetMapping("/profesor/anuncios/{id}/editar")
    public String editarAnuncio(@PathVariable String id, Model model) {
        Anuncio anuncio = anuncioService.buscarPorId(id);

        model.addAttribute("anuncio", anuncio);
        model.addAttribute("asignaturas", Asignatura.values());

        return "profesor-editar-anuncio";
    }

    @PostMapping("/profesor/anuncios/{id}/actualizar")
    public String actualizarAnuncio(@PathVariable String id,
            @ModelAttribute Anuncio anuncioActualizado,
            Model model) {
        try {
            anuncioService.actualizarAnuncio(id, anuncioActualizado);
            return "redirect:/profesor/anuncios";

        } catch (AnuncioInvalidoException e) {
            model.addAttribute("error", e.getMessage());

            Anuncio anuncioOriginal = anuncioService.buscarPorId(id);

            anuncioActualizado.setId(anuncioOriginal.getId());
            anuncioActualizado.setProfesorId(anuncioOriginal.getProfesorId());
            anuncioActualizado.setFechaPublicacion(anuncioOriginal.getFechaPublicacion());

            model.addAttribute("anuncio", anuncioActualizado);
            model.addAttribute("asignaturas", Asignatura.values());

            return "profesor-editar-anuncio";
        }
    }
}