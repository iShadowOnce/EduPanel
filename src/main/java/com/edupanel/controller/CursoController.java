package com.edupanel.controller;

import com.edupanel.model.Curso;
import com.edupanel.service.AlumnoService;
import com.edupanel.service.CursoService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

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

        return "admin/cursos";
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
}
