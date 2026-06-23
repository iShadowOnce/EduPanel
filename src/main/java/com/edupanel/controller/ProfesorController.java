package com.edupanel.controller;

import com.edupanel.exception.AnuncioInvalidoException;
import com.edupanel.model.Anuncio;
import com.edupanel.model.Asignatura;
import com.edupanel.model.Profesor;
import com.edupanel.service.AnuncioService;
import com.edupanel.service.ProfesorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class ProfesorController {

    private final AnuncioService anuncioService;
    private final ProfesorService profesorService;

    public ProfesorController(AnuncioService anuncioService, ProfesorService profesorService) {
        this.anuncioService = anuncioService;
        this.profesorService = profesorService;
    }

    @GetMapping("/profesor/dashboard")
    public String dashboardProfesorSinId() {
        return "redirect:/login";
    }

    @GetMapping("/profesor/{profesorId}/dashboard")
    public String dashboardProfesor(@PathVariable String profesorId, Model model) {
        Profesor profesor = profesorService.buscarPorId(profesorId);

        model.addAttribute("profesor", profesor);

        return "profesor-dashboard";
    }

    @GetMapping("/profesor/{profesorId}/anuncios")
    public String verAnuncios(@PathVariable String profesorId, Model model) {
        Profesor profesor = profesorService.buscarPorId(profesorId);

        model.addAttribute("profesor", profesor);
        model.addAttribute("anuncios", anuncioService.listarPorProfesor(profesorId));
        model.addAttribute("nuevoAnuncio", new Anuncio());
        model.addAttribute("asignaturas", profesor.getAsignaturas());
        //model.addAttribute("asignaturas", Asignatura.values()); de esta forma veria todos los anuncios

        return "profesor-anuncios";
    }

    @PostMapping("/profesor/{profesorId}/anuncios/guardar")
    public String guardarAnuncio(@PathVariable String profesorId,
            @ModelAttribute Anuncio nuevoAnuncio,
            Model model) {
        try {
            Profesor profesor = profesorService.buscarPorId(profesorId);

            String profesorNombre = profesor.getNombre() + " " + profesor.getApellido();

            anuncioService.guardarAnuncio(nuevoAnuncio, profesorId, profesorNombre);

            return "redirect:/profesor/" + profesorId + "/anuncios";

        } catch (AnuncioInvalidoException e) {
            Profesor profesor = profesorService.buscarPorId(profesorId);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("profesor", profesor);
            model.addAttribute("anuncios", anuncioService.listarPorProfesor(profesorId));
            model.addAttribute("nuevoAnuncio", nuevoAnuncio);
            model.addAttribute("asignaturas", profesor.getAsignaturas());

            return "profesor-anuncios";
        }
    }

    @PostMapping("/profesor/{profesorId}/anuncios/{anuncioId}/eliminar")
    public String eliminarAnuncio(@PathVariable String profesorId,
            @PathVariable String anuncioId) {
        anuncioService.eliminarAnuncio(anuncioId);

        return "redirect:/profesor/" + profesorId + "/anuncios";
    }

    @GetMapping("/profesor/{profesorId}/anuncios/{anuncioId}/editar")
    public String editarAnuncio(@PathVariable String profesorId,
            @PathVariable String anuncioId,
            Model model) {
        Profesor profesor = profesorService.buscarPorId(profesorId);
        Anuncio anuncio = anuncioService.buscarPorId(anuncioId);

        model.addAttribute("profesor", profesor);
        model.addAttribute("anuncio", anuncio);
        model.addAttribute("asignaturas", profesor.getAsignaturas());

        return "profesor-editar-anuncio";
    }

    @PostMapping("/profesor/{profesorId}/anuncios/{anuncioId}/actualizar")
    public String actualizarAnuncio(@PathVariable String profesorId,
            @PathVariable String anuncioId,
            @ModelAttribute Anuncio anuncioActualizado,
            Model model) {
        try {
            anuncioService.actualizarAnuncio(anuncioId, anuncioActualizado);

            return "redirect:/profesor/" + profesorId + "/anuncios";

        } catch (AnuncioInvalidoException e) {
            Profesor profesor = profesorService.buscarPorId(profesorId);
            Anuncio anuncioOriginal = anuncioService.buscarPorId(anuncioId);

            anuncioActualizado.setId(anuncioOriginal.getId());
            anuncioActualizado.setProfesorId(anuncioOriginal.getProfesorId());
            anuncioActualizado.setFechaPublicacion(anuncioOriginal.getFechaPublicacion());

            model.addAttribute("error", e.getMessage());
            model.addAttribute("profesor", profesor);
            model.addAttribute("anuncio", anuncioActualizado);
            model.addAttribute("asignaturas", profesor.getAsignaturas());

            return "profesor-editar-anuncio";
        }
    }
}
