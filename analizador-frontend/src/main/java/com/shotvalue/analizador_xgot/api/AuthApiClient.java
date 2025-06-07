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
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public final class AuthApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient CLIENT;

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    static { CLIENT = HttpClient.newHttpClient(); }

    private AuthApiClient() {}

    public static CompletableFuture<Usuario> loginAsync(String email, String password) {

        Map<String,String> body = Map.of("email", email, "password", password);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(GSON.toJson(body), StandardCharsets.UTF_8))
                .build();

        return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() == 200) {
                        return GSON.fromJson(resp.body(), Usuario.class);
                    }
                    throw new RuntimeException("Login fallido ("+resp.statusCode()+"): " + resp.body());
                });
    }
}
