package com.shotvalue.analizador_xgot.controller;

import com.google.gson.Gson;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.scene.Node;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    @FXML private TextField usernameField; // usamos esto como emailField
    @FXML private PasswordField passwordField;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Completa todos los campos.");
            return;
        }

        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", email);
        loginData.put("password", password);

        String json = gson.toJson(loginData);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                .build();

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenAccept(response -> {
                    if (response.statusCode() == 200) {
                        Platform.runLater(() -> {
                            try {
                                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/app-layout.fxml"));
                                Parent root = loader.load();
                                stage.setScene(new Scene(root));
                                stage.setTitle("Inicio");
                                stage.centerOnScreen();
                                stage.setMaximized(false);
                                stage.show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        showError("Login fallido: " + response.body());
                    }
                })
                .exceptionally(e -> {
                    e.printStackTrace();
                    showError("No se pudo conectar al servidor.");
                    return null;
                });
    }

    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/registro.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Fallido");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void showError(String mensaje) {
        Platform.runLater(() -> showAlert(mensaje));
    }
}
