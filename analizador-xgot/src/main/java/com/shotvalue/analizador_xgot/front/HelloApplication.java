package com.shotvalue.analizador_xgot.front;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    // Cambiá esta variable según quieras iniciar en pantalla completa o no
    private final boolean modoPantallaCompleta = false;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/tfcc/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        stage.setScene(scene);
        stage.setTitle("ShotValue - Jugadores");

        if (modoPantallaCompleta) {
            stage.setMaximized(true); // Ventana maximizada con bordes
            // stage.setFullScreen(true); // Si querés fullscreen sin bordes, descomentá esta línea
        } else {
            stage.setWidth(1200); // Tamaño fijo
            stage.setHeight(800);
            stage.centerOnScreen();
        }

        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
