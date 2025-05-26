package com.shotvalue.analizador_xgot.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.model.Tiro;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EstadisticasApiClient {

    private static final String BASE_URL = "http://localhost:8080/api";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();

    public static List<Tiro> getTiros() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/tiros"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<Tiro>>() {
        }.getType();
        return gson.fromJson(response.body(), listType);
    }

    public static List<Jugador> getJugadores() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/jugadores"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<Jugador>>() {
        }.getType();
        return gson.fromJson(response.body(), listType);
    }

    public static int getEquiposCount() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/equipos/count"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body().trim();

        System.out.println("📊 Respuesta equipos/count: '" + body + "'");

        if (body.isEmpty()) {
            System.out.println("⚠️ Respuesta vacía de /equipos/count, devolviendo 0");
            return 0;
        }

        try {
            return Integer.parseInt(body);
        } catch (NumberFormatException e) {
            System.out.println("❌ No se pudo parsear el número: " + body);
            return 0;
        }
    }

    public static int getPartidosCount() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/partidos/count"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        String body = response.body().trim();

        if (body.isEmpty()) {
            System.out.println("⚠️ Respuesta vacía de /partidos/count, devolviendo 0");
            return 0;
        }

        try {
            return Integer.parseInt(body);
        } catch (NumberFormatException e) {
            System.out.println("❌ No se pudo parsear el número: " + body);
            return 0;
        }
    }

}
