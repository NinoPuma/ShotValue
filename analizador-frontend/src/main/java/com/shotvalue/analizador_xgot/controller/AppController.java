package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.view.ViewLifecycle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AppController {

    @FXML private BorderPane mainPane;
    @FXML private AnchorPane contenidoCentro;

    @FXML private Button btnInicio, btnEquipos, btnRegistrar, btnVisualizar,
            btnInformes, btnPerfil, btnAyuda, btnSalir,
            btnCrearJugador, btnCrearEquipo;

    private final String defaultStyle = "-fx-background-color: transparent; -fx-text-fill: white;";
    private final String activeStyle  = "-fx-background-color: #0F7F7F; -fx-text-fill: white; -fx-font-weight: bold;";

    private final Map<String, Parent>        viewCache = new HashMap<>();
    private final Map<String, ViewLifecycle> ctlCache  = new HashMap<>();
    private       ViewLifecycle              controladorVisible;

    private String userName;

    /* =======================================================
     *  API pública
     * ======================================================= */
    public void setUserName(String name) {
        this.userName = name;
        if (controladorVisible instanceof InicioController ini) {
            ini.setNombreUsuario(name);
        }
    }

    public void openCrearEquipo() {
        cargarVista("/tfcc/crear-equipo-view.fxml", null);
    }

    /* =======================================================
     *  Inicialización
     * ======================================================= */
    @FXML
    private void initialize() {
        btnInicio       .setOnAction(e -> cargarVista("/tfcc/inicio-view.fxml",        btnInicio));
        btnEquipos      .setOnAction(e -> cargarVista("/tfcc/equipos-view.fxml",       btnEquipos));
        btnRegistrar    .setOnAction(e -> cargarVista("/tfcc/registrar-view.fxml",     btnRegistrar));
        btnCrearEquipo  .setOnAction(e -> cargarVista("/tfcc/crear-equipo-view.fxml",  btnCrearEquipo));
        btnCrearJugador .setOnAction(e -> cargarVista("/tfcc/crear-jugador-view.fxml", btnCrearJugador));
        btnVisualizar   .setOnAction(e -> cargarVista("/tfcc/visualizar-view.fxml",    btnVisualizar));
        btnInformes     .setOnAction(e -> cargarVista("/tfcc/informes-view.fxml",      btnInformes));
        btnPerfil       .setOnAction(e -> cargarVista("/tfcc/perfil-view.fxml",        btnPerfil));
        btnAyuda        .setOnAction(e -> cargarVista("/tfcc/ayuda-view.fxml",         btnAyuda));
        btnSalir        .setOnAction(e -> cerrarSesion());

        cargarVista("/tfcc/inicio-view.fxml", btnInicio);
    }

    /* =======================================================
     *  Navegación y estilos
     * ======================================================= */
    private void setContenido(Node n) {
        contenidoCentro.getChildren().setAll(n);
        AnchorPane.setTopAnchor   (n, 0.0);
        AnchorPane.setRightAnchor (n, 0.0);
        AnchorPane.setBottomAnchor(n, 0.0);
        AnchorPane.setLeftAnchor  (n, 0.0);
    }

    private void cargarVista(String rutaFXML, Button botonActivo) {
        try {
            // Forzamos recarga de “Inicio” para que muestre datos frescos
            if (rutaFXML.equals("/tfcc/inicio-view.fxml")) {
                viewCache.remove(rutaFXML);
                ctlCache .remove(rutaFXML);
            }

            Parent root = viewCache.computeIfAbsent(rutaFXML, ruta -> {
                try {
                    FXMLLoader fx = new FXMLLoader(getClass().getResource(ruta));
                    Parent nodo  = fx.load();
                    Object ctl   = fx.getController();

                    if (ctl instanceof ViewLifecycle life) ctlCache.put(ruta, life);

                    if (ctl instanceof InicioController ini) {
                        ini.setAppController(this);
                        if (userName != null) ini.setNombreUsuario(userName);
                    }
                    return nodo;
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            ViewLifecycle nuevo = ctlCache.get(rutaFXML);
            if (controladorVisible != null && controladorVisible != nuevo) controladorVisible.onHide();
            controladorVisible = nuevo;
            if (controladorVisible != null) controladorVisible.onShow();

            setContenido(root);
            resetearEstilosMenu();
            if (botonActivo != null) botonActivo.setStyle(activeStyle);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void resetearEstilosMenu() {
        btnInicio      .setStyle(defaultStyle);
        btnEquipos     .setStyle(defaultStyle);
        btnRegistrar   .setStyle(defaultStyle);
        btnCrearEquipo .setStyle(defaultStyle);
        btnCrearJugador.setStyle(defaultStyle);
        btnVisualizar  .setStyle(defaultStyle);
        btnInformes    .setStyle(defaultStyle);
        btnPerfil      .setStyle(defaultStyle);
        btnAyuda       .setStyle(defaultStyle);
    }

    /* =======================================================
     *  Cerrar sesión
     * ======================================================= */
    private void cerrarSesion() {
        try {
            /* unificamos el nombre del método “remember me” */
            LoginController.clearRememberMe(); // <-- usa el que hayas dejado como definitivo

            Stage stage = (Stage) contenidoCentro.getScene().getWindow();
            Parent login = FXMLLoader.load(getClass().getResource("/tfcc/login.fxml"));
            stage.setScene(new Scene(login));
            stage.setTitle("Login");
            stage.setMaximized(false);
            stage.centerOnScreen();

            viewCache.clear();
            ctlCache.clear();
            controladorVisible = null;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
