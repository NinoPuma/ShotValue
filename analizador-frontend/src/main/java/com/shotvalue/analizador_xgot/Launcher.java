package com.shotvalue.analizador_xgot;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Launcher extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ShotValue");
        primaryStage.centerOnScreen();
        primaryStage.setMaximized(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
