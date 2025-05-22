package com.shotvalue.analizador_xgot.front;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class EquiposController {
    @FXML private BorderPane mainBorderPane;
    @FXML private AnchorPane contenidoCentro;
    @FXML private Button btnInicio, btnEquipos, btnRegistrar, btnVisualizar, btnInformes, btnPerfil, btnAyuda;

    private final String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white;";
    private final String activeStyle  = "-fx-background-color: #0F7F7F; -fx-text-fill: white; -fx-font-weight: bold;";

    @FXML
    public void initialize() {
        btnInicio    .setOnAction(e -> cargarVista("/tfcc/inicio-view.fxml",    btnInicio));
        btnEquipos   .setOnAction(e -> cargarVista("/tfcc/equipos-content.fxml", btnEquipos));
        btnRegistrar .setOnAction(e -> cargarVista("/tfcc/registrar-view.fxml",  btnRegistrar));
        btnVisualizar.setOnAction(e -> cargarVista("/tfcc/visualizar-view.fxml", btnVisualizar));
        btnInformes  .setOnAction(e -> cargarVista("/tfcc/informes-view.fxml",  btnInformes));
        btnPerfil    .setOnAction(e -> cargarVista("/tfcc/perfil-view.fxml",    btnPerfil));
        btnAyuda     .setOnAction(e -> cargarVista("/tfcc/ayuda-view.fxml",     btnAyuda));

        cargarVista("/tfcc/equipos-content.fxml", btnEquipos);  // vista por defecto
    }

    private void cargarVista(String rutaFXML, Button activo) {
        try {
            Parent vista = FXMLLoader.load(getClass().getResource(rutaFXML));
            contenidoCentro.getChildren().setAll(vista);

            // 🔧 aquí anclas la nueva vista a los cuatro bordes del AnchorPane
            AnchorPane.setTopAnchor(vista,    0.0);
            AnchorPane.setRightAnchor(vista,  0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista,   0.0);

            resetMenuStyles();
            activo.setStyle(activeStyle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void resetMenuStyles() {
        btnInicio.setStyle(defaultStyle);
        btnEquipos.setStyle(defaultStyle);
        btnRegistrar.setStyle(defaultStyle);
        btnVisualizar.setStyle(defaultStyle);
        btnInformes.setStyle(defaultStyle);
        btnPerfil.setStyle(defaultStyle);
        btnAyuda.setStyle(defaultStyle);
    }
}
