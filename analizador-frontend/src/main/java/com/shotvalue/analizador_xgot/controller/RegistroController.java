package com.shotvalue.analizador_xgot.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.util.LocalDateAdapter;
import com.shotvalue.analizador_xgot.util.VentanaHelper;
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
    private TextField passwordTextField;
    @FXML
    private TextField repeatPasswordTextField;
    @FXML
    private Button togglePasswordBtn;
    @FXML
    private Button toggleRepeatPasswordBtn;
    @FXML
    private Label messageLabel;
    @FXML
    private Button registrarBtn;

    private boolean passwordVisible = false;
    private boolean repeatPasswordVisible = false;
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
        String password = passwordVisible ? passwordTextField.getText() : passwordField.getText();
        String repeatPassword = repeatPasswordVisible ? repeatPasswordTextField.getText() : repeatPasswordField.getText();
        boolean acceptedTerms = termsCheckBox.isSelected();

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || repeatPassword.isEmpty()) {
            showError("Todos los campos son obligatorios.");
            return;
        }

        if (username.length() < 3 || username.contains(" ")) {
            showError("El nombre de usuario debe tener al menos 3 caracteres y sin espacios.");
            return;
        }

        if (!email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            showError("El correo electrónico no es válido.");
            return;
        }

        if (password.length() < 6) {
            showError("La contraseña debe tener al menos 6 caracteres.");
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

        Usuario nuevoUsuario = new Usuario(null, username, email, password, null, null, null, null);
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
                VentanaHelper.cargarEscena(stage, "/tfcc/login.fxml", "Login");
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
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
            togglePasswordBtn.setText("🙈");
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
            togglePasswordBtn.setText("👁");
        }
    }

    @FXML
    private void toggleRepeatPasswordVisibility() {
        repeatPasswordVisible = !repeatPasswordVisible;
        if (repeatPasswordVisible) {
            repeatPasswordTextField.setText(repeatPasswordField.getText());
            repeatPasswordTextField.setVisible(true);
            repeatPasswordTextField.setManaged(true);
            repeatPasswordField.setVisible(false);
            repeatPasswordField.setManaged(false);
            toggleRepeatPasswordBtn.setText("🙈");
        } else {
            repeatPasswordField.setText(repeatPasswordTextField.getText());
            repeatPasswordField.setVisible(true);
            repeatPasswordField.setManaged(true);
            repeatPasswordTextField.setVisible(false);
            repeatPasswordTextField.setManaged(false);
            toggleRepeatPasswordBtn.setText("👁");
        }
    }

    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            VentanaHelper.cargarEscena(stage, "/tfcc/login.fxml", "Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
