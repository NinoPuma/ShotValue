package com.shotvalue.analizador_xgot.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EscenaUtil {
    public static void cambiarEscena(Stage stage, String rutaFXML) {
        try {
            // Estado antes del cambio
            boolean estabaMaximizado = stage.isMaximized();
            boolean estabaMinimizado = stage.isIconified();
            double width = stage.getWidth();
            double height = stage.getHeight();
            double x = stage.getX();
            double y = stage.getY();

            // Debug antes
            System.out.println("ANTES:");
            System.out.println("  Width: " + width);
            System.out.println("  Height: " + height);
            System.out.println("  Maximized: " + estabaMaximizado);

            FXMLLoader loader = new FXMLLoader(EscenaUtil.class.getResource(rutaFXML));
            Parent root = loader.load();
            Scene nuevaScene = new Scene(root);
            stage.setScene(nuevaScene);
            stage.setTitle("ShotValue");

            // Restaurar estado
            // stage.setMaximized(estabaMaximizado); // Comentado para test
            stage.setWidth(width);
            stage.setHeight(height);
            stage.setX(x);
            stage.setY(y);
            stage.setIconified(estabaMinimizado);

            // Debug después
            System.out.println("DESPUÉS:");
            System.out.println("  Width: " + stage.getWidth());
            System.out.println("  Height: " + stage.getHeight());
            System.out.println("  Maximized: " + stage.isMaximized());

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
