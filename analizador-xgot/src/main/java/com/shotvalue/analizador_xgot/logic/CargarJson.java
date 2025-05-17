package com.shotvalue.analizador_xgot.logic;

import com.google.gson.*;
import com.shotvalue.analizador_xgot.model.Tiro;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CargarJson {

    private final Gson gson = new Gson();

    public List<Tiro> cargarTirosDesdeCarpeta(String ruta) {
        List<Tiro> tiros = new ArrayList<>();

        File carpeta = new File(ruta);
        if (carpeta.exists() && carpeta.isDirectory()) {
            File[] archivos = carpeta.listFiles((dir, name) -> name.toLowerCase().endsWith(".json"));
            if (archivos != null) {
                for (File archivo : archivos) {
                    tiros.addAll(cargarTirosDesdeArchivo(archivo));
                }
            }
        }
        return tiros;
    }

    private List<Tiro> cargarTirosDesdeArchivo(File archivo) {
        List<Tiro> tiros = new ArrayList<>();

        try (Reader reader = new FileReader(archivo)) {
            JsonElement root = JsonParser.parseReader(reader);
            if (root.isJsonArray()) {
                JsonArray eventos = root.getAsJsonArray();

                for (JsonElement elem : eventos) {
                    JsonObject evento = elem.getAsJsonObject();
                    String tipo = evento.getAsJsonObject("type").get("name").getAsString();

                    if ("Shot".equals(tipo)) {
                        JsonArray location = evento.getAsJsonArray("location");
                        double x = location.get(0).getAsDouble();
                        double y = location.get(1).getAsDouble();

                        JsonObject shot = evento.getAsJsonObject("shot");

                        String parteDelCuerpo = shot.has("body_part") ?
                                shot.getAsJsonObject("body_part").get("name").getAsString() : "Desconocido";

                        String tipoDeJugada = shot.has("type") ?
                                shot.getAsJsonObject("type").get("name").getAsString() : "Desconocido";

                        String resultado = shot.has("outcome") ?
                                shot.getAsJsonObject("outcome").get("name").getAsString() : "Desconocido";

                        double xgot = shot.has("statsbomb_xg") ?
                                shot.get("statsbomb_xg").getAsDouble() : 0.0;

                        Tiro tiro = new Tiro(x, y, parteDelCuerpo, tipoDeJugada, resultado, xgot);
                        tiros.add(tiro);
                    }
                }
            }

        } catch (IOException e) {
            System.err.println("Error al leer archivo: " + archivo.getName());
            e.printStackTrace();
        }

        return tiros;
    }
}

