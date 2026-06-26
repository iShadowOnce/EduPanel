package com.edupanel.auth.firebase;

import com.edupanel.auth.AuthService;
import com.edupanel.auth.SesionUsuario;
import com.edupanel.model.Usuario;
import com.edupanel.repository.UsuarioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
@Profile("firebase")
public class FirebaseAuthService implements AuthService {

    private static final String URL_IDENTITY_TOOLKIT =
            "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=";

    private final UsuarioRepository usuarioRepository;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    @Value("${firebase.api-key}")
    private String firebaseApiKey;

    public FirebaseAuthService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public SesionUsuario autenticar(String email, String password) {
        validarCredenciales(email, password);

        String uid = autenticarConFirebase(email, password);

        Usuario usuario = usuarioRepository.buscarPorId(uid);

        if (usuario == null) {
            throw new IllegalArgumentException("No se encontró el perfil del usuario. Contacta al administrador.");
        }

        return new SesionUsuario(
                usuario.getUid(),
                usuario.getNombre(),
                usuario.getApellido(),
                usuario.getEmail(),
                usuario.getRol());
    }

    private void validarCredenciales(String email, String password) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email es obligatorio.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña es obligatoria.");
        }
    }

    private String autenticarConFirebase(String email, String password) {
        try {
            String cuerpoSolicitud = objectMapper.writeValueAsString(new SolicitudLogin(email, password, true));

            HttpRequest solicitud = HttpRequest.newBuilder()
                    .uri(URI.create(URL_IDENTITY_TOOLKIT + firebaseApiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(cuerpoSolicitud))
                    .build();

            HttpResponse<String> respuesta = httpClient.send(solicitud, HttpResponse.BodyHandlers.ofString());

            return procesarRespuestaFirebase(respuesta);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error al conectar con Firebase Authentication: " + e.getMessage(), e);
        }
    }

    private String procesarRespuestaFirebase(HttpResponse<String> respuesta) throws Exception {
        JsonNode cuerpoRespuesta = objectMapper.readTree(respuesta.body());

        if (respuesta.statusCode() != 200) {
            String codigoError = cuerpoRespuesta
                    .path("error")
                    .path("message")
                    .asText("");

            if ("EMAIL_NOT_FOUND".equals(codigoError) || "INVALID_PASSWORD".equals(codigoError)
                    || codigoError.startsWith("INVALID_LOGIN_CREDENTIALS")) {
                throw new IllegalArgumentException("Email o contraseña incorrectos.");
            }

            if ("USER_DISABLED".equals(codigoError)) {
                throw new IllegalArgumentException("Tu cuenta está deshabilitada. Contacta al administrador.");
            }

            throw new RuntimeException("Error de Firebase: " + codigoError);
        }

        return cuerpoRespuesta.path("localId").asText();
    }

    private record SolicitudLogin(String email, String password, boolean returnSecureToken) {}
}
