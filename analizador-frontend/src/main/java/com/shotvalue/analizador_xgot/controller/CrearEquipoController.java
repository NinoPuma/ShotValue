package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.EquiposApiClient;
import com.shotvalue.analizador_xgot.model.Equipo;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Window;

public class CrearEquipoController {
    
    @FXML private TextField nameField;
    @FXML private Button    guardarBtn;
    @FXML private Button    limpiarBtn;

    @FXML
    public void initialize() {
        guardarBtn.setOnAction(e -> guardar());
        limpiarBtn.setOnAction(e -> nameField.clear());
    }

    /* ─── Guardar equipo ─────────────────────────────────────────── */
    private void guardar() {

        /* 1) Validación mínima */
        String nombre = nameField.getText().trim();
        if (nombre.isEmpty()) {
            alerta(Alert.AlertType.WARNING,
                    "Falta nombre",
                    "El nombre del equipo no puede estar vacío.");
            return;
        }

        /* 2) Payload sin id (lo genera el backend) */
        Equipo equipo = new Equipo();
        equipo.setName(nombre);

        /* 3) Llamada asíncrona */
        EquiposApiClient.saveEquipoAsync(equipo)
                .thenAccept(eq -> Platform.runLater(() -> {
                    /* ✅  Mensaje simplificado  */
                    alerta(Alert.AlertType.INFORMATION,
                            "Equipo creado",
                            "Equipo creado correctamente.");
                    nameField.clear();
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() ->
                            alerta(Alert.AlertType.ERROR,
                                    "Error",
                                    "No se pudo guardar: " + ex.getMessage())
                    );
                    return null;
                });
    }

    /* ─── Alert helper ───────────────────────────────────────────── */
    private void alerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Window owner = nameField.getScene().getWindow();   // ventana actual

        Alert alert = new Alert(tipo, mensaje, ButtonType.OK);
        alert.setTitle(titulo);
        alert.initOwner(owner);            // evita que abra una ventana maximizada
        alert.initModality(Modality.WINDOW_MODAL);
        alert.showAndWait();
    }
}
