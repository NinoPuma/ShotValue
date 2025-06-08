package com.shotvalue.analizador_xgot.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shotvalue.analizador_xgot.model.Equipo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;


public class EquiposApiClient {
    private static final String BASE_URL = "http://localhost:8080/api/equipos";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    /** Llamada síncrona: devuelve la lista de equipos. */
    public static List<Equipo> getEquiposSync() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
            return gson.fromJson(
                    resp.body(),
                    new TypeToken<List<Equipo>>(){}.getType()
            );
        } else {
            throw new RuntimeException("Error cargando equipos: " + resp.statusCode());
        }
    }

    /** Llamada asíncrona: devuelve un CompletableFuture con la lista de equipos. */
    public static CompletableFuture<List<Equipo>> getEquiposAsync() {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .GET()
                .build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        return gson.fromJson(
                                resp.body(),
                                new TypeToken<List<Equipo>>(){}.getType()
                        );
                    } else {
                        throw new RuntimeException("Error cargando equipos: " + resp.statusCode());
                    }
                });
    }
    public static CompletableFuture<Equipo> saveEquipoAsync(Equipo nuevo) {
        String json = gson.toJson(nuevo);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        return gson.fromJson(resp.body(), Equipo.class);
                    } else {
                        throw new RuntimeException("Error ("+resp.statusCode()+"): "+resp.body());
                    }
                });
    }
}
