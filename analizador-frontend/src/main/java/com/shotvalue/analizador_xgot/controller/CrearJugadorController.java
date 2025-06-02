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

import java.util.List;

public class CrearJugadorController implements ViewLifecycle {

    /* ───────── FXML ───────── */
    @FXML private ComboBox<Equipo>  equipoBox;
    @FXML private TextField         nombreField;
    @FXML private TextField         dorsalField;
    @FXML private ComboBox<String>  posicionBox;
    @FXML private Button            guardarBtn;
    @FXML private Button            limpiarBtn;

    /* ───────── init ───────── */
    @FXML
    private void initialize() {

        posicionBox.getItems().addAll(
                "Portero", "Defensa", "Lateral", "Pivote",
                "Interior", "Extremo", "Delantero"
        );

        guardarBtn.setOnAction(e -> guardar());
        limpiarBtn.setOnAction(e -> limpiar());
    }

    /* ───────── ViewLifecycle ───────── */
    @Override public void onShow() { cargarEquipos(); }
    @Override public void onHide() { }

    /* ───────── carga de equipos ───────── */
    private void cargarEquipos() {
        EquiposApiClient.getEquiposAsync()
                .thenAccept(this::llenarCombo)
                .exceptionally(ex -> { Platform.runLater(() ->
                        alerta("Error", ex.getMessage(), Alert.AlertType.ERROR)); return null;});
    }
    private void llenarCombo(List<Equipo> lista) {
        Platform.runLater(() -> {
            equipoBox.getItems().setAll(lista);
            equipoBox.getSelectionModel().clearSelection();
        });
    }

    /* ───────── guardar ───────── */
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
        j.setJerseyNumber(dorsalField.getText().trim());     // ← String, como lo define el modelo
        j.setPosition(posicionBox.getValue());

        JugadorApiClient.saveJugadorAsync(j)
                .thenAccept(saved -> Platform.runLater(() -> {
                    alerta("Éxito", "Jugador creado correctamente", Alert.AlertType.INFORMATION);
                    limpiar();
                }))
                .exceptionally(ex -> { Platform.runLater(() ->
                        alerta("Error", ex.getMessage(), Alert.AlertType.ERROR)); return null;});
    }

    /* ───────── util ───────── */
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
