package com.shotvalue.front;

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

public class RegistroController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField repeatPasswordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleRegister(ActionEvent event) {
        String user = usernameField.getText();
        String pass1 = passwordField.getText();
        String pass2 = repeatPasswordField.getText();

        if (user.isEmpty() || pass1.isEmpty() || pass2.isEmpty()) {
            messageLabel.setText("Rellena todos los campos.");
        } else if (!pass1.equals(pass2)) {
            messageLabel.setText("Las contraseñas no coinciden.");
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/momentaneo/tfcc/equipos-view.fxml"));
                Scene scene = new Scene(loader.load(), 1000, 700); // App principal

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Equipos");
                stage.setMaximized(true); // App principal en pantalla grande
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                messageLabel.setText("Error al cargar la vista de equipos.");
            }
        }
    }

    @FXML
    private void goToLogin(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/momentaneo/tfcc/login.fxml"));
            Scene scene = new Scene(loader.load(), 600, 500);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.centerOnScreen();
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
