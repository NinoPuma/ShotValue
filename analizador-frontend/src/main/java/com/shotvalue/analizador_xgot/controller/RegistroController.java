package com.shotvalue.analizador_xgot.controller;

import com.google.gson.Gson;
import com.shotvalue.analizador_xgot.model.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class RegistroController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatPasswordField;
    @FXML private TextField emailField;
    @FXML private CheckBox termsCheckBox;
    @FXML private Label messageLabel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

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
        Platform.runLater(() -> {
            System.out.println("✅ showSuccess() ejecutado: " + msg);
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();

                System.out.println("➡️ Cargando app-layout.fxml...");
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/app-layout.fxml"));
                Parent root = loader.load();
                System.out.println("✅ app-layout.fxml cargado correctamente");

                // Intentamos acceder al controlador
                Object controller = loader.getController();
                if (controller instanceof AppController appController) {
                    System.out.println("✅ AppController instanciado");
                    appController.initialize(); // opcional si no se llama automáticamente
                } else {
                    System.out.println("⚠️ El controlador no es instancia de AppController");
                }

                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Inicio");
                stage.centerOnScreen();
                stage.setMaximized(false);
                stage.show();

                System.out.println("🎉 Vista app-layout mostrada correctamente");

            } catch (IOException e) {
                System.out.println("❌ Error al cargar app-layout.fxml:");
                e.printStackTrace();
            }
        });
    }

    private void showError(String msg) {
        Platform.runLater(() -> {
            messageLabel.setStyle("-fx-text-fill: red;");
            messageLabel.setText(msg);
        });
    }

    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/login.fxml"));
            Parent root = loader.load();

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
