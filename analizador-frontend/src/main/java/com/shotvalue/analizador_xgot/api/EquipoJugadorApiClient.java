package com.shotvalue.analizador_xgot.api;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.model.Jugador;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EquipoJugadorApiClient {

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    private static final String BASE_URL = "http://localhost:8080/api";

    public static List<Equipo> getEquipos() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/equipos"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<Equipo>>() {}.getType();
        return gson.fromJson(response.body(), listType);
    }

    public static List<Jugador> getJugadoresPorEquipo(int teamId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/jugadores/porEquipo/" + teamId))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type listType = new TypeToken<List<Jugador>>() {}.getType();
        return gson.fromJson(response.body(), listType);
    }
}
