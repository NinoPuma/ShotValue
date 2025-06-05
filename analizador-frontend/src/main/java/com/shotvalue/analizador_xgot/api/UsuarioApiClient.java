// src/main/java/com/shotvalue/analizador_xgot/api/UsuarioApiClient.java
package com.shotvalue.analizador_xgot.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.util.LocalDateAdapter;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.LocalDate;

/**
 * Cliente HTTP que encapsula todas las operaciones REST relacionadas con <b>Usuario</b>.
 * <p>
 * Si el backend no acepta <code>PUT</code> para la edición del usuario y responde <i>405 Method Not Allowed</i>,
 * el cliente reintenta automáticamente usando <code>POST</code> en la misma URL. De esta forma funciona con
 * controladores Spring que estén anotados con <code>@PostMapping("/api/usuarios/{id}")</code> o
 * <code>@PutMapping</code> indistintamente.
 */
public class UsuarioApiClient {

    private static final String BASE_URL = "http://localhost:8080/api/usuarios";

    private final HttpClient http;
    private final Gson gson;

    public UsuarioApiClient() {
        this.http = HttpClient.newHttpClient();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    /* =============================================================
     * GET /api/usuarios/{id}
     * ============================================================= */
    public Usuario getUsuarioPorId(String id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .GET()
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        if (resp.statusCode() == 200) return gson.fromJson(resp.body(), Usuario.class);
        return null; // 404 → null, cualquier otro código se gestiona arriba.
    }

    /* =============================================================
     * PUT (o POST fallback) /api/usuarios/{id}
     * ============================================================= */
    public Usuario actualizarUsuario(String id,
                                     Usuario datos,
                                     String passActual,
                                     String passNueva) throws Exception {
        String json = gson.toJson(datos);

        // --- Construimos el request PUT ---
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json));

        if (passActual != null && passNueva != null) {
            builder.header("X-Current-Password", passActual)
                    .header("X-New-Password", passNueva);
        }

        HttpResponse<String> resp = http.send(builder.build(), HttpResponse.BodyHandlers.ofString());

        // --- Si el servidor responde 405, repetimos con POST ---
        if (resp.statusCode() == 405) {
            HttpRequest.Builder postBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/" + id))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json));
            if (passActual != null && passNueva != null) {
                postBuilder.header("X-Current-Password", passActual)
                        .header("X-New-Password", passNueva);
            }
            resp = http.send(postBuilder.build(), HttpResponse.BodyHandlers.ofString());
        }

        validar(resp.statusCode(), 200);
        return gson.fromJson(resp.body(), Usuario.class);
    }

    /* =============================================================
     * DELETE /api/usuarios/{id}
     * ============================================================= */
    public void eliminarUsuario(String id) throws Exception {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .DELETE()
                .build();

        HttpResponse<Void> resp = http.send(req, HttpResponse.BodyHandlers.discarding());
        validar(resp.statusCode(), 204);
    }

    /* =============================================================
     * POST /api/usuarios/{id}/avatar
     * ============================================================= */
    public String subirAvatar(String id, File imagen) throws Exception {
        String boundary = "----ShotValue" + System.currentTimeMillis();
        byte[] cuerpo = construirMultipart(boundary, imagen);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id + "/avatar"))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                .POST(HttpRequest.BodyPublishers.ofByteArray(cuerpo))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
        validar(resp.statusCode(), 200);
        return resp.body(); // URL pública devuelta por el backend
    }

    /* -------------------------------------------------------------
     * Helpers
     * ------------------------------------------------------------- */

    private byte[] construirMultipart(String boundary, File archivo) throws Exception {
        String CRLF = "\r\n";
        String mime = Files.probeContentType(archivo.toPath());
        if (mime == null) mime = "application/octet-stream";

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write(("--" + boundary + CRLF).getBytes(StandardCharsets.UTF_8));
        baos.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + archivo.getName() + "\"" + CRLF).getBytes(StandardCharsets.UTF_8));
        baos.write(("Content-Type: " + mime + CRLF + CRLF).getBytes(StandardCharsets.UTF_8));
        baos.write(Files.readAllBytes(archivo.toPath()));
        baos.write(CRLF.getBytes(StandardCharsets.UTF_8));
        baos.write(("--" + boundary + "--" + CRLF).getBytes(StandardCharsets.UTF_8));
        return baos.toByteArray();
    }

    private void validar(int statusCode, int esperado) {
        if (statusCode != esperado) {
            throw new RuntimeException("Error HTTP " + statusCode + " (esperado " + esperado + ")");
        }
    }
}
