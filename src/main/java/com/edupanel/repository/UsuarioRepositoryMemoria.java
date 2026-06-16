package com.edupanel.repository;

import com.edupanel.model.Rol;
import com.edupanel.model.Usuario;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioRepositoryMemoria implements UsuarioRepository {

    private List<Usuario> usuarios = new ArrayList<>();

    @Override
    public void guardar(Usuario usuario) {
        usuarios.add(usuario);
    }

    @Override
    public Usuario buscarPorId(String id) {
        for (Usuario usuario : usuarios) {
            if (usuario.getUid().equals(id)) {
                return usuario;
            }
        }

        return null;
    }

    @Override
    public Usuario buscarPorEmail(String email) {
        for (Usuario usuario : usuarios) {
            if (usuario.getEmail().equalsIgnoreCase(email)) {
                return usuario;
            }
        }

        return null;
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarios;
    }

    @Override
    public List<Usuario> listarPendientes() {
        List<Usuario> pendientes = new ArrayList<>();

        for (Usuario usuario : usuarios) {
            if (usuario.getRol() == Rol.PENDIENTE) {
                pendientes.add(usuario);
            }
        }

        return pendientes;
    }

    @Override
    public void actualizar(Usuario usuarioActualizado) {
        Usuario usuarioExistente = buscarPorId(usuarioActualizado.getUid());

        if (usuarioExistente != null) {
            usuarioExistente.setNombre(usuarioActualizado.getNombre());
            usuarioExistente.setApellido(usuarioActualizado.getApellido());
            usuarioExistente.setRut(usuarioActualizado.getRut());
            usuarioExistente.setEmail(usuarioActualizado.getEmail());
            usuarioExistente.setRol(usuarioActualizado.getRol());
        }
    }

    @Override
    public void eliminar(String id) {
        usuarios.removeIf(usuario -> usuario.getUid().equals(id));
    }
}