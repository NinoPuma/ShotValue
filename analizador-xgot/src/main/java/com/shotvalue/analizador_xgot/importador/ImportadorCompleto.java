package com.shotvalue.analizador_xgot.importador;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.shotvalue.analizador_xgot.model.*;
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
            MongoCollection<Document> tirosCol = db.getCollection("tiros");

            ReplaceOptions upsert = new ReplaceOptions().upsert(true);
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

                        Document nuevoEquipo = new Document("_id", teamId)
                                .append("teamId", teamId)
                                .append("name", teamName);

                        Document existente = equiposCol.find(Filters.eq("_id", teamId)).first();
                        if (existente == null || !existente.equals(nuevoEquipo)) {
                            equiposCol.replaceOne(Filters.eq("_id", teamId), nuevoEquipo, upsert);
                            System.out.println("🟦 Equipo insertado: " + teamName);
                        }

                        JsonArray jugadoresJson = equipoObj.getAsJsonArray("lineup");
                        for (JsonElement jugadorElem : jugadoresJson) {
                            JsonObject jugadorObj = jugadorElem.getAsJsonObject();
                            int playerId = jugadorObj.get("player_id").getAsInt();
                            if (!jugadoresInsertados.add(playerId)) continue;

                            String playerName = jugadorObj.get("player_name").getAsString();
                            JsonArray positionsArray = jugadorObj.getAsJsonArray("positions");
                            String position = null;
                            if (positionsArray != null && positionsArray.size() > 0) {
                                JsonObject firstPosition = positionsArray.get(0).getAsJsonObject();
                                String posEnIngles = firstPosition.get("position").getAsString();
                                position = traducirPosicion(posEnIngles);
                            }

                            String jersey = jugadorObj.has("jersey_number") ? jugadorObj.get("jersey_number").getAsString() : null;

                            Document nuevoJugador = new Document("_id", playerId)
                                    .append("player_id", playerId)
                                    .append("player_name", playerName)
                                    .append("position", position)
                                    .append("jersey_number", jersey)
                                    .append("team_id", teamId)
                                    .append("team_name", teamName);

                            Document jugadorExistente = jugadoresCol.find(Filters.eq("_id", playerId)).first();
                            if (jugadorExistente == null || !jugadorExistente.equals(nuevoJugador)) {
                                jugadoresCol.replaceOne(Filters.eq("_id", playerId), nuevoJugador, upsert);
                            }
                        }
                    }
                } catch (Exception e) {
                    System.err.println("❌ Error leyendo lineup " + lineupFile.getFileName() + ": " + e.getMessage());
                }

                Document partidoExistente = partidosCol.find(Filters.eq("_id", matchId)).first();
                if (partidoExistente == null) {
                    partidosCol.insertOne(new Document("_id", matchId));
                }
            }

            ExecutorService pool = Executors.newFixedThreadPool(NUM_THREADS);
            Set<String> seenPlayers = ConcurrentHashMap.newKeySet();

            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(carpetaEventos), "*.json")) {
                List<Future<?>> futures = new ArrayList<>();

                for (Path eventoFile : stream) {
                    String filename = eventoFile.getFileName().toString();
                    String matchId = filename.substring(0, filename.lastIndexOf('.'));
                    if (!idsValidos.contains(matchId)) continue;

                    if (eventosCol.countDocuments(Filters.eq("matchId", matchId)) > 0) {
                        System.out.println("⏩ Eventos ya importados para " + matchId);
                        continue;
                    }

                    if (tirosCol.countDocuments(Filters.eq("partidoId", matchId)) > 0) {
                        System.out.println("⏩ Tiros ya importados para " + matchId);
                        continue;
                    }


                    futures.add(pool.submit(() -> {
                        try (FileReader reader = new FileReader(eventoFile.toFile())) {
                            Type listType = new TypeToken<List<Evento>>() {
                            }.getType();
                            List<Evento> eventos = gson.fromJson(reader, listType);
                            List<InsertOneModel<Document>> bulkEvents = new ArrayList<>();
                            List<InsertOneModel<Document>> tirosList = new ArrayList<>();

                            for (int idx = 0; idx < eventos.size(); idx++) {
                                Evento ev = eventos.get(idx);
                                if (ev.getType() == null || ev.getType().getName() == null) continue;
                                if (!"Shot".equals(ev.getType().getName())) continue;
                                if (ev.getShot() == null || ev.getLocation() == null || ev.getLocation().size() < 2)
                                    continue;

                                Jugador jugador = ev.getPlayer();
                                if (jugador == null) {
                                    System.out.println("⏭️  Tiro sin jugador: partido " + matchId + " minuto " + ev.getMinute());
                                    continue;
                                }

                                String pid = String.valueOf(jugador.getPlayerId());

                                if (seenPlayers.add(pid)) {
                                    Document pDoc = Document.parse(gson.toJson(jugador));
                                    pDoc.put("_id", pid);
                                    jugadoresCol.replaceOne(Filters.eq("_id", pid), pDoc, upsert);
                                }

                                ev.setMatchId(matchId);
                                Document eDoc = Document.parse(gson.toJson(ev));
                                if (ev.getIndex() != null) {
                                    eDoc.put("_id", matchId + "_" + ev.getIndex());
                                }
                                bulkEvents.add(new InsertOneModel<>(eDoc));

                                Tiro tiro = new Tiro();
                                tiro.setX(ev.getLocation().get(0));
                                tiro.setY(ev.getLocation().get(1));

                                if (ev.getShot().getEndLocation() != null && ev.getShot().getEndLocation().size() >= 2) {
                                    tiro.setDestinoX(ev.getShot().getEndLocation().get(0));
                                    tiro.setDestinoY(ev.getShot().getEndLocation().get(1));
                                    if (ev.getShot().getEndLocation().size() >= 3) {
                                        tiro.setDestinoZ(ev.getShot().getEndLocation().get(2));
                                    }
                                }

                                tiro.setResultado(traducir(ev.getShot().getOutcome() != null ? ev.getShot().getOutcome().getName() : "Desconocido", "resultado"));
                                tiro.setParteDelCuerpo(traducir(ev.getShot().getBodyPart() != null ? ev.getShot().getBodyPart().getName() : "Desconocida", "parte"));
                                String tecnica = ev.getShot().getTechnique() != null ? ev.getShot().getTechnique().getName() : null;
                                String tipoJugada = traducir(tecnica, "jugada");
                                tiro.setTipoDeJugada(tipoJugada != null ? tipoJugada : "Sin definir");
                                tiro.setZonaDelDisparo(traducir(ev.getShot().getZone() != null ? ev.getShot().getZone().getName() : "Sin zona", "zona"));

                                tiro.setSituation(inferirSituacion(ev.getPlay_pattern(), ev.getShot()));

                                String preAccion = "No definido";
                                if (ev.getShot().getType() != null &&
                                        "Penalty".equalsIgnoreCase(ev.getShot().getType().getName())) {
                                    preAccion = "Penal";
                                } else {
                                    for (int j = idx - 1; j >= 0; j--) {
                                        Evento previo = eventos.get(j);
                                        if (previo.getType() == null || previo.getType().getName() == null) continue;

                                        boolean mismoEquipo = previo.getTeam() != null && ev.getTeam() != null &&
                                                previo.getTeam().getTeamId() == ev.getTeam().getTeamId();

                                        if (!mismoEquipo) {
                                            break;
                                        }

                                        String tipoPrevio = previo.getType().getName();

                                        if ("Pass".equalsIgnoreCase(tipoPrevio)) {
                                            boolean esCentro = previo.getPass() != null &&
                                                    previo.getPass().getType() != null &&
                                                    previo.getPass().getType().equalsIgnoreCase("Cross");
                                            preAccion = esCentro ? "Centro" : "Pase";
                                            break;
                                        } else if ("Carry".equalsIgnoreCase(tipoPrevio) ||
                                                "Dribble".equalsIgnoreCase(tipoPrevio)) {
                                            preAccion = "Regate";
                                            break;
                                        } else if ("Shot".equalsIgnoreCase(tipoPrevio)) {
                                            preAccion = "Rebote";
                                            break;
                                        } else if ("Ball Receipt".equalsIgnoreCase(tipoPrevio)) {
                                            continue;
                                        } else {
                                            continue;
                                        }
                                    }
                                }
                                tiro.setPreAction(preAccion);

                                tiro.setXgot(ev.getShot().getStatsbombXg());
                                tiro.setMinuto(ev.getMinute());
                                tiro.setPeriod(ev.getPeriod());

                                String jugadorNombre = jugador.getPlayerName();

                                if (jugadorNombre == null || jugadorNombre.isBlank()) {
                                    Document encontrado = jugadoresCol.find(Filters.eq("player_id", jugador.getPlayerId())).first();
                                    if (encontrado != null && encontrado.containsKey("player_name")) {
                                        jugadorNombre = encontrado.getString("player_name");
                                    }
                                }

                                tiro.setJugadorId(pid);
                                tiro.setJugadorNombre(jugadorNombre);

                                if (ev.getTeam() != null) {
                                    tiro.setEquipoId(String.valueOf(ev.getTeam().getTeamId()));
                                    tiro.setEquipoNombre(ev.getTeam().getName());
                                }

                                tiro.setPartidoId(matchId);

                                System.out.println("🎯 Tiro importado:");
                                System.out.println(" - Jugador: " + tiro.getJugadorNombre());
                                System.out.println(" - Equipo: " + tiro.getEquipoNombre());
                                System.out.println(" - Minuto: " + tiro.getMinuto());
                                System.out.println(" - xGOT: " + tiro.getXgot());
                                System.out.println(" - De " + tiro.getX() + "," + tiro.getY() + " → " + tiro.getDestinoX() + "," + tiro.getDestinoY());
                                System.out.println("---------------------------------------------------");

                                Document tiroDoc = Document.parse(new Gson().toJson(tiro));
                                if (ev.getIndex() != null) {
                                    tiroDoc.put("_id", matchId + "_" + ev.getIndex());
                                }
                                tirosList.add(new InsertOneModel<>(tiroDoc));
                            }


                            if (!bulkEvents.isEmpty()) eventosCol.bulkWrite(bulkEvents);
                            if (!tirosList.isEmpty()) tirosCol.bulkWrite(tirosList);

                            System.out.println("✅ " + bulkEvents.size() + " eventos y " + tirosList.size() + " tiros importados de " + matchId);
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
                    }
                }
            }


            pool.shutdown();

            System.out.println("🎯 RESUMEN FINAL:");
            System.out.println(" - Partidos en 'partidos':     " + partidosCol.countDocuments());
            System.out.println(" - Jugadores en 'jugadores':   " + jugadoresCol.countDocuments());
            System.out.println(" - Equipos en 'equipos':       " + equiposCol.countDocuments());
            System.out.println(" - Eventos en 'eventos':       " + eventosCol.countDocuments());
            System.out.println(" - Tiros en 'tiros':           " + tirosCol.countDocuments());
            System.out.println("🏁 Importación completa");
        }
    }

    private static String traducirPosicion(String posicionIngles) {
        Map<String, String> traducciones = new HashMap<>();
        traducciones.put("Goalkeeper", "Arquero");
        traducciones.put("Right Back", "Lateral Derecho");
        traducciones.put("Left Back", "Lateral Izquierdo");
        traducciones.put("Center Back", "Defensor Central");
        traducciones.put("Right Wing Back", "Carrilero Derecho");
        traducciones.put("Left Wing Back", "Carrilero Izquierdo");

        traducciones.put("Defensive Midfield", "Mediocentro Defensivo");
        traducciones.put("Center Midfield", "Mediocampista Central");
        traducciones.put("Left Center Midfield", "Mediocampista Izquierdo");
        traducciones.put("Right Center Midfield", "Mediocampista Derecho");
        traducciones.put("Attacking Midfield", "Mediapunta");
        traducciones.put("Center Attacking Midfield", "Mediapunta Central");
        traducciones.put("Center Defensive Midfield", "Mediocentro Defensivo Central");

        traducciones.put("Left Center Back", "Defensor Central Izquierdo");
        traducciones.put("Right Center Back", "Defensor Central Derecho");

        traducciones.put("Center Forward", "Delantero Centro");
        traducciones.put("Right Center Forward", "Delantero Derecho");
        traducciones.put("Left Center Forward", "Delantero Izquierdo");

        traducciones.put("Right Wing", "Extremo Derecho");
        traducciones.put("Left Wing", "Extremo Izquierdo");
        traducciones.put("Striker", "Delantero");
        traducciones.put("Second Striker", "Segundo Delantero");

        traducciones.put("Left Defensive Midfield", "Mediocentro Defensivo Izquierdo");
        traducciones.put("Right Defensive Midfield", "Mediocentro Defensivo Derecho");
        traducciones.put("Right Midfield", "Mediocampista Derecho");
        traducciones.put("Left Midfield", "Mediocampista Izquierdo");

        traducciones.put("Attacking Midfield", "Mediapunta");
        traducciones.put("Center Attacking Midfield", "Mediapunta Central");
        traducciones.put("Left Attacking Midfield", "Mediapunta Izquierdo");
        traducciones.put("Right Attacking Midfield", "Mediapunta Derecho");


        if (!traducciones.containsKey(posicionIngles)) {
            System.out.println("⚠️ Posición sin traducir: " + posicionIngles);
        }

        return traducciones.getOrDefault(posicionIngles, posicionIngles);
    }

    private static String traducir(String valor, String tipo) {
        if (valor == null) return "Sin definir";

        return switch (tipo) {
            case "resultado" -> switch (valor.toLowerCase()) {
                case "goal" -> "Gol";
                case "saved" -> "Atajado";
                case "off t" -> "Fuera";
                case "blocked" -> "Bloqueado";
                case "post" -> "Poste";
                default -> "Desconocido";
            };
            case "parte" -> switch (valor.toLowerCase()) {
                case "left foot" -> "Pie izquierdo";
                case "right foot" -> "Pie derecho";
                case "head" -> "Cabeza";
                case "other" -> "Otro";
                default -> "Otro";
            };
            case "jugada" -> switch (valor.toLowerCase()) {
                case "normal" -> "Normal";
                case "lob" -> "Lob";
                case "half volley" -> "Media volea";
                case "volley" -> "Volea";
                case "penalty" -> "Penalty";
                default -> "Sin definir";
            };
            case "zona" -> switch (valor.toLowerCase()) {
                case "centre of the box" -> "Área central";
                case "right side of the box" -> "Área derecha";
                case "left side of the box" -> "Área izquierda";
                case "outside the box" -> "Fuera del área";
                case "deep free kick" -> "Tiro libre lejano";
                default -> "Sin zona";
            };
            case "preAction" -> switch (valor.toLowerCase()) {
                case "penalty" -> "Penal";
                case "pass" -> "Pase";
                case "dribble" -> "Regate";
                case "rebound" -> "Rebote";
                case "cross" -> "Centro";
                default -> "No definido";
            };
            default -> valor;
        };
    }

    private static String inferirSituacion(PlayPattern pattern, Shot shot) {
        String base = "Juego abierto";

        if (shot != null && shot.getType() != null && shot.getType().getName() != null) {
            String nombreTipo = shot.getType().getName();
            if (nombreTipo.equalsIgnoreCase("Penalty") || nombreTipo.equalsIgnoreCase("Free Kick")) {
                return "Balón parado";
            }
        }

        if (pattern == null || pattern.getName() == null) {
            return base;
        }

        String nombre = pattern.getName().toLowerCase();

        if (nombre.contains("free kick") || nombre.contains("set piece") || nombre.contains("corner")) {
            return "Balón parado";
        }

        return base;
    }
}
