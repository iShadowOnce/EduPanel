package com.edupanel.service;

import com.edupanel.model.Alumno;
import com.edupanel.model.Calificacion;
import com.edupanel.model.Rol;
import com.edupanel.model.Asignatura;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlumnoService {

    private List<Alumno> alumnos = new ArrayList<>();

    public AlumnoService() {
        Alumno alumno1 = new Alumno();
        alumno1.setUid("1");
        alumno1.setNombre("Joaquín");
        alumno1.setApellido("Astudillo");
        alumno1.setRut("11.111.111-1");
        alumno1.setEmail("joaquin@correo.com");

        Calificacion nota1 = new Calificacion();
        nota1.setId("1");
        nota1.setAlumnoId("1");
        nota1.setAsignatura(Asignatura.MATEMATICAS);
        nota1.setNota(6.0);
        nota1.setDescripcion("Prueba 1");

        List<Calificacion> notas = new ArrayList<>();
        notas.add(nota1);

        alumno1.setNotas(notas);

        alumnos.add(alumno1);
    }

    public List<Alumno> listarAlumnos() {
        return alumnos;
    }

    public void guardarAlumno(Alumno alumno) {
        alumno.setRol(Rol.ALUMNO);

        if (alumno.getNotas() == null) {
            alumno.setNotas(new ArrayList<>());
        }

        alumnos.add(alumno);
    }

    public Alumno buscarPorId(String uid) {
        for (Alumno alumno : alumnos) {
            if (alumno.getUid().equals(uid)) {
                return alumno;
            }
        }

        return null;
    }

    public void agregarCalificacion(String alumnoId, Calificacion calificacion) {
        Alumno alumno = buscarPorId(alumnoId);

        if (alumno != null) {
            calificacion.setAlumnoId(alumnoId);

            if (alumno.getNotas() == null) {
                alumno.setNotas(new ArrayList<>());
            }

            alumno.getNotas().add(calificacion);
        }
    }
}