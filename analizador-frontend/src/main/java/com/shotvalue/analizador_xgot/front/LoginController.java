package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.config.SpringFXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Node;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
            try {
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Parent root = springFXMLLoader.load("/tfcc/app-layout.fxml");

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Inicio");
                stage.centerOnScreen();
                stage.setMaximized(false);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showAlert("Usuario o contraseña incorrectos.");
        }
    }

    @FXML
    private void goToRegister() {
        try {
            Parent root = springFXMLLoader.load("/tfcc/registro.fxml");
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
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
