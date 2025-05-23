package com.shotvalue.analizador_xgot.services;

import com.google.gson.*;
import com.shotvalue.analizador_xgot.bd.LocalDateAdapter;
import com.shotvalue.analizador_xgot.util.PartidoDeserializer;
import com.shotvalue.analizador_xgot.model.Partido;
import com.shotvalue.analizador_xgot.repositories.PartidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

@Service
public class ImportadorPartidosService {

    @Autowired
    private PartidoRepository partidoRepository;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .registerTypeAdapter(Partido.class, new PartidoDeserializer())
            .create();

    public void importarPartidosDesdeCarpeta(Path carpeta) throws IOException {
        long cantidad = partidoRepository.count();
        if (cantidad > 0) {
            System.out.println("⏹ Partidos ya cargados en la base (" + cantidad + "). Se omite la importación.");
            return;
        }

        Files.walk(carpeta)
                .filter(path -> path.toString().endsWith(".json"))
                .forEach(this::importarArchivo);
    }


    private void importarArchivo(Path path) {
        try (Reader reader = Files.newBufferedReader(path)) {
            JsonElement json = JsonParser.parseReader(reader);
            if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();
                for (JsonElement e : array) {
                    Partido partido = gson.fromJson(e, Partido.class);

                    if (partidoRepository.findByMatchId(partido.getMatchId()).isPresent()) {
                        System.out.println("⏩ Ya existe: " + partido.getMatchId());
                        return;
                    }

                    partidoRepository.save(partido);
                    System.out.println("✔ Importado: " + partido.getMatchId());

                }

            }
        } catch (Exception e) {
            System.err.println("Error leyendo: " + path + " - " + e.getMessage());
        }
    }
}
