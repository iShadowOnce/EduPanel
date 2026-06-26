package com.edupanel.repository.firebase;

import com.google.api.core.ApiFuture;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

abstract class FirebaseRepositorySupport {


    protected DatabaseReference referencia(String coleccion) {
        return FirebaseDatabase.getInstance().getReference(coleccion);
    }

    protected <TipoResultado> TipoResultado esperar(ApiFuture<TipoResultado> futuro) {
        try {
            return futuro.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Operacion Firebase interrumpida.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error al ejecutar operacion Firebase.", e);
        }
    }

    protected DataSnapshot leer(DatabaseReference referencia) {
        CompletableFuture<DataSnapshot> futuro = new CompletableFuture<>();
        referencia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                futuro.complete(snapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                futuro.completeExceptionally(error.toException());
            }
        });

        try {
            return futuro.get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Lectura Firebase interrumpida.", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error al leer datos desde Firebase.", e);
        }
    }

    protected <TipoModelo> TipoModelo buscarPorId(DatabaseReference referencia, String id, Class<TipoModelo> tipo) {
        DataSnapshot snapshot = leer(referencia.child(id));
        if (!snapshot.exists()) {
            return null;
        }

        return snapshot.getValue(tipo);
    }

    protected <TipoModelo> List<TipoModelo> listarTodos(DatabaseReference referencia, Class<TipoModelo> tipo) {
        DataSnapshot snapshot = leer(referencia);
        List<TipoModelo> resultados = new ArrayList<>();

        for (DataSnapshot hijo : snapshot.getChildren()) {
            TipoModelo valor = hijo.getValue(tipo);
            if (valor != null) {
                resultados.add(valor);
            }
        }

        return resultados;
    }
}