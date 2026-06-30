package com.edupanel.controller;

import com.edupanel.exception.AnuncioInvalidoException;
import com.edupanel.exception.MensajeContactoInvalidoException;
import com.edupanel.model.Anuncio;
import com.edupanel.model.MensajeContacto;
import com.edupanel.model.PrioridadMensaje;
import com.edupanel.model.Profesor;
import com.edupanel.service.AnuncioService;
import com.edupanel.service.MensajeContactoService;
import com.edupanel.service.ProfesorService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfesorController {

    private final AnuncioService anuncioService;
    private final ProfesorService profesorService;
    private final MensajeContactoService mensajeContactoService;

    public ProfesorController(AnuncioService anuncioService,
            ProfesorService profesorService,
            MensajeContactoService mensajeContactoService) {
        this.anuncioService = anuncioService;
        this.profesorService = profesorService;
        this.mensajeContactoService = mensajeContactoService;
    }

    @GetMapping("/profesor/dashboard")
    public String dashboardProfesorSinId() {
        return "redirect:/login";
    }

    @GetMapping("/profesor/{profesorId}/dashboard")
    public String dashboardProfesor(@PathVariable String profesorId, Model model) {
        Profesor profesor = profesorService.buscarPorId(profesorId);

        model.addAttribute("profesor", profesor);
        model.addAttribute("anuncios", anuncioService.listarAnuncios());

        return "profesor/dashboard";
    }

    @GetMapping("/profesor/{profesorId}/anuncios")
    public String verAnuncios(@PathVariable String profesorId, Model model) {
        Profesor profesor = profesorService.buscarPorId(profesorId);

        model.addAttribute("profesor", profesor);
        model.addAttribute("anuncios", anuncioService.listarAnuncios());
        model.addAttribute("nuevoAnuncio", new Anuncio());
        model.addAttribute("asignaturas", profesor.getAsignaturas());

        return "profesor/anuncios";
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
            model.addAttribute("anuncios", anuncioService.listarAnuncios());
            model.addAttribute("nuevoAnuncio", nuevoAnuncio);
            model.addAttribute("asignaturas", profesor.getAsignaturas());

            return "profesor/anuncios";
        }
    }

    @PostMapping("/profesor/{profesorId}/anuncios/{anuncioId}/eliminar")
    public String eliminarAnuncio(@PathVariable String profesorId,
            @PathVariable String anuncioId,
            RedirectAttributes redirectAttributes) {
        try {
            anuncioService.eliminarAnuncio(profesorId, anuncioId);
        } catch (AnuncioInvalidoException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profesor/" + profesorId + "/anuncios";
    }

    @GetMapping("/profesor/{profesorId}/anuncios/{anuncioId}/editar")
    public String editarAnuncio(@PathVariable String profesorId,
            @PathVariable String anuncioId,
            Model model,
            RedirectAttributes redirectAttributes) {
        Profesor profesor = profesorService.buscarPorId(profesorId);
        Anuncio anuncio = anuncioService.buscarPorId(anuncioId);

        if (anuncio == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El anuncio indicado no existe.");
        }

        if (!profesorId.equals(anuncio.getProfesorId())) {
            redirectAttributes.addFlashAttribute("error", "Solo puede editar sus propios anuncios.");
            return "redirect:/profesor/" + profesorId + "/anuncios";
        }

        model.addAttribute("profesor", profesor);
        model.addAttribute("anuncio", anuncio);
        model.addAttribute("asignaturas", profesor.getAsignaturas());

        return "profesor/editar-anuncio";
    }

    @PostMapping("/profesor/{profesorId}/anuncios/{anuncioId}/actualizar")
    public String actualizarAnuncio(@PathVariable String profesorId,
            @PathVariable String anuncioId,
            @ModelAttribute Anuncio anuncioActualizado,
            Model model) {
        try {
            anuncioService.actualizarAnuncio(profesorId, anuncioId, anuncioActualizado);

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

            return "profesor/editar-anuncio";
        }
    }

    @GetMapping("/profesor/{profesorId}/contacto")
    public String contactarProfesorJefe(@PathVariable String profesorId, Model model) {
        Profesor profesor = profesorService.buscarPorId(profesorId);

        if (profesor == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El profesor indicado no existe.");
        }

        model.addAttribute("profesor", profesor);
        model.addAttribute("mensajeContacto", new MensajeContacto());
        model.addAttribute("prioridades", PrioridadMensaje.values());

        return "profesor/contacto";
    }

    @PostMapping("/profesor/{profesorId}/contacto/enviar")
    public String enviarMensajeProfesorJefe(@PathVariable String profesorId,
            @ModelAttribute MensajeContacto mensajeContacto,
            Model model,
            RedirectAttributes redirectAttributes) {
        try {
            mensajeContactoService.enviarMensaje(profesorId, mensajeContacto);
            redirectAttributes.addFlashAttribute("exito", "Mensaje enviado al profesor jefe.");

            return "redirect:/profesor/" + profesorId + "/contacto";

        } catch (MensajeContactoInvalidoException e) {
            Profesor profesor = profesorService.buscarPorId(profesorId);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("profesor", profesor);
            model.addAttribute("mensajeContacto", mensajeContacto);
            model.addAttribute("prioridades", PrioridadMensaje.values());

            return "profesor/contacto";
        }
    }
}
