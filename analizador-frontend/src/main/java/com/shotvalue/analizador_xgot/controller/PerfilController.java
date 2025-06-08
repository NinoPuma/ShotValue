// PerfilController.java
package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.PerfilApiClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shotvalue.analizador_xgot.util.LocalDateAdapter;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class PerfilController {

    @FXML private TextField usernameField;
    @FXML private Label emailLabel;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Button saveBtn;
    @FXML private Button logoutBtn;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .create();

    private String userId;

    @FXML
    public void initialize() {
        // Cargar userId de la sesión cifrada
        try {
            String cif = Files.readString(Path.of(System.getProperty("user.home"), ".shotvalue", "session.dat"));
            String json = descifrar(cif);
            @SuppressWarnings("unchecked")
            Map<String,Object> map = gson.fromJson(json, Map.class);
            userId = (String) map.get("id");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Cargar datos del perfil
        PerfilApiClient.fetchProfile(userId)
                .thenAccept(u -> Platform.runLater(() -> {
                    usernameField.setText(u.getUsername());
                    emailLabel.setText(u.getEmail());
                }))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert("No se pudo cargar perfil"));
                    return null;
                });

        // Asignar manejadores
        saveBtn.setOnAction(this::onSave);
        logoutBtn.setOnAction(this::onLogout);
    }

    private void onSave(ActionEvent ev) {
        String nombre = usernameField.getText().trim();
        String curPwd = currentPasswordField.getText();
        String newPwd = newPasswordField.getText();
        String conf   = confirmPasswordField.getText();

        if (nombre.isEmpty()) {
            showAlert("El nombre no puede quedar vacío.");
            return;
        }
        if (!newPwd.isBlank()) {
            if (!newPwd.equals(conf)) {
                showAlert("La nueva contraseña no coincide.");
                return;
            }
            if (curPwd.isBlank()) {
                showAlert("Introduce tu contraseña actual.");
                return;
            }
        }

        Map<String,Object> payload = new HashMap<>();
        payload.put("username", nombre);
        if (!newPwd.isBlank()) {
            payload.put("currentPassword", curPwd);
            payload.put("newPassword", newPwd);
        }

        PerfilApiClient.updateProfile(userId, payload)
                .thenRun(() -> Platform.runLater(() -> showAlert("Perfil actualizado")))
                .exceptionally(ex -> {
                    Platform.runLater(() -> showAlert("Error al guardar perfil: " + ex.getMessage()));
                    return null;
                });
    }

    private void onLogout(ActionEvent ev) {
        // Guardar estado de la ventana
        Stage stage = (Stage)((Node)ev.getSource()).getScene().getWindow();
        double w = stage.getWidth(), h = stage.getHeight();
        boolean maximized = stage.isMaximized();

        try {
            Files.deleteIfExists(Path.of(System.getProperty("user.home"), ".shotvalue", "session.dat"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/login.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, w, h);
                stage.setScene(scene);
                stage.setMaximized(maximized);
                stage.setTitle("Login");
                stage.show();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
    }

    private void showAlert(String msg) {
        new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK).showAndWait();
    }

    private static String descifrar(String cifrado) throws Exception {
        var key = new SecretKeySpec("1234567890123456".getBytes(), "AES");
        var c   = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] dec = java.util.Base64.getDecoder().decode(cifrado.getBytes(StandardCharsets.UTF_8));
        return new String(c.doFinal(dec), StandardCharsets.UTF_8);
    }
}
