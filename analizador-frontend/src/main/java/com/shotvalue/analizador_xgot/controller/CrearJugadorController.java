package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.EquiposApiClient;
import com.shotvalue.analizador_xgot.api.JugadorApiClient;
import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.view.ViewLifecycle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.util.Comparator;
import java.util.List;

public class CrearJugadorController implements ViewLifecycle {

    @FXML private ComboBox<Equipo>  equipoBox;
    @FXML private TextField         nombreField;
    @FXML private TextField         dorsalField;
    @FXML private ComboBox<String>  posicionBox;
    @FXML private Button            guardarBtn;
    @FXML private Button            limpiarBtn;

    @FXML
    private void initialize() {

        posicionBox.getItems().addAll(
                "Arquero",
                "Lateral Derecho",
                "Lateral Izquierdo",
                "Defensor Central",
                "Carrilero Derecho",
                "Carrilero Izquierdo",
                "Mediocentro Defensivo",
                "Mediocampista Central",
                "Mediocampista Izquierdo",
                "Mediocampista Derecho",
                "Mediapunta",
                "Mediapunta Central",
                "Mediocentro Defensivo Central",
                "Defensor Central Izquierdo",
                "Defensor Central Derecho",
                "Delantero Centro",
                "Delantero Derecho",
                "Delantero Izquierdo",
                "Extremo Derecho",
                "Extremo Izquierdo",
                "Delantero",
                "Segundo Delantero",
                "Mediocentro Defensivo Izquierdo",
                "Mediocentro Defensivo Derecho",
                "Mediapunta Izquierdo",
                "Mediapunta Derecho"
        );

        guardarBtn.setOnAction(e -> guardar());
        limpiarBtn.setOnAction(e -> limpiar());
    }

    @Override public void onShow() { cargarEquipos(); }
    @Override public void onHide() { }

    private void cargarEquipos() {
        EquiposApiClient.getEquiposAsync()
                .thenAccept(this::llenarCombo)
                .exceptionally(ex -> { Platform.runLater(() ->
                        alerta("Error", ex.getMessage(), Alert.AlertType.ERROR)); return null;});
    }
    private void llenarCombo(List<Equipo> lista) {
        Platform.runLater(() -> {
            List<Equipo> ordenados = lista.stream()
                    .sorted(Comparator.comparing(Equipo::getName, String.CASE_INSENSITIVE_ORDER))
                    .toList();
            equipoBox.getItems().setAll(ordenados);
            equipoBox.getSelectionModel().clearSelection();
        });
    }

    private void guardar() {

        if (equipoBox.getValue() == null) {
            alerta("Falta equipo", "Selecciona un equipo", Alert.AlertType.WARNING); return; }
        if (nombreField.getText().trim().isEmpty()) {
            alerta("Falta nombre", "Ingresa el nombre del jugador", Alert.AlertType.WARNING); return; }
        if (!dorsalField.getText().matches("\\d+")) {
            alerta("Dorsal inválido", "El dorsal debe ser numérico", Alert.AlertType.WARNING); return; }
        if (posicionBox.getValue() == null) {
            alerta("Falta posición", "Selecciona la posición", Alert.AlertType.WARNING); return; }

        Jugador j = new Jugador();
        j.setTeamId(equipoBox.getValue().getTeamId());
        j.setPlayerName(nombreField.getText().trim());
        j.setJerseyNumber(dorsalField.getText().trim());
        j.setPosition(posicionBox.getValue());

        JugadorApiClient.saveJugadorAsync(j)
                .thenAccept(saved -> Platform.runLater(() -> {
                    alerta("Éxito", "Jugador creado correctamente", Alert.AlertType.INFORMATION);
                    limpiar();
                }))
                .exceptionally(ex -> { Platform.runLater(() ->
                        alerta("Error", ex.getMessage(), Alert.AlertType.ERROR)); return null;});
    }

    private void limpiar() {
        nombreField.clear();
        dorsalField.clear();
        posicionBox.getSelectionModel().clearSelection();
        equipoBox.getSelectionModel().clearSelection();
    }
    private void alerta(String t, String m, Alert.AlertType tp) {
        Window w = nombreField.getScene().getWindow();
        Alert  a = new Alert(tp, m, ButtonType.OK);
        a.setTitle(t);
        a.initOwner(w);
        a.initModality(Modality.WINDOW_MODAL);
        a.showAndWait();
    }
}
