package com.shotvalue.analizador_xgot.front;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class EquiposContentController {
    @FXML private TextField teamNameField;
    @FXML private TextField coachField;
    @FXML private ComboBox<String> formationBox;
    @FXML private ColorPicker colorPicker;
    @FXML private TextField numberField;
    @FXML private TextField playerNameField;
    @FXML private ComboBox<String> generalPositionBox;
    @FXML private ComboBox<String> specificPositionBox;
    @FXML private TableView<Player> playerTable;

    private final ObservableList<Player> players = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configura la tabla con tus propiedades:
        TableColumn<Player,String> colNum = new TableColumn<>("Número");
        colNum.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getNumber()));
        TableColumn<Player,String> colName = new TableColumn<>("Nombre");
        colName.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        TableColumn<Player,String> colGen = new TableColumn<>("Posición general");
        colGen.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getGeneralPosition()));
        TableColumn<Player,String> colSpec = new TableColumn<>("Posición específica");
        colSpec.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getSpecificPosition()));
        playerTable.getColumns().setAll(colNum, colName, colGen, colSpec);
        playerTable.setItems(players);
    }

    @FXML
    private void handleAddPlayer(ActionEvent event) {
        if (numberField.getText().isEmpty() ||
                playerNameField.getText().isEmpty() ||
                generalPositionBox.getValue()==null ||
                specificPositionBox.getValue()==null) {
            new Alert(Alert.AlertType.WARNING, "Faltan campos por rellenar.").showAndWait();
            return;
        }

        players.add(new Player(
                numberField.getText(),
                playerNameField.getText(),
                generalPositionBox.getValue(),
                specificPositionBox.getValue()
        ));

        numberField.clear();
        playerNameField.clear();
        generalPositionBox.setValue(null);
        specificPositionBox.setValue(null);
    }

    public static class Player {
        private final String number, name, generalPosition, specificPosition;
        public Player(String number, String name, String generalPosition, String specificPosition) {
            this.number = number; this.name = name;
            this.generalPosition = generalPosition; this.specificPosition = specificPosition;
        }
        public String getNumber() { return number; }
        public String getName() { return name; }
        public String getGeneralPosition() { return generalPosition; }
        public String getSpecificPosition() { return specificPosition; }
    }
}
