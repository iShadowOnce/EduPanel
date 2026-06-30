package com.edupanel.repository.memory;

import com.edupanel.model.MensajeContacto;
import com.edupanel.repository.MensajeContactoRepository;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;

@Repository
public class MensajeContactoMemoriaRepository implements MensajeContactoRepository {

    private final Map<String, MensajeContacto> mensajes = new ConcurrentHashMap<>();

    @Override
    public void guardar(MensajeContacto mensajeContacto) {
        mensajes.put(mensajeContacto.getId(), mensajeContacto);
    }

    @Override
    public List<MensajeContacto> listarTodos() {
        return mensajes.values().stream()
                .sorted(Comparator.comparing(
                        MensajeContacto::getFechaEnvio,
                        Comparator.nullsLast(Comparator.<LocalDateTime>reverseOrder())))
                .toList();
    }
}
