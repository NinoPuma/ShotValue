package com.shotvalue.analizador_xgot.logic;

import java.io.File;
import java.nio.file.Files;

public class CargarPartidos {

    public static void main(String[] args) throws Exception {
        File folder = new File("C:/Users/Santi/Desktop/Partidos");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                String json = Files.readString(file.toPath());
                System.out.println("Leído: " + file.getName());
            }
        }
    }
}
