// src/main/java/com/shotvalue/analizador_xgot/controller/PerfilController.java
package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class PerfilController {

    /* ---------- FXML fields ---------- */
    @FXML private ImageView avatarImage;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> roleBox;
    @FXML private DatePicker birthDatePicker;

    // Campos para cambio de contraseña (aún no usados)
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    /* ---------- Inicialización ---------- */
    @FXML
    private void initialize() {
        Usuario u = UserSession.get();
        if (u == null) {
            // Sin sesión: podrías redirigir al login o mostrar un error
            return;
        }

        // Rellenar datos
        nameField.setText(u.getNombreCompleto());    // ajusta a getUsername() si así lo llamas
        emailField.setText(u.getEmail());
        phoneField.setText(u.getTelefono());
        birthDatePicker.setValue(u.getFechaNacimiento());

        roleBox.getItems().setAll("ADMIN", "USUARIO");
        roleBox.getSelectionModel().select(u.getRol());

        // Avatar (si tienes URL o base64)
        if (u.getAvatarUrl() != null && !u.getAvatarUrl().isBlank()) {
            avatarImage.setImage(new Image(u.getAvatarUrl(), true));
        }
    }

    /* ---------- Acciones de los botones ---------- */

    /** Abre un diálogo para seleccionar una nueva imagen y la muestra (no la guarda aún). */
    @FXML
    private void handleChangeImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Seleccionar nueva imagen");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        Stage stage = (Stage) avatarImage.getScene().getWindow();
        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            avatarImage.setImage(new Image(file.toURI().toString()));
            // TODO: Subir la imagen al servidor y actualizar UserSession
        }
    }

    /** Guarda los cambios del perfil (datos y/o contraseña). */
    @FXML
    private void handleSaveChanges() {
        // TODO: Validar campos, construir petición PUT/POST, enviar y actualizar UserSession
        Alert a = new Alert(Alert.AlertType.INFORMATION,
                "Funcionalidad de guardar pendientes por implementar.");
        a.showAndWait();
    }

    /** Limpia la sesión y vuelve a la pantalla de login. */
    @FXML
    private void handleLogout() {
        UserSession.clear();   // 🔥 limpiar usuario en memoria

        // Cargar la vista de login
        // (Ajusta la ruta a tu login-view.fxml o login.fxml)
        try {
            javafx.fxml.FXMLLoader loader =
                    new javafx.fxml.FXMLLoader(getClass().getResource("/tfcc/login.fxml"));
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) avatarImage.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle("Login");
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Envía una petición para eliminar la cuenta y cierra la app o redirige al login. */
    @FXML
    private void handleDeleteAccount() {
        // TODO: Confirmar con el usuario y llamar DELETE /api/usuarios/{id}
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Estás seguro de que quieres eliminar tu cuenta? Esta acción es irreversible.",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                // TODO: Llamar al backend
                UserSession.clear();
                ((Stage) avatarImage.getScene().getWindow()).close();
            }
        });
    }
}
