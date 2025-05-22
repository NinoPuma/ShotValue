package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.config.SpringFXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class InicioController {

    @FXML
    private AnchorPane contenidoCentro;

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @FXML
    public void initialize() {
        mostrarInicio(); // Carga por defecto
    }

    @FXML
    public void mostrarInicio() {
        cargarVista("/tfcc/inicio-view.fxml"); // creala si querés una vista inicial
    }

    @FXML
    public void mostrarEquipos() {
        cargarVista("/tfcc/equipos.fxml");
    }

    private void cargarVista(String ruta) {
        try {
            Parent vista = springFXMLLoader.load(ruta);
            contenidoCentro.getChildren().setAll(vista);
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
