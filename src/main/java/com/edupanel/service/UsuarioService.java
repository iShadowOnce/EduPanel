package com.edupanel.service;

import com.edupanel.exception.UsuarioDuplicadoException;
import com.edupanel.exception.UsuarioNoEncontradoException;
import com.edupanel.model.Alumno;
import com.edupanel.model.Profesor;
import com.edupanel.model.Rol;
import com.edupanel.model.Usuario;
import com.edupanel.model.UsuarioPendiente;
import com.edupanel.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          AlumnoService alumnoService,
                          ProfesorService profesorService) {
        this.usuarioRepository = usuarioRepository;
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
    }

    public void registrarUsuarioPendiente(UsuarioPendiente usuario) {
        validarUsuario(usuario);

        Usuario usuarioExistente = usuarioRepository.buscarPorEmail(usuario.getEmail());

        if (usuarioExistente != null
                || alumnoService.buscarPorEmail(usuario.getEmail()) != null
                || profesorService.buscarPorEmail(usuario.getEmail()) != null) {
            throw new UsuarioDuplicadoException("Ya existe un usuario registrado con ese email.");
        }

        usuario.setUid(UUID.randomUUID().toString());
        usuario.setRol(Rol.PENDIENTE);

        usuarioRepository.guardar(usuario);
    }

    public List<Usuario> listarUsuariosPendientes() {
        return usuarioRepository.listarPendientes();
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }

    public void asignarRol(String usuarioId, Rol nuevoRol) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId);

        if (usuario == null) {
            throw new UsuarioNoEncontradoException("No se encontró el usuario.");
        }

        if (nuevoRol == Rol.ALUMNO) {
            Alumno alumno = new Alumno();
            alumno.setUid(usuario.getUid());
            alumno.setNombre(usuario.getNombre());
            alumno.setApellido(usuario.getApellido());
            alumno.setRut(usuario.getRut());
            alumno.setEmail(usuario.getEmail());
            alumno.setPassword(usuario.getPassword());

            alumnoService.guardarAlumno(alumno);
            usuarioRepository.eliminar(usuarioId);
            return;
        }

        if (nuevoRol == Rol.PROFESOR) {
            Profesor profesor = new Profesor();
            profesor.setUid(usuario.getUid());
            profesor.setNombre(usuario.getNombre());
            profesor.setApellido(usuario.getApellido());
            profesor.setRut(usuario.getRut());
            profesor.setEmail(usuario.getEmail());
            profesor.setPassword(usuario.getPassword());

            profesorService.guardarProfesor(profesor);
            usuarioRepository.eliminar(usuarioId);
        }
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }

        if (usuario.getApellido() == null || usuario.getApellido().isBlank()) {
            throw new IllegalArgumentException("El apellido es obligatorio.");
        }

        if (usuario.getRut() == null || usuario.getRut().isBlank()) {
            throw new IllegalArgumentException("El RUT es obligatorio.");
        }

        if (usuario.getEmail() == null || usuario.getEmail().isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }

        if (usuario.getPassword() == null || usuario.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contrasena es obligatoria.");
        }

        if (usuario.getPassword().length() < 6) {
            throw new IllegalArgumentException("La contrasena debe tener al menos 6 caracteres.");
        }
    }
}
