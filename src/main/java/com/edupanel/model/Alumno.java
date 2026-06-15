package com.edupanel.model;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class Alumno extends Usuario {

    private List<Calificacion> notas;

    public Alumno() {
        super();
        setRol(Rol.ALUMNO);
        this.notas = new ArrayList<>();
    }

    @Override
    public String obtenerRutaDashboard() {
        return "/alumno/dashboard";
    }

    public double calcularPromedio() {
        if (notas == null || notas.isEmpty()) {
            return 0.0;
        }

        double suma = 0;

        for (Calificacion calificacion : notas) {
            suma += calificacion.getNota();
        }

        return suma / notas.size();
    }

    public boolean estaAprobado() {
        return calcularPromedio() >= 4.0;
    }

    public String obtenerEstadoAcademico() {
        if (notas == null || notas.isEmpty()) {
            return "Sin notas";
        }

        if (estaAprobado()) {
            return "Aprobado";
        }

        return "Reprobado";
    }
}