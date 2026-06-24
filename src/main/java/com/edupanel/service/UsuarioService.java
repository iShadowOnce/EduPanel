package com.edupanel.service;

import com.edupanel.exception.UsuarioNoEncontradoException;
import com.edupanel.model.Alumno;
import com.edupanel.model.Profesor;
import com.edupanel.model.Rol;
import com.edupanel.model.Usuario;
import com.edupanel.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

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

    public List<Usuario> listarUsuariosPendientes() {
        return usuarioRepository.listarPendientes();
    }

    public Usuario buscarPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }

    public void asignarRol(String usuarioId, Rol nuevoRol) {
        Usuario usuario = usuarioRepository.buscarPorId(usuarioId);

        if (usuario == null) {
            throw new UsuarioNoEncontradoException("No se encontro el usuario.");
        }

        if (nuevoRol == Rol.ALUMNO) {
            usuario.setRol(Rol.ALUMNO);
            usuarioRepository.actualizar(usuario);

            Alumno alumno = new Alumno();
            alumno.setUid(usuario.getUid());
            alumno.setNombre(usuario.getNombre());
            alumno.setApellido(usuario.getApellido());
            alumno.setRut(usuario.getRut());
            alumno.setEmail(usuario.getEmail());
            // TODO Firebase: este password solo existe para el modo temporal en memoria.
            // Con Firebase Auth debe venir null y no guardarse en Realtime Database.
            alumno.setPassword(usuario.getPassword());

            alumnoService.guardarAlumno(alumno);
            return;
        }

        if (nuevoRol == Rol.PROFESOR) {
            usuario.setRol(Rol.PROFESOR);
            usuarioRepository.actualizar(usuario);

            Profesor profesor = new Profesor();
            profesor.setUid(usuario.getUid());
            profesor.setNombre(usuario.getNombre());
            profesor.setApellido(usuario.getApellido());
            profesor.setRut(usuario.getRut());
            profesor.setEmail(usuario.getEmail());
            // TODO Firebase: este password solo existe para el modo temporal en memoria.
            // Con Firebase Auth debe venir null y no guardarse en Realtime Database.
            profesor.setPassword(usuario.getPassword());

            profesorService.guardarProfesor(profesor);
        }
    }
}
