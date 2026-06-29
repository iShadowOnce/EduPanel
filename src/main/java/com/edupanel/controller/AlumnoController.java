package com.edupanel.controller;

import com.edupanel.service.AlumnoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.edupanel.model.Calificacion;
import com.edupanel.service.AnuncioService;
import org.springframework.web.bind.annotation.PathVariable;

import com.edupanel.exception.CalificacionInvalidaException;

import com.edupanel.model.Profesor;
import com.edupanel.service.ProfesorService;

import com.edupanel.model.Curso;
import com.edupanel.service.CursoService;


/*
NOTA JOACO: De momento los Controller quedan como estan debido que los 
get y post estan correctos, y la app no deberia hacer 
diferencia si el dato que recibe es en memoria o firebase 
en mucha teoria XD, pero baasicamente aca estan las rutas y
las peticiones que genera el usuario a la app
*/

@Controller
public class AlumnoController {

    private final AlumnoService alumnoService;
    private final AnuncioService anuncioService;
    private final ProfesorService profesorService;
    private final CursoService cursoService;

    public AlumnoController(AlumnoService alumnoService,
            AnuncioService anuncioService,
            ProfesorService profesorService,
            CursoService cursoService) {
        this.alumnoService = alumnoService;
        this.anuncioService = anuncioService;
        this.profesorService = profesorService;
        this.cursoService = cursoService;
    }

    @GetMapping("/alumno/{id}/dashboard")
    public String dashboardAlumno(@PathVariable String id, Model model) {
        Curso curso = cursoService.buscarCursoPorAlumnoId(id);

        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        model.addAttribute("curso", curso);

        return "alumno/dashboard";
    }

    @GetMapping("/alumno/{id}/notas")
    public String verNotasAlumnoComoAlumno(@PathVariable String id, Model model) {
        Curso curso = cursoService.buscarCursoPorAlumnoId(id);

        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        model.addAttribute("curso", curso);

        return "alumno/notas";
    }

    @GetMapping("/alumno/{id}/anuncios")
    public String verAnunciosAlumno(@PathVariable String id, Model model) {
        Curso curso = cursoService.buscarCursoPorAlumnoId(id);

        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        model.addAttribute("curso", curso);
        model.addAttribute("anuncios", anuncioService.listarAnuncios());

        return "alumno/anuncios";
    }

    @GetMapping("/profesor/{profesorId}/alumnos")
    public String verAlumnosComoProfesor(@PathVariable String profesorId, Model model) {
        Profesor profesor = profesorService.buscarPorId(profesorId);

        model.addAttribute("profesor", profesor);
        model.addAttribute("alumnos", alumnoService.listarAlumnos());

        return "profesor/alumnos";
    }

    @GetMapping("/profesor/{profesorId}/alumnos/{alumnoId}/notas")
    public String verNotasAlumnoComoProfesor(@PathVariable String profesorId,
            @PathVariable String alumnoId,
            Model model) {
        Profesor profesor = profesorService.buscarPorId(profesorId);

        model.addAttribute("profesor", profesor);
        model.addAttribute("alumno", alumnoService.buscarPorId(alumnoId));
        model.addAttribute("nuevaCalificacion", new Calificacion());
        model.addAttribute("asignaturas", profesor.getAsignaturas());

        return "profesor/alumno-notas";
    }

    @PostMapping("/profesor/{profesorId}/alumnos/{alumnoId}/notas/guardar")
    public String guardarNotaComoProfesor(@PathVariable String profesorId,
            @PathVariable String alumnoId,
            @ModelAttribute Calificacion nuevaCalificacion,
            Model model) {
        try {
            alumnoService.agregarCalificacion(alumnoId, nuevaCalificacion);

            return "redirect:/profesor/" + profesorId + "/alumnos/" + alumnoId + "/notas";

        } catch (CalificacionInvalidaException e) {
            Profesor profesor = profesorService.buscarPorId(profesorId);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("profesor", profesor);
            model.addAttribute("alumno", alumnoService.buscarPorId(alumnoId));
            model.addAttribute("nuevaCalificacion", nuevaCalificacion);
            model.addAttribute("asignaturas", profesor.getAsignaturas());

            return "profesor/alumno-notas";
        }
    }

    @GetMapping("/profesor/{profesorId}/alumnos/{alumnoId}/notas/{notaId}/editar")
    public String editarNotaComoProfesor(@PathVariable String profesorId,
            @PathVariable String alumnoId,
            @PathVariable String notaId,
            Model model) {
        Profesor profesor = profesorService.buscarPorId(profesorId);

        model.addAttribute("profesor", profesor);
        model.addAttribute("alumno", alumnoService.buscarPorId(alumnoId));
        model.addAttribute("calificacion", alumnoService.buscarCalificacionPorId(alumnoId, notaId));

        return "profesor/editar-nota";
    }

    @PostMapping("/profesor/{profesorId}/alumnos/{alumnoId}/notas/{notaId}/actualizar")
    public String actualizarNotaComoProfesor(@PathVariable String profesorId,
            @PathVariable String alumnoId,
            @PathVariable String notaId,
            @ModelAttribute Calificacion calificacionActualizada,
            Model model) {
        try {
            alumnoService.actualizarCalificacion(alumnoId, notaId, calificacionActualizada);
            return "redirect:/profesor/" + profesorId + "/alumnos/" + alumnoId + "/notas";

        } catch (CalificacionInvalidaException e) {
            Profesor profesor = profesorService.buscarPorId(profesorId);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("profesor", profesor);
            model.addAttribute("alumno", alumnoService.buscarPorId(alumnoId));

            calificacionActualizada.setId(notaId);
            calificacionActualizada.setAlumnoId(alumnoId);

            model.addAttribute("calificacion", calificacionActualizada);

            return "profesor/editar-nota";
        }
    }

    @PostMapping("/profesor/{profesorId}/alumnos/{alumnoId}/notas/{notaId}/eliminar")
    public String eliminarNotaComoProfesor(@PathVariable String profesorId,
            @PathVariable String alumnoId,
            @PathVariable String notaId) {

        alumnoService.eliminarCalificacion(alumnoId, notaId);

        return "redirect:/profesor/" + profesorId + "/alumnos/" + alumnoId + "/notas";
    }
}
