package com.shotvalue.analizador_xgot.services;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.shotvalue.analizador_xgot.util.HeightDeserializer;
import com.shotvalue.analizador_xgot.util.PlayPatternDeserializer;
import com.shotvalue.analizador_xgot.model.Evento;
import com.shotvalue.analizador_xgot.model.PlayPattern;
import com.shotvalue.analizador_xgot.repositories.EventoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

@Service
public class ImportadorEventosService {

    @Autowired
    private EventoRepository eventoRepo;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(PlayPattern.class, new PlayPatternDeserializer())
            .registerTypeAdapter(String.class, new HeightDeserializer())
            .create();

    public void importarDesdeCarpeta(String path) {
        File carpeta = new File(path);
        File[] archivos = carpeta.listFiles((dir, name) -> name.endsWith(".json"));

        if (archivos == null || archivos.length == 0) {
            System.out.println("❌ No se encontraron archivos .json en la carpeta: " + path);
            return;
        }

        System.out.println("📁 Archivos encontrados: " + archivos.length);

        for (File archivo : archivos) {
            System.out.println("📄 Procesando archivo: " + archivo.getName());  // <--- Línea útil

            try (FileReader reader = new FileReader(archivo)) {
                Type eventoListType = new TypeToken<List<Evento>>() {}.getType();
                List<Evento> eventos = gson.fromJson(reader, eventoListType);

                if (!eventos.isEmpty()) {
                    String matchId = archivo.getName().replace(".json", "");

                    for (Evento evento : eventos) {
                        evento.setMatchId(matchId);
                    }

                    if (eventoRepo.findByMatchId(matchId).isEmpty()) {
                        eventoRepo.saveAll(eventos);
                        System.out.println("✅ Importados " + eventos.size() + " eventos del partido " + matchId);
                    } else {
                        System.out.println("⚠️  Eventos ya importados para el partido " + matchId);
                    }
                } else {
                    System.out.println("⚠️  No se encontraron eventos en el archivo " + archivo.getName());
                }

            } catch (Exception e) {
                System.err.println("❌ Error al importar " + archivo.getName() + ": " + e.getMessage());
            }
        }
    }
}
