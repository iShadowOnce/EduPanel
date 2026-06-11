package com.edupanel.repository;

import java.util.List;

import com.edupanel.model.Usuario;

public interface UsuarioRepository {

    void guardar(Usuario usuario);

    Usuario buscarPorId(String id);

    Usuario buscarPorEmail(String email);

    List<Usuario> listarTodos();

    List<Usuario> listarPendientes();

    void actualizar(Usuario usuario);

    void eliminar(String id);

}
