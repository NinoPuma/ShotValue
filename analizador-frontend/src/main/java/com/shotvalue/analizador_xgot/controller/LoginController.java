package com.shotvalue.analizador_xgot.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.util.LocalDateAdapter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginController {

    /* ----------- FXML ----------- */
    @FXML private TextField     usernameField;   // se usa como email
    @FXML private PasswordField passwordField;

    /* ----------- helpers ----------- */
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .create();

    /* ================================================================== */
    @FXML
    private void handleLogin(ActionEvent event) {

        String email    = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Completa todos los campos.");
            return;
        }

        Map<String, String> body = new HashMap<>();
        body.put("email", email);
        body.put("password", password);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body), StandardCharsets.UTF_8))
                .build();

        httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenAccept(resp -> {
                    if (resp.statusCode() == 200) {

                        Usuario usr  = gson.fromJson(resp.body(), Usuario.class);
                        String nombre = usr.getUsername();           // o getNombreCompleto()

                        Platform.runLater(() -> {
                            try {
                                /* cargamos el layout principal */
                                FXMLLoader loader = new FXMLLoader(getClass()
                                        .getResource("/tfcc/app-layout.fxml"));
                                Parent root       = loader.load();

                                /* PASAMOS EL NOMBRE del usuario al controlador principal */
                                AppController app = loader.getController();
                                app.setUserName(nombre);

                                Stage stage = (Stage) ((Node) event.getSource())
                                        .getScene().getWindow();
                                stage.setScene(new Scene(root));
                                stage.setTitle("Inicio");
                                stage.centerOnScreen();
                                stage.setMaximized(false);
                                stage.show();

                            } catch (IOException ex) {
                                ex.printStackTrace();
                                showAlert("No se pudo cargar la aplicación.");
                            }
                        });

                    } else {
                        showAlert("Login fallido: " + resp.body());
                    }
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    showAlert("No se pudo conectar al servidor.");
                    return null;
                });
    }

    /* ---------- navegación registro ---------- */
    @FXML
    private void goToRegister() {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/tfcc/registro.fxml"));
            Parent root   = fx.load();
            Stage stage   = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* ---------- util ---------- */
    private void showAlert(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Error");
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
