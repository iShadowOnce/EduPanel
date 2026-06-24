package com.edupanel.auth;

import com.edupanel.exception.UsuarioDuplicadoException;
import com.edupanel.model.Rol;
import com.edupanel.model.Usuario;
import com.edupanel.model.UsuarioPendiente;
import com.edupanel.repository.UsuarioRepository;
import com.edupanel.service.AlumnoService;
import com.edupanel.service.ProfesorService;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("!firebase")
public class RegistroTemporalUsuarioService implements RegistroUsuarioService {

/*
NOTA JOACO: reemplazar por FirebaseRegistroUsuarioService.
Este flujo genera uid local y guarda password solo para pruebas en memoria.
*/ 
   
    private final UsuarioRepository usuarioRepository;
    private final AlumnoService alumnoService;
    private final ProfesorService profesorService;

    public RegistroTemporalUsuarioService(UsuarioRepository usuarioRepository,
            AlumnoService alumnoService,
            ProfesorService profesorService) {
        this.usuarioRepository = usuarioRepository;
        this.alumnoService = alumnoService;
        this.profesorService = profesorService;
    }

    @Override
    public void registrarUsuarioPendiente(UsuarioPendiente usuario) {
        RegistroUsuarioValidator.validar(usuario);

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
}
