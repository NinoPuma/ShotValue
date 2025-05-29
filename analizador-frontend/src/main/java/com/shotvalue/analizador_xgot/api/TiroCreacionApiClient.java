package com.shotvalue.analizador_xgot.api;

import com.google.gson.Gson;
import com.shotvalue.analizador_xgot.model.Tiro;
import com.shotvalue.analizador_xgot.model.TiroCreacion;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class TiroCreacionApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/tiros";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    /**
     * Envía un TiroCreacion al backend y devuelve el Tiro (con id) que devuelve el servicio.
     */
    public static CompletableFuture<Tiro> saveTiro(TiroCreacion payload) {
        // serializa el TiroCreacion
        String json = gson.toJson(payload);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    int code = resp.statusCode();
                    if (code >= 200 && code < 300) {
                        // parsea la respuesta como Tiro
                        return gson.fromJson(resp.body(), Tiro.class);
                    } else {
                        throw new RuntimeException("Error al guardar tiro (" + code + "): " + resp.body());
                    }
                });
    }
}
