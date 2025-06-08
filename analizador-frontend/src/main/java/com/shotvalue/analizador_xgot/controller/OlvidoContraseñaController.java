package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.util.VentanaHelper;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class OlvidoContraseñaController {

    @FXML
    private TextField emailField;
    @FXML
    private Label messageLabel;

    @FXML
    private void handleSendRequest() {
        // In a real app you would call your API here
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Recuperar contraseña");
        alert.setHeaderText(null);
        alert.setContentText("Si el correo está registrado recibirás un enlace para restablecer tu contraseña.");
        alert.showAndWait();
        messageLabel.setText("");
    }

    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            VentanaHelper.cargarEscena(stage, "/tfcc/login.fxml", "Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}