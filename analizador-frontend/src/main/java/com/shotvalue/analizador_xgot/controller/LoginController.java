package com.shotvalue.analizador_xgot.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.shotvalue.analizador_xgot.api.AuthApiClient;
import com.shotvalue.analizador_xgot.model.Usuario;
import com.shotvalue.analizador_xgot.util.LocalDateAdapter;
import com.shotvalue.analizador_xgot.util.VentanaHelper;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.TextFields;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private CheckBox rememberMeCheckBox;
    @FXML private TextField passwordTextField;
    @FXML private Button togglePasswordBtn;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(java.time.LocalDate.class, new LocalDateAdapter())
            .create();
    private boolean passwordVisible = false;

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
        String password = passwordVisible
                ? passwordTextField.getText().trim()
                : passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Completa todos los campos.");
            return;
        }

        CompletableFuture<Usuario> future = AuthApiClient.loginAsync(email, password);
        future.thenAccept(usr -> {
            boolean recordar = rememberMeCheckBox.isSelected();
            guardarCorreoUsado(email);
            // <-- aquí pasamos también el id devuelto:
            guardarSesion(email, usr.getUsername(), usr.getId(), recordar);
            Platform.runLater(() -> cargarApp(event, usr.getUsername()));
        }).exceptionally(ex -> {
            Platform.runLater(() -> showAlert("Login fallido: " + ex.getMessage()));
            return null;
        });
    }

    private void cargarApp(ActionEvent event, String nombreUsuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tfcc/app-layout.fxml"));
            Parent root = loader.load();
            AppController app = loader.getController();
            app.setUserName(nombreUsuario);

            Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            if (root instanceof Region region) {
                region.prefWidthProperty().bind(stage.widthProperty());
                region.prefHeightProperty().bind(stage.heightProperty());
            }
            stage.setScene(scene);
            stage.setTitle("Inicio");
            stage.setMaximized(true);
        } catch (IOException e) {
            e.printStackTrace();
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
            Scene scene = new Scene(root);
            if (root instanceof Region region) {
                region.prefWidthProperty().bind(stage.widthProperty());
                region.prefHeightProperty().bind(stage.heightProperty());
            }
            stage.setScene(scene);
            stage.setTitle("Inicio");
            stage.setMaximized(true);
            if (usernameField.getScene() != null) {
                ((Stage)usernameField.getScene().getWindow()).close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("No se pudo cargar la aplicación.");
        }
    }

    private void intentarRestaurarSesion() {
        try {
            Path ruta = Path.of(ARCHIVO_SESION);
            if (!Files.exists(ruta)) return;
            String contenido = descifrar(Files.readString(ruta));
            if (contenido == null || !contenido.trim().startsWith("{")) return;

            Map<?,?> data = gson.fromJson(contenido, Map.class);
            String email = (String)data.get("email");
            String usuario = (String)data.get("usuario");
            boolean recordar = Boolean.TRUE.equals(data.get("recordar"));

            if (email != null) usernameField.setText(email);
            if (recordar) cargarApp(usuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Nueva firma de guardarSesion que incluye userId
    private void guardarSesion(String email,
                               String nombreUsuario,
                               String userId,       // <— nuevo parámetro
                               boolean recordar) {
        try {
            Map<String,Object> datos = new HashMap<>();
            datos.put("email",   email);
            datos.put("usuario", nombreUsuario);
            datos.put("id",      userId);       // <— lo guardamos aquí
            datos.put("recordar",recordar);
            String json = gson.toJson(datos);
            String cifrado = cifrar(json);

            Path ruta = Path.of(ARCHIVO_SESION);
            Files.createDirectories(ruta.getParent());
            Files.writeString(ruta, cifrado);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void guardarCorreoUsado(String nuevoEmail) {
        try {
            Path ruta = Path.of(ARCHIVO_EMAILS);
            List<Map<String,String>> lista = Files.exists(ruta)
                    ? gson.fromJson(Files.readString(ruta), List.class)
                    : new ArrayList<>();
            if (lista.stream().noneMatch(m -> nuevoEmail.equals(m.get("email")))) {
                lista.add(Map.of("email", nuevoEmail));
                Files.createDirectories(ruta.getParent());
                Files.writeString(ruta, gson.toJson(lista));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> cargarCorreosUsados() {
        try {
            Path ruta = Path.of(ARCHIVO_EMAILS);
            if (!Files.exists(ruta)) return List.of();
            List<Map<String,String>> lista = gson.fromJson(Files.readString(ruta), List.class);
            List<String> correos = new ArrayList<>();
            for (Map<String,String> m : lista) correos.add(m.get("email"));
            return correos;
        } catch (IOException e) {
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Desactiva la opción de recordar sesión guardada.
     * Uso: LoginController.desactivarRecordarSesion();
     */
    public static void desactivarRecordarSesion() {
        try {
            Path ruta = Path.of(ARCHIVO_SESION);
            if (!Files.exists(ruta)) return;
            String contenido = descifrar(Files.readString(ruta));
            if (contenido != null && contenido.trim().startsWith("{")) {
                Gson tmp = new Gson();
                Map<String,Object> data = tmp.fromJson(contenido, Map.class);
                data.put("recordar", false);
                String nuevoJson = tmp.toJson(data);
                String cifrado = cifrar(nuevoJson);
                Files.writeString(ruta, cifrado);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    private void goToRegister() {
        try {
            Stage stage = (Stage)usernameField.getScene().getWindow();
            VentanaHelper.cargarEscena(stage, "/tfcc/registro.fxml", "Registro");
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

    private static String cifrar(String texto) throws Exception {
        Key key = new SecretKeySpec(CLAVE_SECRETA.getBytes(), "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] enc = c.doFinal(texto.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(enc);
    }

    private static String descifrar(String textoCifrado) throws Exception {
        Key key = new SecretKeySpec(CLAVE_SECRETA.getBytes(), "AES");
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(textoCifrado);
        return new String(c.doFinal(decoded), StandardCharsets.UTF_8);
    }
}
