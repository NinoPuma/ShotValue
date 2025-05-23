package com.shotvalue.analizador_xgot.importador;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.InsertOneModel;
import com.mongodb.client.model.ReplaceOptions;
import com.mongodb.client.model.Filters;
import com.shotvalue.analizador_xgot.model.Evento;
import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.model.PlayPattern;
import com.shotvalue.analizador_xgot.util.HeightDeserializer;
import com.shotvalue.analizador_xgot.util.PlayPatternDeserializer;
import com.shotvalue.analizador_xgot.util.PartidosFiltradosUtil;
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
        // 1. Filtrar partidos válidos
        String carpetaMatches = "C:/Users/Santi/Downloads/open-data-master/open-data-master/data/matches";
        Set<String> idsValidos = PartidosFiltradosUtil.obtenerMatchIdsValidos(carpetaMatches);

        // 2. Cadena de conexión a MongoDB Atlas, apuntando a la base 'shotvalue'
        String connectionString =
                "mongodb+srv://santioca97:santioca97"
                        + "@xgot-cluster.gtbwx6p.mongodb.net/shotvalue"
                        + "?retryWrites=true"
                        + "&w=majority"
                        + "&appName=xGOT-cluster";

        try (MongoClient mongoClient = MongoClients.create(connectionString)) {
            MongoDatabase db = mongoClient.getDatabase("shotvalue");
            MongoCollection<Document> matchesCol = db.getCollection("matches");
            MongoCollection<Document> playersCol = db.getCollection("players");
            MongoCollection<Document> eventosCol = db.getCollection("eventos");

            // 3. Upsert de IDs de partidos en 'matches'
            ReplaceOptions upsert = new ReplaceOptions().upsert(true);
            for (String matchId : idsValidos) {
                Document mDoc = new Document("_id", matchId);
                matchesCol.replaceOne(Filters.eq("_id", matchId), mDoc, upsert);
            }

            // 4. Importación concurrente de eventos y jugadores
            String carpetaEventos = "C:/Users/Santi/Desktop/Partidos";
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
                                // Upsert de jugador
                                Jugador jugador = ev.getPlayer();
                                if (jugador != null) {
                                    String pid = String.valueOf(jugador.getPlayer_id());
                                    if (seenPlayers.add(pid)) {
                                        Document pDoc = Document.parse(gson.toJson(jugador));
                                        pDoc.put("_id", pid);
                                        playersCol.replaceOne(Filters.eq("_id", pid), pDoc, upsert);
                                    }
                                }

                                // Preparar evento y acumular para bulk write
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
                // Esperar finalización de todas las tareas y manejar posibles errores
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
            System.out.println("🏁 Importación completa de partidos, jugadores y eventos");
        }
    }
}
