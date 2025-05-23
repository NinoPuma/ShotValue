package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.config.SpringFXMLLoader;
import com.shotvalue.analizador_xgot.model.Usuario;
import com.google.gson.Gson;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

@Component
public class RegistroController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatPasswordField;
    @FXML private TextField emailField;
    @FXML private CheckBox termsCheckBox;
    @FXML private Label messageLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @Autowired
    private SpringFXMLLoader springFXMLLoader;


    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();
        boolean acceptedTerms = termsCheckBox.isSelected();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            messageLabel.setText("Por favor, rellena todos los campos.");
            return;
        }

        if (!password.equals(repeatPassword)) {
            messageLabel.setText("Las contraseñas no coinciden.");
            return;
        }

        if (!acceptedTerms) {
            messageLabel.setText("Debes aceptar los términos y condiciones.");
            return;
        }

        Usuario nuevoUsuario = new Usuario(null, username, email, password);
        sendRegistration(nuevoUsuario);
    }

    private void sendRegistration(Usuario user) {
        String json = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/usuarios"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    System.out.println("Código de estado: " + response.statusCode());
                    System.out.println("Respuesta del servidor: " + response.body());

                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        showSuccess("¡Registro exitoso!");
                    } else {
                        showError("Error en el registro: " + response.body());
                    }
                });
    }

    private void showSuccess(String msg) {
        javafx.application.Platform.runLater(() -> {
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();
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
        });
    }


    private void showError(String msg) {
        javafx.application.Platform.runLater(() -> {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(msg);
        });
    }

    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            Parent root = springFXMLLoader.load("/tfcc/login.fxml");

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
            stage.centerOnScreen();
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}