package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.EquiposApiClient;
import com.shotvalue.analizador_xgot.api.JugadorApiClient;
import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.model.Jugador;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.util.List;

public class CrearJugadorController {

    /* ---------- FXML ---------- */
    @FXML private ComboBox<Equipo> equipoBox;
    @FXML private TextField       nombreField;
    @FXML private TextField       dorsalField;
    @FXML private ComboBox<String> posicionBox;
    @FXML private Button          guardarBtn;
    @FXML private Button          limpiarBtn;

    /* ---------- INIT ---------- */
    @FXML
    public void initialize() {
        // posiciones fijas
        posicionBox.getItems().addAll(
                "Portero", "Defensa", "Lateral", "Pivote",
                "Interior", "Extremo", "Delantero"
        );

        cargarEquipos();              // llena el combo de equipos

        guardarBtn.setOnAction(e -> guardar());
        limpiarBtn.setOnAction(e -> limpiar());
    }

    /* ---------- cargar equipos ---------- */
    private void cargarEquipos() {
        EquiposApiClient.getEquiposAsync()
                .thenAccept(this::llenarCombo)
                .exceptionally(ex -> { alerta("Error", ex.getMessage(), Alert.AlertType.ERROR); return null; });
    }

    private void llenarCombo(List<Equipo> equipos) {
        Platform.runLater(() -> equipoBox.getItems().setAll(equipos));
    }

    /* ---------- guardar jugador ---------- */
    private void guardar() {

        // validaciones mínimas
        if (equipoBox.getValue() == null) {
            alerta("Falta equipo", "Selecciona un equipo", Alert.AlertType.WARNING); return;
        }
        if (nombreField.getText().trim().isEmpty()) {
            alerta("Falta nombre", "Ingresa el nombre del jugador", Alert.AlertType.WARNING); return;
        }
        if (!dorsalField.getText().matches("\\d+")) {
            alerta("Dorsal inválido", "El dorsal debe ser numérico", Alert.AlertType.WARNING); return;
        }

        // payload
        Jugador payload = new Jugador();
        payload.setTeamId(equipoBox.getValue().getTeamId());
        payload.setPlayerName(nombreField.getText().trim());
        payload.setJerseyNumber(dorsalField.getText().trim());
        payload.setPosition(posicionBox.getValue());

        JugadorApiClient.saveJugadorAsync(payload)
                .thenRun(() -> Platform.runLater(() -> {
                    alerta("Éxito", "Jugador creado correctamente", Alert.AlertType.INFORMATION);
                    limpiar();
                }))
                .exceptionally(ex -> { Platform.runLater(() ->
                        alerta("Error", ex.getMessage(), Alert.AlertType.ERROR)); return null;});
    }

    /* ---------- util ---------- */
    private void limpiar() {
        nombreField.clear();
        dorsalField.clear();
        posicionBox.getSelectionModel().clearSelection();
        equipoBox.getSelectionModel().clearSelection();
    }

    private void alerta(String title, String msg, Alert.AlertType type) {
        Window owner = nombreField.getScene().getWindow();
        Alert a = new Alert(type, msg, ButtonType.OK);
        a.setTitle(title);
        a.initOwner(owner);
        a.initModality(Modality.WINDOW_MODAL);
        a.showAndWait();
    }
}
