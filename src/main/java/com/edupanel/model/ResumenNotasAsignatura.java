package com.edupanel.model;

import java.util.ArrayList;
import java.util.List;

public class ResumenNotasAsignatura {

    private final Asignatura asignatura;
    private final List<Calificacion> calificaciones = new ArrayList<>();

    public ResumenNotasAsignatura(Asignatura asignatura) {
        this.asignatura = asignatura;
    }

    public void agregar(Calificacion calificacion) {
        if (calificacion != null) {
            calificaciones.add(calificacion);
        }
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public String getNombreAsignatura() {
        return asignatura == null ? "Sin asignatura" : asignatura.getNombre();
    }

    public List<Calificacion> getCalificaciones() {
        return calificaciones;
    }

    public boolean isTieneCalificaciones() {
        return !calificaciones.isEmpty();
    }

    public double getPromedio() {
        if (calificaciones.isEmpty()) {
            return 0.0;
        }

        double suma = 0.0;

        for (Calificacion calificacion : calificaciones) {
            suma += calificacion.getNota();
        }

        return suma / calificaciones.size();
    }
}
