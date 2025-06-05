// src/main/java/com/shotvalue/analizador_xgot/controller/PerfilController.java
package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.session.UserSession;
import com.shotvalue.analizador_xgot.api.UsuarioApiClient;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.time.LocalDate;
import java.util.Objects;

/**
 * Controlador de la vista de perfil.
 */
public class PerfilController {

    /* ------------------------- FXML fields ------------------------- */
    @FXML private ImageView avatarImage;
    @FXML private TextField usernameField;          // solo lectura
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleBox;
    @FXML private DatePicker birthDatePicker;
    @FXML private Button saveBtn;
    @FXML private Button logoutBtn;
    @FXML private Button deleteBtn;

    // Cambio de contraseña
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    /* ------------------------- State ------------------------------ */
    private File avatarFile;
    private final UsuarioApiClient api = new UsuarioApiClient();

    /* ------------------------ Init -------------------------------- */
    @FXML
    private void initialize() {
        Usuario u = UserSession.get();
        if (u == null) {
            new Alert(Alert.AlertType.ERROR, "Sesión expirada. Inicia sesión de nuevo.").showAndWait();
            return;
        }

        // Usuario (nuevo campo) — no editable
        if (usernameField != null) {
            usernameField.setText(empty(u.getUsername()));
            usernameField.setEditable(false);
        }

        // Rellenar datos restantes
        nameField.setText(empty(u.getNombreCompleto()));
        emailField.setText(empty(u.getEmail()));
        phoneField.setText(empty(u.getTelefono()));
        birthDatePicker.setValue(u.getFechaNacimiento());

        roleBox.getItems().setAll("ADMIN", "USUARIO");
        roleBox.getSelectionModel().select(u.getRol());

        if (u.getAvatarUrl() != null && !u.getAvatarUrl().isBlank()) {
            avatarImage.setImage(new Image(u.getAvatarUrl(), true));
        }
    }

    /* --------------------- Handlers ------------------------------- */

    @FXML
    private void handleChangeImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar nueva imagen");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));

        File file = chooser.showOpenDialog(avatarImage.getScene().getWindow());
        if (file == null) return;

        avatarImage.setImage(new Image(file.toURI().toString()));
        avatarFile = file;

        saveBtn.setDisable(true);
        Task<Void> upload = new Task<>() {
            @Override
            protected Void call() throws Exception {
                String url = api.subirAvatar(UserSession.get().getId(), file);
                Usuario u = UserSession.get();
                u.setAvatarUrl(url);
                UserSession.set(u);
                return null;
            }
        };
        upload.setOnSucceeded(e -> saveBtn.setDisable(false));
        upload.setOnFailed(e -> {
            saveBtn.setDisable(false);
            upload.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo subir la imagen").showAndWait();
        });
        new Thread(upload, "upload-avatar").start();
    }

    @FXML
    private void handleSaveChanges() {
        if (!camposValidos()) return;

        Usuario u = UserSession.get();
        u.setNombreCompleto(empty(nameField.getText()).trim());
        u.setEmail(empty(emailField.getText()).trim());
        u.setTelefono(empty(phoneField.getText()).trim());
        u.setFechaNacimiento(birthDatePicker.getValue());
        u.setRol(roleBox.getValue());

        String nueva = empty(newPasswordField.getText()).trim();
        String actual = empty(currentPasswordField.getText()).trim();
        boolean cambiar = !nueva.isEmpty();

        saveBtn.setDisable(true);
        Task<Usuario> t = new Task<>() {
            @Override
            protected Usuario call() throws Exception {
                return api.actualizarUsuario(u.getId(), u, cambiar ? actual : null, cambiar ? nueva : null);
            }
        };
        t.setOnSucceeded(e -> {
            UserSession.set(t.getValue());
            saveBtn.setDisable(false);
            new Alert(Alert.AlertType.INFORMATION, "Perfil actualizado").showAndWait();
            currentPasswordField.clear();
            newPasswordField.clear();
            confirmPasswordField.clear();
        });
        t.setOnFailed(e -> {
            saveBtn.setDisable(false);
            t.getException().printStackTrace();
            new Alert(Alert.AlertType.ERROR, "No se pudo guardar: " + t.getException().getMessage()).showAndWait();
        });
        new Thread(t, "update-user").start();
    }

    @FXML
    private void handleLogout() {
        UserSession.clear();
        cargarLogin();
    }

    @FXML
    private void handleDeleteAccount() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "¿Estás seguro de que quieres eliminar tu cuenta? Esta acción es irreversible.", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(r -> {
            if (r == ButtonType.YES) {
                deleteBtn.setDisable(true);
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        api.eliminarUsuario(UserSession.get().getId());
                        return null;
                    }
                };
                task.setOnSucceeded(e -> {
                    UserSession.clear();
                    cargarLogin();
                });
                task.setOnFailed(e -> {
                    deleteBtn.setDisable(false);
                    task.getException().printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "No se pudo eliminar la cuenta").showAndWait();
                });
                new Thread(task, "delete-user").start();
            }
        });
    }

    /* --------------------- Validation ----------------------------- */
    private boolean camposValidos() {
        String nombre = nameField.getText();
        if (nombre == null || nombre.isBlank()) {
            aviso("El nombre no puede estar vacío");
            return false;
        }
        String email = emailField.getText();
        if (email == null || email.isBlank() || !email.contains("@")) {
            aviso("Introduce un email válido");
            return false;
        }
        if (birthDatePicker.getValue() == null || birthDatePicker.getValue().isAfter(LocalDate.now())) {
            aviso("Selecciona una fecha de nacimiento válida");
            return false;
        }
        String nueva = newPasswordField.getText();
        if (nueva != null && !nueva.isBlank()) {
            if (currentPasswordField.getText() == null || currentPasswordField.getText().isBlank()) {
                aviso("Debes introducir la contraseña actual para cambiarla");
                return false;
            }
            if (!Objects.equals(nueva, confirmPasswordField.getText())) {
                aviso("La nueva contraseña y su confirmación no coinciden");
                return false;
            }
            if (nueva.length() < 6) {
                aviso("La nueva contraseña debe tener al menos 6 caracteres");
                return false;
            }
        }
        return true;
    }

    private void aviso(String msg) {
        new Alert(Alert.AlertType.WARNING, msg).showAndWait();
    }

    /* -------------------- Utilities ------------------------------- */
    private String empty(String s) { return s == null ? "" : s; }

    private void cargarLogin() {
        Platform.runLater(() -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/tfcc/login.fxml"));
                Stage stage = (Stage) avatarImage.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Login");
                stage.centerOnScreen();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
