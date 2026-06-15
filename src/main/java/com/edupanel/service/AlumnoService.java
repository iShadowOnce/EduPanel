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

            if (alumno.getNotas() == null) {
                alumno.setNotas(new ArrayList<>());
            }

            calificacion.setId(String.valueOf(alumno.getNotas().size() + 1));
            calificacion.setAlumnoId(alumnoId);

            alumno.getNotas().add(calificacion);
        }
    }

    public void eliminarAlumno(String uid) {
        alumnos.removeIf(alumno -> alumno.getUid().equals(uid));
    }

    public void actualizarAlumno(String uid, Alumno datosActualizados) {
        Alumno alumnoExistente = buscarPorId(uid);

        if (alumnoExistente != null) {
            alumnoExistente.setNombre(datosActualizados.getNombre());
            alumnoExistente.setApellido(datosActualizados.getApellido());
            alumnoExistente.setRut(datosActualizados.getRut());
            alumnoExistente.setEmail(datosActualizados.getEmail());
        }
    }

    public Calificacion buscarCalificacionPorId(String alumnoId, String notaId) {
        Alumno alumno = buscarPorId(alumnoId);

        if (alumno != null && alumno.getNotas() != null) {
            for (Calificacion calificacion : alumno.getNotas()) {
                if (calificacion.getId().equals(notaId)) {
                    return calificacion;
                }
            }
        }

        return null;
    }

    public void actualizarCalificacion(String alumnoId, String notaId, Calificacion datosActualizados) {
        Calificacion calificacionExistente = buscarCalificacionPorId(alumnoId, notaId);

        if (calificacionExistente != null) {
            calificacionExistente.setAsignatura(datosActualizados.getAsignatura());
            calificacionExistente.setNota(datosActualizados.getNota());
            calificacionExistente.setDescripcion(datosActualizados.getDescripcion());
        }
    }

    public void eliminarCalificacion(String alumnoId, String notaId) {
        Alumno alumno = buscarPorId(alumnoId);

        if (alumno != null && alumno.getNotas() != null) {
            alumno.getNotas().removeIf(calificacion -> calificacion.getId().equals(notaId));
        }
    }
}