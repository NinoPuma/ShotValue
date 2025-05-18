package com.shotvalue.front;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

public class EquiposController {

    @FXML private BorderPane mainBorderPane;
    @FXML private AnchorPane contenidoCentro;

    @FXML private Button btnInicio;
    @FXML private Button btnEquipos;
    @FXML private Button btnRegistrar;
    @FXML private Button btnVisualizar;
    @FXML private Button btnInformes;
    @FXML private Button btnPerfil;
    @FXML private Button btnAyuda;

    // Controles del formulario de jugadores (solo necesarios en equipos-content.fxml)
    @FXML private TextField numberField;
    @FXML private TextField playerNameField;
    @FXML private ComboBox<String> generalPositionBox;
    @FXML private ComboBox<String> specificPositionBox;
    @FXML private TableView<Player> playerTable;

    private final ObservableList<Player> players = FXCollections.observableArrayList();
    private final String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white;";
    private final String activeStyle = "-fx-background-color: #0F7F7F; -fx-text-fill: white; -fx-font-weight: bold;";

    @FXML
    public void initialize() {
        btnInicio.setOnAction(e -> cargarVista("/tfcc/inicio-view.fxml", btnInicio));
        btnEquipos.setOnAction(e -> cargarVista("/tfcc/equipos-content.fxml", btnEquipos));
        btnRegistrar.setOnAction(e -> cargarVista("/tfcc/registro-view.fxml", btnRegistrar));
        btnVisualizar.setOnAction(e -> cargarVista("/tfcc/visualizar-view.fxml", btnVisualizar));
        btnInformes.setOnAction(e -> cargarVista("/tfcc/informes-view.fxml", btnInformes));
        btnPerfil.setOnAction(e -> cargarVista("/tfcc/perfil-view.fxml", btnPerfil));
        btnAyuda.setOnAction(e -> cargarVista("/tfcc/ayuda-view.fxml", btnAyuda));

        cargarVista("/tfcc/equipos-content.fxml", btnEquipos);
    }

    private void cargarVista(String rutaFXML, Button botonActivo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
            Parent vista = loader.load();
            contenidoCentro.getChildren().setAll(vista);
            resetMenuStyles();
            botonActivo.setStyle(activeStyle);
        } catch (IOException e) {
            e.printStackTrace();
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

    // Método usado solo si estás en equipos-content.fxml
    @FXML
    private void handleAddPlayer(ActionEvent event) {
        if (numberField == null || playerNameField == null) return;

        String number = numberField.getText();
        String name = playerNameField.getText();
        String generalPos = generalPositionBox.getValue();
        String specificPos = specificPositionBox.getValue();

        if (number.isEmpty() || name.isEmpty() || generalPos == null || specificPos == null) {
            showAlert("Faltan campos por rellenar.");
            return;
        }

        Player player = new Player(number, name, generalPos, specificPos);
        players.add(player);

        numberField.clear();
        playerNameField.clear();
        generalPositionBox.setValue(null);
        specificPositionBox.setValue(null);
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Player {
        private final String number;
        private final String name;
        private final String generalPosition;
        private final String specificPosition;

        public Player(String number, String name, String generalPosition, String specificPosition) {
            this.number = number;
            this.name = name;
            this.generalPosition = generalPosition;
            this.specificPosition = specificPosition;
        }

        public String getNumber() { return number; }
        public String getName() { return name; }
        public String getGeneralPosition() { return generalPosition; }
        public String getSpecificPosition() { return specificPosition; }
    }
}
