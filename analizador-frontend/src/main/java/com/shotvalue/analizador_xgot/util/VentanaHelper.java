package com.shotvalue.analizador_xgot.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class VentanaHelper {


    public static Parent cargarEscena(Stage stage, String rutaFXML, String tituloVentana) throws IOException {
        FXMLLoader loader = new FXMLLoader(VentanaHelper.class.getResource(rutaFXML));
        Parent root = loader.load();

        if (stage.getScene() == null) {
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(root);
        }
        stage.setTitle(tituloVentana);

        if (root instanceof Region region) {
            region.prefWidthProperty().bind(stage.widthProperty());
            region.prefHeightProperty().bind(stage.heightProperty());
        }

        stage.setMinWidth(900);
        stage.setMinHeight(600);

        if (!stage.isShowing()) {
            stage.setMaximized(true);
            stage.show();
        } else {
            stage.setMaximized(true);
        }
        stage.centerOnScreen();


        return root;
    }
}
