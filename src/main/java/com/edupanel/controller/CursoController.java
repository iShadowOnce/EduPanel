package com.edupanel.controller;

import com.edupanel.exception.CursoInvalidoException;
import com.edupanel.model.Curso;
import com.edupanel.service.CursoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import com.edupanel.service.AlumnoService;

@Controller
public class CursoController {

    private final CursoService cursoService;
    private final AlumnoService alumnoService;

    public CursoController(CursoService cursoService, AlumnoService alumnoService) {
        this.cursoService = cursoService;
        this.alumnoService = alumnoService;
    }

    @GetMapping("/profesor-jefe/cursos")
    public String verCursos(Model model) {
        model.addAttribute("cursos", cursoService.listarCursos());
        model.addAttribute("nuevoCurso", new Curso());

        return "profesor-jefe-cursos";
    }

    @PostMapping("/profesor-jefe/cursos/guardar")
    public String guardarCurso(@ModelAttribute Curso nuevoCurso, Model model) {
        try {
            cursoService.guardarCurso(nuevoCurso);
            return "redirect:/profesor-jefe/cursos";

        } catch (CursoInvalidoException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("cursos", cursoService.listarCursos());
            model.addAttribute("nuevoCurso", nuevoCurso);

            return "profesor-jefe-cursos";
        }
    }

    @PostMapping("/profesor-jefe/cursos/{id}/eliminar")
    public String eliminarCurso(@PathVariable String id) {
        cursoService.eliminarCurso(id);

        return "redirect:/profesor-jefe/cursos";
    }

    @GetMapping("/profesor-jefe/cursos/{cursoId}/alumnos")
    public String gestionarAlumnosCurso(@PathVariable String cursoId, Model model) {
        model.addAttribute("curso", cursoService.buscarPorId(cursoId));
        model.addAttribute("alumnos", alumnoService.listarAlumnos());

        return "profesor-jefe-curso-alumnos";
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
}