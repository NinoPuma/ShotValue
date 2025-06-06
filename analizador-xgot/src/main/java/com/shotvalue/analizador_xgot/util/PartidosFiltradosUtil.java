package com.shotvalue.analizador_xgot.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shotvalue.analizador_xgot.model.Partido;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class PartidosFiltradosUtil {

    private static final Set<String> ALLOWED_COMPETITIONS = Set.of(
            "premier league", "la liga", "serie a", "bundesliga", "ligue 1",
            "copa america", "copa américa",
            "uefa euro", "euro 2024"
    );

    private static final Gson gson = new GsonBuilder().create();

    public static Set<String> obtenerMatchIdsValidos(String carpetaMatches) {

        Set<String> matchIdsValidos     = new HashSet<>();

        AtomicInteger totalArchivos     = new AtomicInteger();
        AtomicInteger totalPartidos     = new AtomicInteger();
        AtomicInteger partidosSinDatos  = new AtomicInteger();
        AtomicInteger partidosDescartes = new AtomicInteger();
        AtomicInteger partidosValidos   = new AtomicInteger();

        try {
            Files.walk(Path.of(carpetaMatches))
                    .filter(p -> p.toString().endsWith(".json"))
                    .forEach(path -> {
                        totalArchivos.incrementAndGet();

                        try (FileReader reader = new FileReader(path.toFile())) {
                            Type listType = new TypeToken<List<Partido>>(){}.getType();
                            List<Partido> partidos = gson.fromJson(reader, listType);

                            for (Partido partido : partidos) {
                                totalPartidos.incrementAndGet();

                                String fecha = partido.getMatchDate();

                                String liga = partido.getCompetitionName();
                                if (liga == null && partido.getCompetition() != null) {
                                    liga = partido.getCompetition().getCompetitionName();
                                }

                                if (fecha == null || liga == null) {
                                    partidosSinDatos.incrementAndGet();
                                    continue;
                                }

                                if (fecha.startsWith("2024") &&
                                        ALLOWED_COMPETITIONS.contains(liga.toLowerCase())) {

                                    matchIdsValidos.add(String.valueOf(partido.getMatchId()));
                                    partidosValidos.incrementAndGet();
                                } else {
                                    partidosDescartes.incrementAndGet();
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error leyendo " + path.getFileName() + " → " + e.getMessage());
                        }
                    });

        } catch (Exception e) {
            System.err.println("Error recorriendo carpeta: " + e.getMessage());
        }

        System.out.println("\n RESUMEN DE FILTRADO:");
        System.out.println("- Archivos escaneados:         " + totalArchivos.get());
        System.out.println("- Partidos totales procesados: " + totalPartidos.get());
        System.out.println("- Partidos sin fecha/comp.:    " + partidosSinDatos.get());
        System.out.println("- Partidos descartados:        " + partidosDescartes.get());
        System.out.println("- Partidos válidos 2024:       " + partidosValidos.get());
        return matchIdsValidos;
    }
}
