package com.shotvalue.analizador_xgot;

import com.shotvalue.analizador_xgot.util.VentanaHelper;
import javafx.application.Application;
import javafx.stage.Stage;


public class Launcher extends Application {

    @Override
    public void start(Stage stage) {
        try {
            VentanaHelper.cargarEscena(stage, "/tfcc/login.fxml", "ShotValue - Análisis xGOT");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
