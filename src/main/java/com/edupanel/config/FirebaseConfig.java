package com.edupanel.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.IOException;
import java.io.InputStream;

public class FirebaseConfig {
    public static void inicializar() {
        try {
            if (!FirebaseApp.getApps().isEmpty()) {
                System.out.println("Firebase ya estaba inicializado");
                return;
            }

            InputStream flujoCuentaServicio  = FirebaseConfig.class
                    .getClassLoader()
                    .getResourceAsStream("firebase-config.json");

            FirebaseOptions opcion  = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(flujoCuentaServicio ))
                    .setDatabaseUrl("https://poocalificaciones-default-rtdb.firebaseio.com")
                    .build();

            FirebaseApp.initializeApp(opcion );
            System.out.println("Firebase inicializado correctamente.");

        } catch (IOException e) {
            throw new RuntimeException("Error al inicializar Firebase: " + e.getMessage(), e);
        }
    }
}
