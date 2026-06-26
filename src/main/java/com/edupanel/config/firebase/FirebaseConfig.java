package com.edupanel.config.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
@Profile("firebase")
public class FirebaseConfig {

    private static final Logger logger = LoggerFactory.getLogger(FirebaseConfig.class);

    @PostConstruct
    public void inicializar() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                logger.info("Firebase ya estaba inicializado");
                return;
            }

            InputStream flujoCuentaServicio = FirebaseConfig.class
                    .getClassLoader()
                    .getResourceAsStream("firebase-config.json");

            if (flujoCuentaServicio == null) {
                throw new RuntimeException("No se encontró firebase-config.json en resources");
            }

            FirebaseOptions opcion = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(flujoCuentaServicio))
                    .setDatabaseUrl("https://poocalificaciones-default-rtdb.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(opcion);
            logger.info("Firebase inicializado correctamente.");

        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar Firebase: " + e.getMessage(), e);
        }
    }
}
