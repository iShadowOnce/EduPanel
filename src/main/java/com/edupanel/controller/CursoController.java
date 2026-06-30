package com.edupanel.controller;

import com.edupanel.exception.CalificacionInvalidaException;
import com.edupanel.model.Alumno;
import com.edupanel.model.Asignatura;
import com.edupanel.model.Calificacion;
import com.edupanel.model.Curso;
import com.edupanel.service.AlumnoService;
import com.edupanel.service.CalificacionService;
import com.edupanel.service.CursoService;
import java.util.HashMap;
import java.util.Map;
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
public class CursoController {

    private final CursoService cursoService;
    private final AlumnoService alumnoService;
    private final CalificacionService calificacionService;

    public CursoController(CursoService cursoService,
            AlumnoService alumnoService,
            CalificacionService calificacionService) {
        this.cursoService = cursoService;
        this.alumnoService = alumnoService;
        this.calificacionService = calificacionService;
    }

    @GetMapping("/profesor-jefe/cursos")
    public String verCursos(Model model) {
        model.addAttribute("cursos", cursoService.listarCursos());

        return "admin/cursos";
    }

    @GetMapping("/profesor-jefe/alumnos")
    public String verAlumnosRegistrados(Model model) {
        model.addAttribute("alumnos", alumnoService.listarAlumnos());
        model.addAttribute("cursoPorAlumnoId", crearMapaCursosPorAlumno());

        return "admin/alumnos";
    }

    @GetMapping("/profesor-jefe/cursos/{cursoId}/alumnos")
    public String gestionarAlumnosCurso(@PathVariable String cursoId, Model model) {
        Curso curso = cursoService.buscarPorId(cursoId);
        if (curso == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El curso indicado no existe.");
        }

        model.addAttribute("curso", curso);
        model.addAttribute("alumnos", alumnoService.listarAlumnos());

        return "admin/curso-alumnos";
    }

    @PostMapping("/profesor-jefe/cursos/{cursoId}/alumnos/{alumnoId}/asignar")
    public String asignarAlumnoACurso(@PathVariable String cursoId,
            @PathVariable String alumnoId) {
        cursoService.asignarAlumnoACurso(cursoId, alumnoId);

        return "redirect:/profesor-jefe/cursos/" + cursoId + "/alumnos";
    }

    @PostMapping("/profesor-jefe/cursos/{cursoId}/alumnos/{alumnoId}/quitar")
    public String quitarAlumnoDeCurso(@PathVariable String cursoId,
            @PathVariable String alumnoId) {
        cursoService.quitarAlumnoDeCurso(cursoId, alumnoId);

        return "redirect:/profesor-jefe/cursos/" + cursoId + "/alumnos";
    }

    @GetMapping("/profesor-jefe/cursos/{cursoId}/alumnos/{alumnoId}/notas")
    public String verNotasAlumnoComoAdmin(@PathVariable String cursoId,
            @PathVariable String alumnoId,
            Model model) {
        Curso curso = buscarCursoExistente(cursoId);
        validarAlumnoPerteneceAlCurso(curso, alumnoId);
        cargarModeloNotasAdmin(model, curso, alumnoId, new Calificacion());

        return "admin/alumno-notas";
    }

    @PostMapping("/profesor-jefe/cursos/{cursoId}/alumnos/{alumnoId}/notas/guardar")
    public String guardarNotaComoAdmin(@PathVariable String cursoId,
            @PathVariable String alumnoId,
            @ModelAttribute Calificacion nuevaCalificacion,
            Model model) {
        Curso curso = buscarCursoExistente(cursoId);
        validarAlumnoPerteneceAlCurso(curso, alumnoId);

        try {
            calificacionService.guardarComoAdmin(alumnoId, nuevaCalificacion);

            return "redirect:/profesor-jefe/cursos/" + cursoId + "/alumnos/" + alumnoId + "/notas";

        } catch (CalificacionInvalidaException e) {
            model.addAttribute("error", e.getMessage());
            cargarModeloNotasAdmin(model, curso, alumnoId, nuevaCalificacion);

            return "admin/alumno-notas";
        }
    }

