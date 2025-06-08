package com.shotvalue.analizador_xgot.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.util.LocalDateAdapter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class PerfilApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/usuarios";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .create();

    private PerfilApiClient() {}

    /** Recupera el perfil completo del usuario */
    public static CompletableFuture<Usuario> fetchProfile(String userId) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + userId))
                .GET()
                .build();

        return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() == 200) {
                        return GSON.fromJson(resp.body(), Usuario.class);
                    }
                    throw new RuntimeException("Error cargando perfil (" + resp.statusCode() + ")");
                });
    }

    /**
     * Actualiza username y/o contraseña.
     * El map puede contener "username", y opcionalmente "currentPassword" y "newPassword".
     */
    public static CompletableFuture<Void> updateProfile(String userId, Map<String,Object> payload) {
        StringBuilder uri = new StringBuilder(BASE_URL).append("/").append(userId);
        if (payload.containsKey("currentPassword") && payload.containsKey("newPassword")) {
            uri.append("?currentPassword=")
                    .append(payload.get("currentPassword"))
                    .append("&newPassword=")
                    .append(payload.get("newPassword"));
        }
        String bodyJson = GSON.toJson(Map.of("username", payload.get("username")));
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(uri.toString()))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(bodyJson, StandardCharsets.UTF_8))
                .build();

        return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.discarding())
                .thenAccept(resp -> {
                    if (resp.statusCode() != 200) {
                        throw new RuntimeException("Error guardando perfil (" + resp.statusCode() + ")");
                    }
                });
    }
}
