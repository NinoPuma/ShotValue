package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.config.SpringFXMLLoader;
import com.shotvalue.analizador_xgot.util.EscenaUtil;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.io.IOException;

@Component
public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @Autowired
    private SpringFXMLLoader springFXMLLoader;

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

    @FXML
    private void goToRegister(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Parent root = springFXMLLoader.load("/tfcc/registro.fxml");

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Registro"); // Podés cambiarle el título si querés
            stage.centerOnScreen();     // Centra la nueva escena
            stage.setMaximized(false);  // Asegura que no se abra maximizado
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // Mostralo en consola por ahora
        }
    }


    @FXML
    private void showAlert(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Fallido");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
