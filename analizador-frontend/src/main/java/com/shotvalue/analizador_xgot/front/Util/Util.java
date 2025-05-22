package com.shotvalue.analizador_xgot.front.Util;
// src/main/java/com/shotvalue/analizador_xgot/util/Util.java

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class Util {
    /**
     * Abre la URL en el navegador por defecto.
     */
    public static void openWeb(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
            Platform.runLater(() ->
                    new Alert(Alert.AlertType.ERROR,
                            "No se pudo abrir el navegador:\n" + e.getMessage()
                    ).showAndWait()
            );
        }
    }
}