    @GetMapping("/profesor-jefe/cursos/{cursoId}/alumnos/{alumnoId}/notas/{notaId}/editar")
    public String editarNotaComoAdmin(@PathVariable String cursoId,
            @PathVariable String alumnoId,
            @PathVariable String notaId,
            Model model) {
        Curso curso = buscarCursoExistente(cursoId);
        validarAlumnoPerteneceAlCurso(curso, alumnoId);

        Calificacion calificacion = calificacionService.buscarPorIdComoAdmin(alumnoId, notaId);
        if (calificacion == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La nota indicada no existe.");
        }

        model.addAttribute("curso", curso);
        model.addAttribute("alumno", buscarAlumnoConNotas(alumnoId));
        model.addAttribute("calificacion", calificacion);
        model.addAttribute("asignaturas", Asignatura.values());

        return "admin/editar-nota";
    }

    @PostMapping("/profesor-jefe/cursos/{cursoId}/alumnos/{alumnoId}/notas/{notaId}/actualizar")
    public String actualizarNotaComoAdmin(@PathVariable String cursoId,
            @PathVariable String alumnoId,
            @PathVariable String notaId,
            @ModelAttribute Calificacion calificacionActualizada,
            Model model) {
        Curso curso = buscarCursoExistente(cursoId);
        validarAlumnoPerteneceAlCurso(curso, alumnoId);

        try {
            calificacionService.actualizarComoAdmin(alumnoId, notaId, calificacionActualizada);

            return "redirect:/profesor-jefe/cursos/" + cursoId + "/alumnos/" + alumnoId + "/notas";

        } catch (CalificacionInvalidaException e) {
            calificacionActualizada.setId(notaId);
            calificacionActualizada.setAlumnoId(alumnoId);

            model.addAttribute("error", e.getMessage());
            model.addAttribute("curso", curso);
            model.addAttribute("alumno", buscarAlumnoConNotas(alumnoId));
            model.addAttribute("calificacion", calificacionActualizada);
            model.addAttribute("asignaturas", Asignatura.values());

            return "admin/editar-nota";
        }
    }

    @PostMapping("/profesor-jefe/cursos/{cursoId}/alumnos/{alumnoId}/notas/{notaId}/eliminar")
    public String eliminarNotaComoAdmin(@PathVariable String cursoId,
            @PathVariable String alumnoId,
            @PathVariable String notaId,
            RedirectAttributes redirectAttributes) {
        Curso curso = buscarCursoExistente(cursoId);
        validarAlumnoPerteneceAlCurso(curso, alumnoId);

        try {
            calificacionService.eliminarComoAdmin(alumnoId, notaId);
        } catch (CalificacionInvalidaException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profesor-jefe/cursos/" + cursoId + "/alumnos/" + alumnoId + "/notas";
    }

    private void cargarModeloNotasAdmin(Model model,
            Curso curso,
            String alumnoId,
            Calificacion nuevaCalificacion) {
        Alumno alumno = buscarAlumnoConNotas(alumnoId);

        model.addAttribute("curso", curso);
        model.addAttribute("alumno", alumno);
        model.addAttribute("nuevaCalificacion", nuevaCalificacion);
        model.addAttribute("asignaturas", Asignatura.values());
        model.addAttribute("resumenesAsignatura", calificacionService.resumirPorAsignatura(alumno.getNotas()));
    }

    private Curso buscarCursoExistente(String cursoId) {
        Curso curso = cursoService.buscarPorId(cursoId);
        if (curso == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El curso indicado no existe.");
        }

        return curso;
    }

    private Alumno buscarAlumnoConNotas(String alumnoId) {
        Alumno alumno = alumnoService.buscarPorId(alumnoId);
        if (alumno == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El alumno indicado no existe.");
        }

        alumno.setNotas(calificacionService.listarPorAlumno(alumnoId));
        return alumno;
    }

    private void validarAlumnoPerteneceAlCurso(Curso curso, String alumnoId) {
        if (curso.getAlumnosIds() == null || !curso.getAlumnosIds().contains(alumnoId)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El alumno no pertenece al curso seleccionado.");
        }
    }

    private Map<String, Curso> crearMapaCursosPorAlumno() {
        Map<String, Curso> cursoPorAlumnoId = new HashMap<>();

        for (Curso curso : cursoService.listarCursos()) {
            if (curso.getAlumnosIds() == null) {
                continue;
            }

            for (String alumnoId : curso.getAlumnosIds()) {
                cursoPorAlumnoId.put(alumnoId, curso);
            }
        }

        return cursoPorAlumnoId;
    }
}
