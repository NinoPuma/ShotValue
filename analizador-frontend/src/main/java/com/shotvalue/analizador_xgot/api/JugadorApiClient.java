package com.shotvalue.analizador_xgot.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shotvalue.analizador_xgot.model.Jugador;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Cliente REST para /api/jugadores
 *  Sólo contiene métodos estáticos (no se instancia).
 */
public final class JugadorApiClient {

    private static final HttpClient CLIENT   = HttpClient.newHttpClient();
    private static final Gson       GSON     = new Gson();
    private static final String     BASE_URL = "http://localhost:8080/api/jugadores";


    /** GET /api/jugadores/nombres */
    public static List<String> getNombresCompletos() throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/nombres"))
                .GET()
                .build();

        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 200) {
            return GSON.fromJson(resp.body(), new TypeToken<List<String>>(){}.getType());
        }
        throw new RuntimeException("Error (" + resp.statusCode() + "): " + resp.body());
    }

    /** GET /api/jugadores/porEquipo/{teamId} */
    public static List<Jugador> getJugadoresPorEquipo(int teamId) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/porEquipo/" + teamId))
                .GET()
                .build();

        HttpResponse<String> resp = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 200) {
            return GSON.fromJson(resp.body(), new TypeToken<List<Jugador>>(){}.getType());
        }
        throw new RuntimeException("Error (" + resp.statusCode() + "): " + resp.body());
    }


    /** POST /api/jugadores (asíncrono) */
    public static CompletableFuture<Void> saveJugadorAsync(Jugador jugador) {
        String json = GSON.toJson(jugador);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        return CLIENT.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenAccept(resp -> {
                    if (resp.statusCode() < 200 || resp.statusCode() >= 300) {
                        throw new RuntimeException("Error ("+resp.statusCode()+"): " + resp.body());
                    }
                });
    }

}
