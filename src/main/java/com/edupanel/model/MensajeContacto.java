package com.edupanel.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeContacto {

    private String id;
    private String profesorId;
    private String profesorNombre;
    private String profesorEmail;
    private String asunto;
    private String mensaje;
    private PrioridadMensaje prioridad;
    private LocalDateTime fechaEnvio;
}
