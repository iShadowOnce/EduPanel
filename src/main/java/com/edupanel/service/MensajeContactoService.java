package com.edupanel.service;

import com.edupanel.exception.MensajeContactoInvalidoException;
import com.edupanel.model.MensajeContacto;
import com.edupanel.model.Profesor;
import com.edupanel.repository.MensajeContactoRepository;
import com.edupanel.repository.ProfesorRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class MensajeContactoService {

    private final MensajeContactoRepository mensajeContactoRepository;
    private final ProfesorRepository profesorRepository;

    public MensajeContactoService(MensajeContactoRepository mensajeContactoRepository,
            ProfesorRepository profesorRepository) {
        this.mensajeContactoRepository = mensajeContactoRepository;
        this.profesorRepository = profesorRepository;
    }

    public List<MensajeContacto> listarMensajes() {
        return mensajeContactoRepository.listarTodos();
    }

    public void enviarMensaje(String profesorId, MensajeContacto mensajeContacto) {
        Profesor profesor = profesorId == null || profesorId.isBlank()
                ? null
                : profesorRepository.buscarPorId(profesorId);

        if (profesor == null) {
            throw new MensajeContactoInvalidoException("El profesor indicado no existe.");
        }

        validarMensaje(mensajeContacto);

        mensajeContacto.setId(UUID.randomUUID().toString());
        mensajeContacto.setProfesorId(profesorId);
        mensajeContacto.setProfesorNombre(profesor.getNombre() + " " + profesor.getApellido());
        mensajeContacto.setProfesorEmail(profesor.getEmail());
        mensajeContacto.setAsunto(mensajeContacto.getAsunto().trim());
        mensajeContacto.setMensaje(mensajeContacto.getMensaje().trim());
        mensajeContacto.setFechaEnvio(LocalDateTime.now());

        mensajeContactoRepository.guardar(mensajeContacto);
    }

    private void validarMensaje(MensajeContacto mensajeContacto) {
        if (mensajeContacto == null) {
            throw new MensajeContactoInvalidoException("Los datos del mensaje son obligatorios.");
        }

        if (mensajeContacto.getAsunto() == null || mensajeContacto.getAsunto().isBlank()) {
            throw new MensajeContactoInvalidoException("El asunto del mensaje es obligatorio.");
        }

        if (mensajeContacto.getMensaje() == null || mensajeContacto.getMensaje().isBlank()) {
            throw new MensajeContactoInvalidoException("El mensaje es obligatorio.");
        }

        if (mensajeContacto.getPrioridad() == null) {
            throw new MensajeContactoInvalidoException("Debe seleccionar una prioridad.");
        }
    }
}
