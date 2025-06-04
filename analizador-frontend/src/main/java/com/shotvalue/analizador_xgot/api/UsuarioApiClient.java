package com.shotvalue.analizador_xgot.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.util.LocalDateAdapter;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

/**
 * Cliente HTTP para invocar los endpoints relacionados con Usuario.
 */
public class UsuarioApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/usuarios";
    private final HttpClient httpClient;
    private final Gson gson;

    public UsuarioApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    /**
     * GET /api/usuarios/{id} → devuelve un objeto Usuario o null si no existe.
     */
    public Usuario getUsuarioPorId(String id) throws Exception {
        String url = BASE_URL + "/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> resp = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 200) {
            // Aquí Gson usa el LocalDateAdapter modificado para tomar null sin error
            return gson.fromJson(resp.body(), Usuario.class);
        } else {
            return null;
        }
    }
}
