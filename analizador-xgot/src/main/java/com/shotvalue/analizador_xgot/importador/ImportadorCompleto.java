package com.shotvalue.analizador_xgot.importador;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.shotvalue.analizador_xgot.model.Evento;
import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.model.PlayPattern;
import com.shotvalue.analizador_xgot.util.HeightDeserializer;
import com.shotvalue.analizador_xgot.util.PartidosFiltradosUtil;
import com.shotvalue.analizador_xgot.util.PlayPatternDeserializer;
import org.bson.Document;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;

public class ImportadorCompleto {

    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(PlayPattern.class, new PlayPatternDeserializer())
            .registerTypeAdapter(String.class, new HeightDeserializer())
            .create();

    public static void main(String[] args) throws IOException, InterruptedException {
        String carpetaMatches = "C:/Users/Santi/Downloads/open-data-master/open-data-master/data/matches";
        String carpetaEventos = "C:/Users/Santi/Desktop/Partidos";
        String carpetaLineups = "C:/Users/Santi/Downloads/open-data-master/open-data-master/data/lineups";
        Set<String> idsValidos = PartidosFiltradosUtil.obtenerMatchIdsValidos(carpetaMatches);

        String connectionString = "mongodb+srv://santioca97:santioca97@xgot-cluster.gtbwx6p.mongodb.net/shotvalue?retryWrites=true&w=majority&appName=xGOT-cluster";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase db = mongoClient.getDatabase("shotvalue");

            MongoCollection<Document> partidosCol = db.getCollection("partidos");
            MongoCollection<Document> jugadoresCol = db.getCollection("jugadores");
            MongoCollection<Document> eventosCol = db.getCollection("eventos");
            MongoCollection<Document> equiposCol = db.getCollection("equipos");

            ReplaceOptions upsert = new ReplaceOptions().upsert(true);

            // IMPORTAR EQUIPOS Y JUGADORES DESDE LINEUPS
            Set<Integer> equiposInsertados = new HashSet<>();
            Set<Integer> jugadoresInsertados = new HashSet<>();

            for (String matchId : idsValidos) {
                Path lineupFile = Paths.get(carpetaLineups, matchId + ".json");
                if (!Files.exists(lineupFile)) continue;

                try (FileReader reader = new FileReader(lineupFile.toFile())) {
                    JsonArray alineaciones = JsonParser.parseReader(reader).getAsJsonArray();

                    for (JsonElement equipoElem : alineaciones) {
                        JsonObject equipoObj = equipoElem.getAsJsonObject();

                        int teamId = equipoObj.get("team_id").getAsInt();
                        String teamName = equipoObj.get("team_name").getAsString();

                        // Guardar equipo
                        if (equiposInsertados.add(teamId)) {
                            Document equipoDoc = new Document("_id", teamId)
                                    .append("team_id", teamId)
                                    .append("team_name", teamName);
                            equiposCol.replaceOne(Filters.eq("_id", teamId), equipoDoc, upsert);
                            System.out.println("🟦 Equipo insertado: " + teamName);
                        }

                        // Guardar jugadores
                        JsonArray jugadoresJson = equipoObj.getAsJsonArray("lineup");
                        for (JsonElement jugadorElem : jugadoresJson) {
                            JsonObject jugadorObj = jugadorElem.getAsJsonObject();

                            int playerId = jugadorObj.get("player_id").getAsInt();
                            if (!jugadoresInsertados.add(playerId)) continue;

                            String playerName = jugadorObj.get("player_name").getAsString();
                            String position = jugadorObj.has("position") ? jugadorObj.get("position").getAsString() : null;
                            String jersey = jugadorObj.has("jersey_number") ? jugadorObj.get("jersey_number").getAsString() : null;

                            Document jugadorDoc = new Document("_id", playerId)
                                    .append("player_id", playerId)
                                    .append("player_name", playerName)
                                    .append("position", position)
                                    .append("jersey_number", jersey)
                                    .append("team_id", teamId)
                                    .append("team_name", teamName);

                            jugadoresCol.replaceOne(Filters.eq("_id", playerId), jugadorDoc, upsert);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error leyendo lineup " + lineupFile.getFileName() + ": " + e.getMessage());
                }
            }

            // IMPORTAR PARTIDOS FILTRADOS
            for (String matchId : idsValidos) {
                partidosCol.replaceOne(Filters.eq("_id", matchId), new Document("_id", matchId), upsert);
            }

            // IMPORTAR EVENTOS (opcional, filtrando sólo "Shot")
            ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
            Set<String> seenPlayers = ConcurrentHashMap.newKeySet();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(carpetaEventos), "*.json")) {
                List<Future<?>> futures = new ArrayList<>();

                for (Path eventoFile : stream) {
                    String filename = eventoFile.getFileName().toString();
                    String matchId = filename.substring(0, filename.lastIndexOf('.'));
                    if (!idsValidos.contains(matchId)) continue;

                    futures.add(pool.submit(() -> {
                        try (FileReader reader = new FileReader(eventoFile.toFile())) {
                            Type listType = new TypeToken<List<Evento>>() {}.getType();
                            List<Evento> eventos = gson.fromJson(reader, listType);
                            List<InsertOneModel<Document>> bulkEvents = new ArrayList<>();

                            for (Evento ev : eventos) {
                                if (ev.getType() == null || ev.getType().getName() == null) continue;
                                if (!"Shot".equals(ev.getType().getName())) continue;

                                Jugador jugador = ev.getPlayer();
                                if (jugador != null) {
                                    String pid = String.valueOf(jugador.getPlayer_id());

                                    if (seenPlayers.add(pid)) {
                                        Document pDoc = Document.parse(gson.toJson(jugador));
                                        pDoc.put("_id", pid);
                                        jugadoresCol.replaceOne(Filters.eq("_id", pid), pDoc, upsert);
                                    }
                                }

                                ev.setMatchId(matchId);
                                Document eDoc = Document.parse(gson.toJson(ev));
                                bulkEvents.add(new InsertOneModel<>(eDoc));
                            }

                            if (!bulkEvents.isEmpty()) {
                                eventosCol.bulkWrite(bulkEvents);
                                System.out.println("✅ " + bulkEvents.size() + " eventos importados de " + matchId);
                            }
                        } catch (Exception e) {
                            System.err.println("❌ Error procesando " + filename + ": " + e.getMessage());
                        }
                    }));
                }

                for (Future<?> f : futures) {
                    try {
                        f.get();
                    } catch (ExecutionException e) {
                        System.err.println("❌ Error en tarea concurrente: " + e.getCause());
                        e.getCause().printStackTrace();
                    }
                }
            }

            pool.shutdown();

            System.out.println("🎯 RESUMEN FINAL:");
            System.out.println(" - Partidos en 'partidos':     " + partidosCol.countDocuments());
            System.out.println(" - Jugadores en 'jugadores':   " + jugadoresCol.countDocuments());
            System.out.println(" - Equipos en 'equipos':       " + equiposCol.countDocuments());
            System.out.println(" - Eventos en 'eventos':       " + eventosCol.countDocuments());
            System.out.println("🏁 Importación completa");
        }
    }
}
