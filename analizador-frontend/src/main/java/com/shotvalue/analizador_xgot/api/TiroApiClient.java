package com.shotvalue.analizador_xgot.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shotvalue.analizador_xgot.model.Tiro;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TiroApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    private static final String BASE_URL = "http://localhost:8080/api";

    public static List<Tiro> filtrarTiros(Map<String, String> filtros) throws Exception {
        String query = filtros.entrySet().stream()
                .map(entry -> URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8) + "=" +
                        URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));

        URI uri = URI.create(BASE_URL + "/tiros/buscar?" + query);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        Type listType = new TypeToken<List<Tiro>>() {}.getType();
        return gson.fromJson(response.body(), listType);
    }

}
