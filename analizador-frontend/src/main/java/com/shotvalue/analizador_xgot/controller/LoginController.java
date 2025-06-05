package com.shotvalue.analizador_xgot.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shotvalue.analizador_xgot.api.AuthApiClient;
import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.session.UserSession;
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
import org.controlsfx.control.textfield.TextFields;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.time.LocalDate;
import java.util.*;

/**
 * Controlador de la pantalla de login.<br>
 * ▸ “Recordarme” (cifrado AES)<br>
 * ▸ Autocompletado de emails (ControlsFX)<br>
 * ▸ UserSession en memoria
 */
public class LoginController {

    /* ---------- FXML ---------- */
    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox      rememberMeCheckBox;

    /* ---------- Utilidades ---------- */
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    private static final String SESSION_FILE = System.getProperty("user.home") + "/.shotvalue/session.dat";
    private static final String EMAILS_FILE  = System.getProperty("user.home") + "/.shotvalue/emails-usados.json";
    private static final String SECRET_KEY   = "1234567890123456";   // 16 chars → AES-128

    /* ---------- Init ---------- */
    @FXML
    private void initialize() {
        // autocompletar con emails usados previamente
        TextFields.bindAutoCompletion(usernameField, loadUsedEmails());
        // intentar restaurar sesión recordada al abrir la ventana
        Platform.runLater(this::tryRestoreSession);
    }

    /* ---------- Login ---------- */
    @FXML
    private void handleLogin(ActionEvent evt) {
        String email = usernameField.getText().trim();
        String pass  = passwordField.getText().trim();

        if (email.isBlank() || pass.isBlank()) {
            showAlert("Completa todos los campos.");
            return;
        }

        AuthApiClient.loginAsync(email, pass)
                .thenAccept(usuario -> {
                    // Persistencia local
                    saveUsedEmail(email);
                    saveSession(email, usuario.getUsername(), rememberMeCheckBox.isSelected());

                    // Sesión en memoria
                    UserSession.start(usuario);

                    // Abrimos la app principal
                    Platform.runLater(() -> loadMainApp(evt, usuario));
                })
                .exceptionally(ex -> { showAlert(ex.getMessage()); return null; });
    }

    /* ---------- Cargar aplicación principal ---------- */
    private void loadMainApp(ActionEvent evt, Usuario usuario) {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/tfcc/app-layout.fxml"));
            Parent root   = fx.load();

            // Pasamos el nombre para saludar en la vista “Inicio”
            fx.<AppController>getController().setUserName(
                    Optional.ofNullable(usuario.getNombreCompleto())
                            .filter(s -> !s.isBlank())
                            .orElse(usuario.getUsername())
            );

            Stage st = evt != null
                    ? (Stage) ((Node) evt.getSource()).getScene().getWindow()
                    : new Stage();

            st.setScene(new Scene(root));
            st.setTitle("Inicio");
            st.centerOnScreen();
            st.setMaximized(false);
            st.show();

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("No se pudo cargar la aplicación.");
        }
    }

    /* ---------- “Recordarme” ---------- */
    private void tryRestoreSession() {
        try {
            Path p = Path.of(SESSION_FILE);
            if (!Files.exists(p)) return;

            String json = decrypt(Files.readString(p));
            if (json == null || json.isBlank()) return;

            Map<?,?> data = GSON.fromJson(json, Map.class);
            String email     = (String) data.get("email");
            String usuario   = (String) data.get("usuario");
            boolean recordar = Boolean.TRUE.equals(data.get("recordar"));

            if (email != null) usernameField.setText(email);
            if (recordar && usuario != null) {
                // Sesión “rápida” solo en cliente (no validamos con backend)
                Usuario u = new Usuario();
                u.setUsername(usuario);
                u.setEmail(email);
                UserSession.start(u);
                loadMainApp(null, u);
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void saveSession(String email, String usuario, boolean recordar) {
        try {
            String enc = encrypt(GSON.toJson(Map.of(
                    "email", email,
                    "usuario", usuario,
                    "recordar", recordar
            )));
            Path p = Path.of(SESSION_FILE);
            Files.createDirectories(p.getParent());
            Files.writeString(p, enc);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    public static void clearRememberMe() {
        try { Files.deleteIfExists(Path.of(SESSION_FILE)); }
        catch (IOException ignored) {}
    }

    /* ---------- Emails usados (autocompletado) ---------- */
    private List<String> loadUsedEmails() {
        try {
            Path p = Path.of(EMAILS_FILE);
            if (!Files.exists(p)) return List.of();

            List<Map<String,String>> lista =
                    GSON.fromJson(Files.readString(p), List.class);
            return lista.stream()
                    .map(m -> m.get("email"))
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException ex) { ex.printStackTrace(); return List.of(); }
    }

    private void saveUsedEmail(String email) {
        try {
            Path p = Path.of(EMAILS_FILE);
            List<Map<String,String>> lista = Files.exists(p)
                    ? GSON.fromJson(Files.readString(p), List.class)
                    : new ArrayList<>();

            if (lista.stream().noneMatch(m -> email.equals(m.get("email")))) {
                lista.add(Map.of("email", email));
                Files.createDirectories(p.getParent());
                Files.writeString(p, GSON.toJson(lista));
            }
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    /* ---------- AES helpers ---------- */
    private static String encrypt(String plain) {
        try {
            Key k = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, k);
            return Base64.getEncoder()
                    .encodeToString(c.doFinal(plain.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { e.printStackTrace(); return null; }
    }
    private static String decrypt(String cipherText) {
        try {
            Key k = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, k);
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            return new String(c.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    /* ---------- Navegar a registro ---------- */
    @FXML
    private void goToRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/tfcc/registro.fxml"));
            Stage st = (Stage) usernameField.getScene().getWindow();
            st.setScene(new Scene(root));
            st.centerOnScreen();
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    /* ---------- Alert helper ---------- */
    private void showAlert(String msg) {
        Platform.runLater(() -> new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK).showAndWait());
    }
}
