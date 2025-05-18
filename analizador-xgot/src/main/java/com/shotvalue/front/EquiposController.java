package com.shotvalue.front;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class EquiposController {

    @FXML private TextField numberField;
    @FXML private TextField playerNameField;
    @FXML private ComboBox<String> generalPositionBox;
    @FXML private ComboBox<String> specificPositionBox;
    @FXML private TableView<Player> playerTable;

    private ObservableList<Player> players = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        generalPositionBox.getItems().addAll("Defensa", "Mediocentro", "Delantero");
        specificPositionBox.getItems().addAll("Lateral", "Central", "Pivote", "Extremo", "Media punta");

        playerTable.setItems(players);
    }

    @FXML
    private void handleAddPlayer(ActionEvent event) {
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

    // Clase interna para el modelo de jugador
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
