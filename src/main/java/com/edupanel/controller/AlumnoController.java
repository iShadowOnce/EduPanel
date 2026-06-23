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

import com.edupanel.exception.AlumnoInvalidoException;
import com.edupanel.exception.CalificacionInvalidaException;

import com.edupanel.model.Profesor;
import com.edupanel.service.ProfesorService;

import com.edupanel.model.Curso;
import com.edupanel.service.CursoService;

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

    @GetMapping("/profesor/alumnos")
    public String verAlumnosSinProfesor() {
        return "redirect:/login";
    }

    @PostMapping("/profesor/alumnos/guardar")
    public String guardarAlumno() {
        return "redirect:/login";
    }

    @GetMapping("/profesor/alumnos/{id}/notas")
    public String verNotasAlumno(@PathVariable String id, Model model) {
        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        model.addAttribute("nuevaCalificacion", new Calificacion());
        return "profesor-alumno-notas";
    }

    @PostMapping("/profesor/alumnos/{id}/notas/guardar")
    public String guardarNota(@PathVariable String id,
            @ModelAttribute Calificacion nuevaCalificacion,
            Model model) {
        try {
            alumnoService.agregarCalificacion(id, nuevaCalificacion);
            return "redirect:/profesor/alumnos/" + id + "/notas";

        } catch (CalificacionInvalidaException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("alumno", alumnoService.buscarPorId(id));
            model.addAttribute("nuevaCalificacion", nuevaCalificacion);
            return "profesor-alumno-notas";
        }
    }

    @GetMapping("/alumno/{id}/dashboard")
    public String dashboardAlumno(@PathVariable String id, Model model) {
        Curso curso = cursoService.buscarCursoPorAlumnoId(id);

        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        model.addAttribute("curso", curso);

        return "alumno-dashboard";
    }

    @GetMapping("/alumno/{id}/notas")
    public String verNotasAlumnoComoAlumno(@PathVariable String id, Model model) {
        Curso curso = cursoService.buscarCursoPorAlumnoId(id);

        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        model.addAttribute("curso", curso);

        return "alumno-notas";
    }

    @GetMapping("/alumno/{id}/anuncios")
    public String verAnunciosAlumno(@PathVariable String id, Model model) {
        Curso curso = cursoService.buscarCursoPorAlumnoId(id);

        model.addAttribute("alumno", alumnoService.buscarPorId(id));
        model.addAttribute("curso", curso);
        model.addAttribute("anuncios", anuncioService.listarAnuncios());

        return "alumno-anuncios";
    }

    @PostMapping("/profesor/alumnos/{id}/eliminar")
    public String eliminarAlumno(@PathVariable String id) {
        alumnoService.eliminarAlumno(id);
        return "redirect:/profesor/alumnos";
    }

    @GetMapping("/profesor/alumnos/{id}/editar")
    public String editarAlumno(@PathVariable String id, Model model) {
        Alumno alumno = alumnoService.buscarPorId(id);

        model.addAttribute("alumno", alumno);

        return "profesor-editar-alumno";
    }

    @PostMapping("/profesor/alumnos/{id}/actualizar")
    public String actualizarAlumno(@PathVariable String id,
            @ModelAttribute Alumno alumnoActualizado,
            Model model) {
        try {
            alumnoService.actualizarAlumno(id, alumnoActualizado);
            return "redirect:/profesor/alumnos";

        } catch (AlumnoInvalidoException e) {
            model.addAttribute("error", e.getMessage());

            Alumno alumnoOriginal = alumnoService.buscarPorId(id);

            alumnoActualizado.setUid(alumnoOriginal.getUid());
            alumnoActualizado.setNotas(alumnoOriginal.getNotas());
            alumnoActualizado.setRol(alumnoOriginal.getRol());

            model.addAttribute("alumno", alumnoActualizado);

            return "profesor-editar-alumno";
        }
    }

    @GetMapping("/profesor/alumnos/{alumnoId}/notas/{notaId}/editar")
    public String editarNota(@PathVariable String alumnoId,
            @PathVariable String notaId,
            Model model) {

        model.addAttribute("alumno", alumnoService.buscarPorId(alumnoId));
        model.addAttribute("calificacion", alumnoService.buscarCalificacionPorId(alumnoId, notaId));

        return "profesor-editar-nota";
    }

    @PostMapping("/profesor/alumnos/{alumnoId}/notas/{notaId}/actualizar")
    public String actualizarNota(@PathVariable String alumnoId,
            @PathVariable String notaId,
            @ModelAttribute Calificacion calificacionActualizada,
            Model model) {
        try {
            alumnoService.actualizarCalificacion(alumnoId, notaId, calificacionActualizada);
            return "redirect:/profesor/alumnos/" + alumnoId + "/notas";

        } catch (CalificacionInvalidaException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("alumno", alumnoService.buscarPorId(alumnoId));

            calificacionActualizada.setId(notaId);
            calificacionActualizada.setAlumnoId(alumnoId);

            model.addAttribute("calificacion", calificacionActualizada);

            return "profesor-editar-nota";
        }
    }

    @PostMapping("/profesor/alumnos/{alumnoId}/notas/{notaId}/eliminar")
    public String eliminarNota(@PathVariable String alumnoId,
            @PathVariable String notaId) {

        alumnoService.eliminarCalificacion(alumnoId, notaId);

        return "redirect:/profesor/alumnos/" + alumnoId + "/notas";
    }

    @GetMapping("/profesor/{profesorId}/alumnos")
    public String verAlumnosComoProfesor(@PathVariable String profesorId, Model model) {
        Profesor profesor = profesorService.buscarPorId(profesorId);

        model.addAttribute("profesor", profesor);
        model.addAttribute("alumnos", alumnoService.listarAlumnos());
        model.addAttribute("nuevoAlumno", new Alumno());

        return "profesor-alumnos";
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

        return "profesor-alumno-notas";
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

            return "profesor-alumno-notas";
        }
    }

    @PostMapping("/profesor/{profesorId}/alumnos/guardar")
    public String guardarAlumnoComoProfesor(@PathVariable String profesorId) {
        return "redirect:/profesor/" + profesorId + "/alumnos";
    }
}
