package com.shotvalue.analizador_xgot.front;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.Node;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            messageLabel.setText("Rellena todos los campos.");
        } else if (user.equals("hugo") && pass.equals("elcrack")) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/equipos-view.fxml"));
                Scene scene = new Scene(loader.load(), 1000, 700);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Equipos");
                stage.setMaximized(true); // Solo acá queremos pantalla completa
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error al cargar la vista de equipos.");
            }
        } else {
            messageLabel.setText("Credenciales incorrectas.");
        }
    }

    @FXML
    private void goToRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/registro.fxml"));
            Scene scene = new Scene(loader.load(), 600, 500);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Registro");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}