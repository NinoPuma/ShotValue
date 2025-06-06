package com.shotvalue.analizador_xgot.services;

import com.google.gson.*;
import com.mongodb.client.*;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class EquipoDataLoader {

    private final MongoCollection<Document> equiposCollection;

    public EquipoDataLoader(String uri) {
        MongoClient mongoClient = MongoClients.create(uri);
        MongoDatabase database = mongoClient.getDatabase("shotvalue");
        equiposCollection = database.getCollection("equipos");
    }

    public void cargarEquipoDesdeJson(String rutaLineupJson) {
        try {
            JsonElement root = JsonParser.parseReader(new FileReader(rutaLineupJson));
            JsonArray equiposArray = root.getAsJsonArray();

            for (JsonElement equipoElement : equiposArray) {
                JsonObject equipoJson = equipoElement.getAsJsonObject();

                int teamId = equipoJson.get("team_id").getAsInt();
                String nombreEquipo = equipoJson.get("team_name").getAsString();

                List<Document> jugadores = new ArrayList<>();
                JsonArray jugadoresArray = equipoJson.getAsJsonArray("lineup");

                for (JsonElement jugadorElement : jugadoresArray) {
                    JsonObject jugadorJson = jugadorElement.getAsJsonObject();
                    int numero = jugadorJson.get("jersey_number").getAsInt();
                    String nombreJugador = jugadorJson.getAsJsonObject("player").get("name").getAsString();
                    String posicion = jugadorJson.getAsJsonObject("position").get("name").getAsString();

                    Document jugadorDoc = new Document("numero", numero)
                            .append("nombre", nombreJugador)
                            .append("posicion", posicion);

                    jugadores.add(jugadorDoc);
                }

                Document equipoDoc = new Document("teamId", teamId)
                        .append("nombre", nombreEquipo)
                        .append("entrenador", "Desconocido")
                        .append("color", "Blanco")
                        .append("jugadores", jugadores);

                equiposCollection.updateOne(
                        eq("teamId", teamId),
                        new Document("$set", equipoDoc),
                        new UpdateOptions().upsert(true)
                );

                System.out.println("Equipo " + nombreEquipo + " insertado/actualizado correctamente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

// USO:
// EquipoDataLoader importer = new EquipoDataLoader("TU_URI_ATLAS");
// importer.cargarEquipoDesdeJson("src/main/resources/data/lineups/1234.json");