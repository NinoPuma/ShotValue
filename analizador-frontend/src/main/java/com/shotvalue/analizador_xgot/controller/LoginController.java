package com.shotvalue.analizador_xgot.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import org.controlsfx.control.textfield.TextFields;   // auto-completado

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.util.*;

/**
 * Controlador de la pantalla de login.
 * Incluye:
 *  • “Recordarme” (sesión local cifrada AES)
 *  • Autocompletado de correos usados (ControlsFX)
 *  • UserSession in-memory para el resto de la app
 */
public class LoginController {

    @FXML private TextField     usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox      rememberMeCheckBox;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .create();

    private static final String SESSION_FILE = System.getProperty("user.home") + "/.shotvalue/session.dat";
    private static final String EMAILS_FILE  = System.getProperty("user.home") + "/.shotvalue/emails-usados.json";
    private static final String SECRET_KEY   = "1234567890123456";   // 16 chars AES-128

    /* -------- Inicialización -------- */
    @FXML
    public void initialize() {
        // autocompletar emails previamente usados
        TextFields.bindAutoCompletion(usernameField, loadUsedEmails());
        // intentar restaurar sesión recordada
        Platform.runLater(this::tryRestoreSession);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email    = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isBlank() || password.isBlank()) {
            showAlert("Completa todos los campos.");
            return;
        }

        Map<String, String> body = Map.of("email", email, "password", password);

        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(body), StandardCharsets.UTF_8))
                .build();

        httpClient.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenAccept(resp -> {
                    if (resp.statusCode() == 200) {
                        Usuario usuario = gson.fromJson(resp.body(), Usuario.class);

                        /* ---- Persistencia local ---- */
                        boolean recordar = rememberMeCheckBox.isSelected();
                        saveUsedEmail(email);
                        saveSession(email, usuario.getUsername(), recordar);

                        /* ---- Sesión en memoria ---- */
                        UserSession.start(usuario);

                        Platform.runLater(() -> loadMainApp(event, usuario));
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

    private void loadMainApp(ActionEvent event, Usuario usuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/app-layout.fxml"));
            Parent root = loader.load();

            AppController appController = loader.getController();
            String nombre = (usuario.getNombreCompleto() != null && !usuario.getNombreCompleto().isBlank())
                    ? usuario.getNombreCompleto()
                    : usuario.getUsername();
            appController.setUserName(nombre);   // método en AppController para mostrar el nombre

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Inicio");
            stage.centerOnScreen();
            stage.setMaximized(false);
            stage.show();
        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("No se pudo cargar la aplicación.");
        }
    }

    private void tryRestoreSession() {
        try {
            Path path = Path.of(SESSION_FILE);
            if (!Files.exists(path)) return;

            String json = decrypt(Files.readString(path));
            if (json == null || json.isBlank() || !json.trim().startsWith("{")) return;

            Map<?, ?> data = gson.fromJson(json, Map.class);
            String email    = (String) data.get("email");
            String usuario  = (String) data.get("usuario");
            boolean recordar = Boolean.TRUE.equals(data.get("recordar"));

            if (email != null) usernameField.setText(email);
            if (recordar && usuario != null) {
                // simulamos login directo solo para la interfaz;
                // si prefieres revalidar con backend, lanza handleLogin()
                loadMainApp(null, new Usuario(null, usuario, email, null,
                        usuario, null, null, null, null));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveSession(String email, String usuario, boolean recordar) {
        try {
            Map<String, Object> data = Map.of(
                    "email",    email,
                    "usuario",  usuario,
                    "recordar", recordar
            );
            String json = gson.toJson(data);
            String enc  = encrypt(json);

            Path p = Path.of(SESSION_FILE);
            Files.createDirectories(p.getParent());
            Files.writeString(p, enc);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveUsedEmail(String email) {
        try {
            List<Map<String, String>> lista = new ArrayList<>();
            Path p = Path.of(EMAILS_FILE);

            if (Files.exists(p)) {
                lista = gson.fromJson(Files.readString(p), List.class);
            }
            boolean exists = lista.stream()
                    .anyMatch(m -> email.equals(m.get("email")));
            if (!exists) {
                lista.add(Map.of("email", email));
                Files.createDirectories(p.getParent());
                Files.writeString(p, gson.toJson(lista));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> loadUsedEmails() {
        try {
            Path p = Path.of(EMAILS_FILE);
            if (!Files.exists(p)) return List.of();
            List<Map<String, String>> lista = gson.fromJson(Files.readString(p), List.class);
            List<String> emails = new ArrayList<>();
            for (Map<String, String> m : lista) emails.add(m.get("email"));
            return emails;
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /* -------- Navegar a registro -------- */
    @FXML
    private void goToRegister() {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/tfcc/registro.fxml"));
            Parent root = fx.load();
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String encrypt(String plain) {
        try {
            Key key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, key);
            return Base64.getEncoder()
                    .encodeToString(c.doFinal(plain.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { e.printStackTrace(); return null; }
    }
    private static String decrypt(String cipherText) {
        try {
            Key key = new SecretKeySpec(SECRET_KEY.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            return new String(c.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    /* -------- Alert helper -------- */
    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText(msg);
            a.showAndWait();
        });
    }

    public static void clearRememberMe() {
        try { Files.deleteIfExists(Path.of(SESSION_FILE)); }
        catch (IOException ignored) {}
    }
}
