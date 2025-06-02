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
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

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

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .create();

    private static final String ARCHIVO_SESION = System.getProperty("user.home") + "/.shotvalue/session.dat";
    private static final String ARCHIVO_EMAILS = System.getProperty("user.home") + "/.shotvalue/emails-usados.json";
    private static final String CLAVE_SECRETA = "1234567890123456";

    @FXML
    public void initialize() {
        TextFields.bindAutoCompletion(usernameField, cargarCorreosUsados());
        Platform.runLater(this::intentarRestaurarSesion);
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = usernameField.getText().trim();
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
                        Usuario usr = gson.fromJson(resp.body(), Usuario.class);
                        boolean recordar = rememberMeCheckBox.isSelected();

                        guardarCorreoUsado(email);
                        guardarSesion(email, usr.getUsername(), recordar);

                        Platform.runLater(() -> cargarApp(event, usr.getUsername()));
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

    private void cargarApp(ActionEvent event, String nombreUsuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/app-layout.fxml"));
            Parent root = loader.load();

            AppController app = loader.getController();
            app.setUserName(nombreUsuario);

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

    private void cargarApp(String nombreUsuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/app-layout.fxml"));
            Parent root = loader.load();

            AppController app = loader.getController();
            app.setUserName(nombreUsuario);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Inicio");
            stage.centerOnScreen();
            stage.setMaximized(false);
            stage.show();

            if (usernameField.getScene() != null) {
                ((Stage) usernameField.getScene().getWindow()).close();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("No se pudo cargar la aplicación.");
        }
    }

    private void intentarRestaurarSesion() {
        try {
            if (Files.exists(Path.of(ARCHIVO_SESION))) {
                String contenido = descifrar(Files.readString(Path.of(ARCHIVO_SESION)));
                if (contenido == null || contenido.isBlank()) return;

                if (contenido.trim().startsWith("{")) {
                    Map<?, ?> data = gson.fromJson(contenido, Map.class);
                    String email = (String) data.get("email");
                    String usuario = (String) data.get("usuario");
                    boolean recordar = Boolean.TRUE.equals(data.get("recordar"));

                    if (email != null) usernameField.setText(email);
                    if (recordar) cargarApp(usuario);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarSesion(String email, String nombreUsuario, boolean recordar) {
        try {
            Map<String, Object> datos = new HashMap<>();
            datos.put("email", email);
            datos.put("usuario", nombreUsuario);
            datos.put("recordar", recordar);
            String json = gson.toJson(datos);
            String cifrado = cifrar(json);

            Files.createDirectories(Path.of(ARCHIVO_SESION).getParent());
            Files.writeString(Path.of(ARCHIVO_SESION), cifrado);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void guardarCorreoUsado(String nuevoEmail) {
        try {
            List<Map<String, String>> lista = new ArrayList<>();
            if (Files.exists(Path.of(ARCHIVO_EMAILS))) {
                String json = Files.readString(Path.of(ARCHIVO_EMAILS));
                lista = gson.fromJson(json, List.class);
            }

            boolean yaExiste = lista.stream().anyMatch(map -> nuevoEmail.equals(map.get("email")));
            if (!yaExiste) {
                Map<String, String> nuevo = new HashMap<>();
                nuevo.put("email", nuevoEmail);
                lista.add(nuevo);
                Files.createDirectories(Path.of(ARCHIVO_EMAILS).getParent());
                Files.writeString(Path.of(ARCHIVO_EMAILS), gson.toJson(lista));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> cargarCorreosUsados() {
        try {
            if (!Files.exists(Path.of(ARCHIVO_EMAILS))) return List.of();
            String json = Files.readString(Path.of(ARCHIVO_EMAILS));
            List<Map<String, String>> lista = gson.fromJson(json, List.class);
            List<String> correos = new ArrayList<>();
            for (Map<String, String> item : lista) {
                correos.add(item.get("email"));
            }
            return correos;
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public static void desactivarRecordarSesion() {
        try {
            Path ruta = Path.of(ARCHIVO_SESION);
            if (Files.exists(ruta)) {
                String contenido = Files.readString(ruta);
                String json = descifrar(contenido);
                if (json != null && json.trim().startsWith("{")) {
                    Gson gson = new Gson();
                    Map<String, Object> data = gson.fromJson(json, Map.class);
                    data.put("recordar", false);
                    String nuevoJson = gson.toJson(data);
                    String cifrado = cifrar(nuevoJson);
                    Files.writeString(ruta, cifrado);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String cifrar(String texto) {
        try {
            Key key = new SecretKeySpec(CLAVE_SECRETA.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encrypted = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String descifrar(String textoCifrado) {
        try {
            Key key = new SecretKeySpec(CLAVE_SECRETA.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decoded = Base64.getDecoder().decode(textoCifrado);
            return new String(cipher.doFinal(decoded), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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

    private void showAlert(String msg) {
        Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle("Error");
            a.setHeaderText(null);
            a.setContentText(msg);
            a.showAndWait();
        });
    }
}
