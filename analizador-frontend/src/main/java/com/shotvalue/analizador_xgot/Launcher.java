package com.shotvalue.analizador_xgot;

import com.shotvalue.analizador_xgot.util.VentanaHelper;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Launcher extends Application {

    private static final String CONFIG_FILE = "window.properties";

    @Override
    public void start(Stage stage) throws Exception {
        VentanaHelper.cargarEscena(stage, "/tfcc/login.fxml", "ShotValue - Análisis xGOT");

        stage.setOnCloseRequest(event -> {
            Properties outProps = new Properties();
            outProps.setProperty("maximized", String.valueOf(stage.isMaximized()));
            if (!stage.isMaximized()) {
                outProps.setProperty("width", String.valueOf(stage.getWidth()));
                outProps.setProperty("height", String.valueOf(stage.getHeight()));
            }

            try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
                outProps.store(fos, "Tamaño de ventana");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
