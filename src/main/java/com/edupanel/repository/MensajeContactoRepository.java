package com.edupanel.repository;

import com.edupanel.model.MensajeContacto;
import java.util.List;

public interface MensajeContactoRepository {

    void guardar(MensajeContacto mensajeContacto);

    List<MensajeContacto> listarTodos();
}
