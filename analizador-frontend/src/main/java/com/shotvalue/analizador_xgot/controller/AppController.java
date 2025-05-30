package com.shotvalue.analizador_xgot.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AppController {

    @FXML
    private BorderPane mainPane;

    @FXML
    private AnchorPane contenidoCentro;

    @FXML
    private Button btnInicio, btnEquipos, btnRegistrar, btnVisualizar, btnInformes, btnPerfil, btnAyuda, btnSalir;

    private final String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white;";
    private final String activeStyle = "-fx-background-color: #0F7F7F; -fx-text-fill: white; -fx-font-weight: bold;";

    // ✅ Este método ahora funciona como corresponde con contenidoCentro
    public void setContenido(Node contenido) {
        contenidoCentro.getChildren().setAll(contenido);

        AnchorPane.setTopAnchor(contenido, 0.0);
        AnchorPane.setRightAnchor(contenido, 0.0);
        AnchorPane.setBottomAnchor(contenido, 0.0);
        AnchorPane.setLeftAnchor(contenido, 0.0);
    }

    @FXML
    public void initialize() {
        btnInicio.setOnAction(e -> cargarVista("/tfcc/inicio-view.fxml", btnInicio));
        btnEquipos.setOnAction(e -> cargarVista("/tfcc/equipos-controller.fxml", btnEquipos));
        btnRegistrar.setOnAction(e -> cargarVista("/tfcc/registrar-view.fxml", btnRegistrar));
        btnVisualizar.setOnAction(e -> cargarVista("/tfcc/visualizar-view.fxml", btnVisualizar));
        btnInformes.setOnAction(e -> cargarVista("/tfcc/informes-view.fxml", btnInformes));
        btnPerfil.setOnAction(e -> cargarVista("/tfcc/perfil-view.fxml", btnPerfil));
        btnAyuda.setOnAction(e -> cargarVista("/tfcc/ayuda-view.fxml", btnAyuda));
        btnSalir.setOnAction(e -> cerrarSesion());

        // Vista inicial por defecto
        cargarVista("/tfcc/inicio-view.fxml", btnInicio);
    }
    public void openCrearEquipo() {
        cargarVista("/tfcc/crear-equipo-view.fxml", null);
    }


    private void cargarVista(String rutaFXML, Button botonActivo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = loader.load();
            setContenido(vista); // usamos el método nuevo

            resetearEstilosMenu();
            if (botonActivo != null) {
                botonActivo.setStyle(activeStyle);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void resetearEstilosMenu() {
        btnInicio.setStyle(defaultStyle);
        btnEquipos.setStyle(defaultStyle);
        btnRegistrar.setStyle(defaultStyle);
        btnVisualizar.setStyle(defaultStyle);
        btnInformes.setStyle(defaultStyle);
        btnPerfil.setStyle(defaultStyle);
        btnAyuda.setStyle(defaultStyle);
    }

    private void cerrarSesion() {
        try {
            Stage stage = (Stage) contenidoCentro.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/login.fxml"));
            Parent login = loader.load();
            stage.setScene(new Scene(login));
            stage.setTitle("Login");
            stage.setMaximized(false);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
