package com.shotvalue.analizador_xgot.api;

import java.net.http.*;
import java.net.URI;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class JugadorApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    private static final String BASE_URL = "http://localhost:8080/api/jugadores";

    public List<String> getNombresCompletos() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/nombres"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            return gson.fromJson(response.body(), new TypeToken<List<String>>(){}.getType());
        } else {
            throw new RuntimeException("Error al obtener nombres completos: " + response.statusCode());
        }
    }
}
