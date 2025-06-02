package com.shotvalue.analizador_xgot.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shotvalue.analizador_xgot.api.AuthApiClient;
import com.shotvalue.analizador_xgot.model.Usuario;
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

public class LoginController {

    @FXML private TextField      usernameField;
    @FXML private PasswordField  passwordField;
    @FXML private CheckBox       rememberMeCheckBox;

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
            .create();

    private static final String ARCHIVO_SESION = System.getProperty("user.home") + "/.shotvalue/session.dat";
    private static final String ARCHIVO_EMAILS = System.getProperty("user.home") + "/.shotvalue/emails-usados.json";
    private static final String CLAVE_SECRETA  = "1234567890123456";

    @FXML
    private void initialize() {
        TextFields.bindAutoCompletion(usernameField, cargarCorreosUsados());
        Platform.runLater(this::intentarRestaurarSesion);
    }

    @FXML
    private void handleLogin(ActionEvent evt) {

        String email = usernameField.getText().trim();
        String pass  = passwordField.getText().trim();

        if (email.isBlank() || pass.isBlank()) { showAlert("Completa todos los campos."); return; }

        AuthApiClient.loginAsync(email, pass)
                .thenAccept(user -> {
                    guardarCorreoUsado(email);
                    guardarSesion(email, user.getUsername(), rememberMeCheckBox.isSelected());
                    Platform.runLater(() -> cargarApp(evt, user.getUsername()));
                })
                .exceptionally(ex -> { showAlert(ex.getMessage()); return null; });
    }

    private void cargarApp(ActionEvent evt, String nombreUsuario) {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/tfcc/app-layout.fxml"));
            Parent root   = fx.load();

            fx.<AppController>getController().setUserName(nombreUsuario);

            Stage st;
            if (evt != null) {
                st = (Stage) ((Node) evt.getSource()).getScene().getWindow();
            } else {
                st = new Stage();
            }
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

    private void intentarRestaurarSesion() {
        try {
            Path p = Path.of(ARCHIVO_SESION);
            if (!Files.exists(p)) return;

            String json = descifrar(Files.readString(p));
            if (json == null || json.isBlank()) return;

            Map<?,?> data   = GSON.fromJson(json, Map.class);
            String email    = (String) data.get("email");
            String usuario  = (String) data.get("usuario");
            boolean recordar = Boolean.TRUE.equals(data.get("recordar"));

            if (email != null) usernameField.setText(email);
            if (recordar) cargarApp(null, usuario);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private void guardarSesion(String email, String usuario, boolean recordar) {
        try {
            Map<String,Object> m = Map.of("email", email, "usuario", usuario, "recordar", recordar);
            String cifrado = cifrar(GSON.toJson(m));

            Files.createDirectories(Path.of(ARCHIVO_SESION).getParent());
            Files.writeString(Path.of(ARCHIVO_SESION), cifrado);
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    public static void desactivarRecordarSesion() {
        try {
            Path p = Path.of(ARCHIVO_SESION);
            if (!Files.exists(p)) return;

            String json = descifrar(Files.readString(p));
            if (json == null || json.isBlank()) return;

            Map<String,Object> data = GSON.fromJson(json, Map.class);
            data.put("recordar", false);                      // ✅ desactivamos
            String nuevoCifrado = cifrar(GSON.toJson(data));
            Files.writeString(p, nuevoCifrado);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    private List<String> cargarCorreosUsados() {
        try {
            Path p = Path.of(ARCHIVO_EMAILS);
            if (!Files.exists(p)) return List.of();

            List<Map<String,String>> lista =
                    GSON.fromJson(Files.readString(p), List.class);
            return lista.stream()
                    .map(m -> m.get("email"))
                    .filter(Objects::nonNull).toList();

        } catch (IOException ex) { ex.printStackTrace(); return List.of(); }
    }

    private void guardarCorreoUsado(String email) {
        try {
            Path p = Path.of(ARCHIVO_EMAILS);
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

    private static String cifrar(String txt) {
        try {
            Key k = new SecretKeySpec(CLAVE_SECRETA.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, k);
            return Base64.getEncoder().encodeToString(c.doFinal(txt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) { e.printStackTrace(); return null; }
    }
    private static String descifrar(String enc) {
        try {
            Key k = new SecretKeySpec(CLAVE_SECRETA.getBytes(), "AES");
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, k);
            return new String(c.doFinal(Base64.getDecoder().decode(enc)), StandardCharsets.UTF_8);
        } catch (Exception e) { e.printStackTrace(); return null; }
    }

    /* ──────────── Ir a registro ──────────── */
    @FXML
    private void goToRegister() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/tfcc/registro.fxml"));
            Stage st = (Stage) usernameField.getScene().getWindow();
            st.setScene(new Scene(root));
            st.centerOnScreen();
        } catch (IOException ex) { ex.printStackTrace(); }
    }

    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR, msg, ButtonType.OK);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.showAndWait();
        });
    }
}
