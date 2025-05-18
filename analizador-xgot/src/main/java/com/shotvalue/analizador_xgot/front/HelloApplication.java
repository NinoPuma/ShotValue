package com.shotvalue.analizador_xgot.front;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/tfcc/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 500); // Tamaño inicial

        stage.setScene(scene);
        stage.setTitle("ShotValue");
        stage.centerOnScreen();     // Centrado
        stage.setMaximized(true);   // Pantalla completa al iniciar
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
