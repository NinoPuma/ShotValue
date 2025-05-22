package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.util.EscenaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.equals("admin") && password.equals("admin")) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            EscenaUtil.cambiarEscena(stage, "/tfcc/equipos-view.fxml");
        } else {
            showAlert("Usuario o contraseña incorrectos.");
        }
    }


    private void showAlert(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Fallido");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
