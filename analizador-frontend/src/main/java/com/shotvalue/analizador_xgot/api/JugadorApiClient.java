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

public class JugadorApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    private static final String BASE_URL = "http://localhost:8080/api/jugadores";

    /**
     * Devuelve la lista de nombres completos.
     */
    public List<String> getNombresCompletos() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/nombres"))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<String>>(){}.getType());
        } else {
            throw new RuntimeException(
                    "Error al obtener nombres completos: " + response.statusCode());
        }
    }

    /**
     * Devuelve la lista de jugadores para el teamId indicado.
     */
    public List<Jugador> getJugadoresPorEquipo(int teamId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/porEquipo/" + teamId))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(
                    response.body(),
                    new TypeToken<List<Jugador>>(){}.getType()
            );
        } else {
            throw new RuntimeException(
                    "Error al obtener jugadores del equipo " + teamId + ": " + response.statusCode());
        }
    }

    public static CompletableFuture<Void> saveJugadorAsync(Jugador jugador){
        String json = gson.toJson(jugador);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type","application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenAccept(resp -> {
                    if(resp.statusCode() < 200 || resp.statusCode() >= 300){
                        throw new RuntimeException("Error al crear jugador: "+resp.body());
                    }
                });
    }

}
