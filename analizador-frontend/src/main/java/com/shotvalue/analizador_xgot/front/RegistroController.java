package com.shotvalue.analizador_xgot.front;

import com.google.gson.Gson;
import com.shotvalue.analizador_xgot.front.model.Usuario;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class RegistroController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatPasswordField;
    @FXML private TextField     emailField;
    @FXML private CheckBox      termsCheckBox;
    @FXML private Label         messageLabel;

    private static final String BASE_URL = "http://localhost:8080/api";
    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson       gson = new Gson();

    @FXML
    private void handleRegister() {
        var username       = usernameField.getText().trim();
        var email          = emailField.getText().trim();
        var password       = passwordField.getText();
        var repeatPassword = repeatPasswordField.getText();
        var acceptedTerms  = termsCheckBox.isSelected();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            showError("Por favor, rellena todos los campos.");
            return;
        }
        if (!password.equals(repeatPassword)) {
            showError("Las contraseñas no coinciden.");
            return;
        }
        if (!acceptedTerms) {
            showError("Debes aceptar los términos y condiciones.");
            return;
        }

        var nuevoUsuario = new Usuario(null, username, email, password);
        sendRegistration(nuevoUsuario)
                .thenAccept(this::handleRegistrationResponse)
                .exceptionally(ex -> { showError("Fallo de conexión: " + ex.getMessage()); return null; });
    }

    private CompletableFuture<HttpResponse<String>> sendRegistration(Usuario user) {
        String json = gson.toJson(user);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/usuarios"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        return http.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    private void handleRegistrationResponse(HttpResponse<String> response) {
        if (response == null) return;

        switch (response.statusCode()) {
            case 200, 201 -> showSuccess("¡Registro exitoso! Ahora puedes iniciar sesión.");
            case 409      -> showError("El usuario ya existe.");
            default       -> showError("Error en el registro (" + response.statusCode() + "):\n" + response.body());
        }
    }

    private void showSuccess(String msg) {
        Platform.runLater(() -> {
            messageLabel.setStyle("-fx-text-fill: green;");
            messageLabel.setText(msg);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/login.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("No se pudo cargar la pantalla de login");
            e.printStackTrace();
        }
    }
}
