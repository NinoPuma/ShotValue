package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.config.SpringFXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AppController {

    @FXML
    private AnchorPane contenidoCentro;

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    @FXML
    public void initialize() {
        mostrarInicio(); // Vista inicial al abrir el layout
    }

    @FXML
    public void mostrarInicio() {
        cargarVista("/tfcc/inicio-view.fxml");
    }

    @FXML
    public void mostrarEquipos() {
        cargarVista("/tfcc/equipos-controller.fxml");
    }

    @FXML
    public void mostrarRegistrar() {
        cargarVista("/tfcc/registro.fxml");
    }

    @FXML
    public void mostrarVisualizar() {
        cargarVista("/tfcc/visualizar-view.fxml");
    }

    @FXML
    public void mostrarInformes() {
        cargarVista("/tfcc/informes-view.fxml");
    }

    @FXML
    public void mostrarPerfil() {
        cargarVista("/tfcc/perfil-view.fxml");
    }

    @FXML
    public void mostrarAyuda() {
        cargarVista("/tfcc/ayuda-view.fxml");
    }

    @FXML
    public void cerrarSesion() {
        try {
            Stage stage = (Stage) contenidoCentro.getScene().getWindow();
            Parent login = springFXMLLoader.load("/tfcc/login.fxml");
            stage.setScene(new Scene(login));
            stage.centerOnScreen();
            stage.setTitle("Login");
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
