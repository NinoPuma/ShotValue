package com.shotvalue.analizador_xgot.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shotvalue.analizador_xgot.model.Tiro;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TiroApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    private static final String BASE_URL = "http://localhost:8080/api/tiros";

    public static List<Tiro> filtrarTiros(Map<String, String> filtros) throws Exception {
        String json = gson.toJson(filtros);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/filtrar"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("🧪 JSON recibido: " + response.body()); // para depuración

        Type listType = new TypeToken<List<Tiro>>() {}.getType();
        return gson.fromJson(response.body(), listType);
    }

    /**
     * Llama al endpoint POST /api/tiros para crear un nuevo tiro.
     * Devuelve un CompletableFuture<Tiro> con el objeto devuelto por el backend (ID, xgot, etc.).
     */
    public static CompletableFuture<Tiro> saveTiroAsync(Tiro tiro) {
        String json = gson.toJson(tiro);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        return client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(resp -> {
                    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
                        // Parsear la respuesta a nuestro modelo Tiro
                        return gson.fromJson(resp.body(), Tiro.class);
                    } else {
                        throw new RuntimeException("Error (" + resp.statusCode() + "): " + resp.body());
                    }
                });
    }
}
