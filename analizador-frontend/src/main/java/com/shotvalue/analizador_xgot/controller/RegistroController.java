package com.shotvalue.analizador_xgot.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.util.LocalDateAdapter;
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
import java.time.LocalDate;

public class RegistroController {

    @FXML
    private TextField usernameField;
    @FXML
    private TextField nombreCompletoField;
    @FXML
    private TextField emailField;
    @FXML
    private ComboBox<String> rolComboBox;
    @FXML
    private TextField telefonoField;
    @FXML
    private DatePicker fechaNacimientoPicker;
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField repeatPasswordField;
    @FXML
    private CheckBox termsCheckBox;
    @FXML
    private Label messageLabel;
    @FXML
    private Button registrarBtn;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();


    @FXML
    public void initialize() {
        registrarBtn.setOnAction(e -> handleRegister());
    }

    @FXML
    private void handleRegister() {
        String username = usernameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String repeatPassword = repeatPasswordField.getText();
        boolean acceptedTerms = termsCheckBox.isSelected();

        // Validación campos vacíos
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            showError("Todos los campos son obligatorios.");
            return;
        }

        // Validación usuario
        if (username.length() < 3 || username.contains(" ")) {
            showError("El nombre de usuario debe tener al menos 3 caracteres y sin espacios.");
            return;
        }

        // Validación email simple
        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            showError("El correo electrónico no es válido.");
            return;
        }

        // Validación contraseña
        if (password.length() < 6) {
            showError("La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        if (!password.equals(repeatPassword)) {
            showError("Las contraseñas no coinciden.");
            return;
        }

        // Validación términos
        if (!acceptedTerms) {
            showError("Debes aceptar los términos y condiciones.");
            return;
        }

        // Si todo está bien, crear el usuario con TODOS los campos
        String nombreCompleto     = nombreCompletoField.getText().trim();
        String rol                = rolComboBox.getValue();          // asegúrate de que no sea null
        String telefono           = telefonoField.getText().trim();
        LocalDate fechaNacimiento = fechaNacimientoPicker.getValue(); // puede ser null si el usuario no lo puso

        Usuario nuevoUsuario = new Usuario(
                null,            // id (lo generará el backend)
                username,
                email,
                password,
                nombreCompleto,
                rol,
                telefono,
                fechaNacimiento,
                null             // avatarUrl (lo subirás más adelante, si procede)
        );

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
                    if (response.statusCode() == 200 || response.statusCode() == 201) {
                        showSuccess("¡Registro exitoso!");
                    } else {
                        showError("Error en el registro: " + response.body());
                    }
                });
    }

    private void showSuccess(String msg) {
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) usernameField.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/login.fxml"));
                Parent root = loader.load();
                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                stage.centerOnScreen();
                stage.setMaximized(false);
                stage.show();
            } catch (IOException e) {
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
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.centerOnScreen();
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
