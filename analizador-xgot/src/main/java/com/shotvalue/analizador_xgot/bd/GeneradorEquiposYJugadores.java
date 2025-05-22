package com.shotvalue.analizador_xgot.bd;

import com.google.gson.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class GeneradorEquiposYJugadores {

    static class Equipo {
        int team_id;
        String team_name;
    }

    static class Jugador {
        int player_id;
        String player_name;
        int team_id;
        String position;
        String jersey_number;
    }

    public static void main(String[] args) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Set<Equipo> equipos = new HashSet<>();
        Set<Jugador> jugadores = new HashSet<>();

        // 1. Recorrer todos los matches
        Path matchesDir = Paths.get("C:/Users/Santi/Downloads/open-data-master/open-data-master/data/matches");
        Files.walk(matchesDir)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try (Reader reader = Files.newBufferedReader(path)) {
                        JsonArray partidos = JsonParser.parseReader(reader).getAsJsonArray();
                        for (JsonElement partido : partidos) {
                            JsonObject localTeam = partido.getAsJsonObject().get("home_team").getAsJsonObject();
                            JsonObject awayTeam = partido.getAsJsonObject().get("away_team").getAsJsonObject();

                            Equipo local = new Equipo();
                            local.team_id = localTeam.get("home_team_id").getAsInt();
                            local.team_name = localTeam.get("home_team_name").getAsString();

                            Equipo visitante = new Equipo();
                            visitante.team_id = awayTeam.get("away_team_id").getAsInt();
                            visitante.team_name = awayTeam.get("away_team_name").getAsString();

                            equipos.add(local);
                            equipos.add(visitante);
                        }
                    } catch (Exception e) {
                        System.out.println("Error leyendo match: " + path.getFileName());
                    }
                });

        // 2. Recorrer todos los lineups
        Path lineupsDir = Paths.get("C:/Users/Santi/Downloads/open-data-master/open-data-master/data/lineups");
        Files.walk(lineupsDir)
                .filter(Files::isRegularFile)
                .forEach(path -> {
                    try (Reader reader = Files.newBufferedReader(path)) {
                        JsonArray array = JsonParser.parseReader(reader).getAsJsonArray();
                        for (JsonElement equipo : array) {
                            JsonObject equipoObj = equipo.getAsJsonObject();
                            int teamId = equipoObj.get("team_id").getAsInt();
                            JsonArray players = equipoObj.get("lineup").getAsJsonArray();

                            for (JsonElement p : players) {
                                JsonObject pj = p.getAsJsonObject();
                                Jugador jugador = new Jugador();
                                jugador.player_id = pj.get("player_id").getAsInt();
                                jugador.player_name = pj.get("player_name").getAsString();
                                jugador.team_id = teamId;
                                jugador.position = pj.has("position") ? pj.get("position").getAsString() : null;
                                jugador.jersey_number = pj.has("jersey_number") ? pj.get("jersey_number").getAsString() : null;
                                jugadores.add(jugador);
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Error leyendo lineup: " + path.getFileName());
                    }
                });

        // 3. Guardar resultados
        try (Writer writer = new FileWriter("C:/Users/Santi/Downloads/open-data-master/open-data-master/data/equipos.json")) {
            gson.toJson(equipos, writer);
        }

        try (Writer writer = new FileWriter("C:/Users/Santi/Downloads/open-data-master/open-data-master/data/jugadores.json")) {
            gson.toJson(jugadores, writer);
        }

        System.out.println("¡Listo! Equipos y jugadores exportados sin duplicados.");
    }
}
