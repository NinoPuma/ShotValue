package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.EquiposApiClient;
import com.shotvalue.analizador_xgot.api.EstadisticasApiClient;
import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.model.Tiro;
import com.shotvalue.analizador_xgot.view.ViewLifecycle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class InicioController implements ViewLifecycle {


    @FXML private Label            equiposCount;
    @FXML private Label            partidosCount;
    @FXML private Label            shotsCount;
    @FXML private Label            xgTotal;
    @FXML private ListView<String> recentsList;
    @FXML private Label            bienvenidaLabel;
    @FXML private Button           btnCrearEquipo;
    @FXML private Button           btnVisualizar;
    @FXML private Button           btnPerfil;
    @FXML private Button           btnCrearJugador;
    @FXML private Button           btnCrearTiro;

    /* ────────── refs ────────── */
    private AppController appController;   // se inyecta desde AppController
    private String        nombreUsuario;

    /* ============ Setters desde AppController ============ */
    public void setAppController(AppController app) {
        this.appController = app;
    }
    public void setNombreUsuario(String nombre) {
        this.nombreUsuario = nombre;
        bienvenidaLabel.setText("¡Bienvenido, " + nombre + "!");
    }

    /* ============ inicialización FXML ============ */
    @FXML
    private void initialize() {
        btnCrearEquipo.setOnAction(e -> {
            if (appController != null) appController.openCrearEquipo();
        });
        btnCrearTiro.setOnAction(e -> {
            if (appController != null) appController.openCrearTiro();
        });
        btnVisualizar.setOnAction(e -> {
            if (appController != null) appController.openVisualizar();
        });
        btnCrearJugador.setOnAction(e -> {
            if (appController != null) appController.openCrearJugador();
        });
        btnPerfil.setOnAction(e -> {
            if (appController != null) appController.openPerfil();
        });
    }

    /* ============ ViewLifecycle ============ */
    @Override
    public void onShow() {
        cargarDatos();        // SIEMPRE que se muestra, refrescamos
    }
    @Override public void onHide() { /* no-op */ }

    /* ============ carga de datos asíncrona ============ */
    private void cargarDatos() {
        new Thread(() -> {
            try {
                List<Tiro>     tiros     = EstadisticasApiClient.getTiros();
                List<Jugador>  jugadores = EstadisticasApiClient.getJugadores();
                List<Equipo>   equiposList = EquiposApiClient.getEquiposSync();
                int            equipos     = EstadisticasApiClient.getEquiposCount();
                int            partidos    = EstadisticasApiClient.getPartidosCount();

                Platform.runLater(() -> {
                    equiposCount  .setText(String.valueOf(equipos));
                    partidosCount .setText(String.valueOf(partidos));
                    shotsCount    .setText(String.valueOf(tiros.size()));
                    xgTotal       .setText(String.format("%.2f",
                            tiros.stream().mapToDouble(Tiro::getXgot).sum()));

                    recentsList.getItems().clear();
                    for (int i = Math.max(0, equiposList.size() - 3); i < equiposList.size(); i++) {
                        Equipo eq = equiposList.get(i);
                        recentsList.getItems().add("Equipo creado: " + eq.getName());
                    }

                    // Últimos jugadores creados
                    for (int i = Math.max(0, jugadores.size() - 3); i < jugadores.size(); i++) {
                        Jugador j = jugadores.get(i);
                        recentsList.getItems().add("Jugador creado: " + j.getPlayerName());
                    }

                    // Últimos tiros registrados
                    for (int i = 0; i < Math.min(3, tiros.size()); i++) {
                        Tiro t = tiros.get(i);
                        String nombreJ = jugadores.stream()
                                .filter(j -> j.getPlayerId() == t.getJugadorId())
                                .map(Jugador::getPlayerName)
                                .findFirst()
                                .orElse("Desconocido");
                        recentsList.getItems().add("Tiro de " + nombreJ + " — min " + t.getMinuto());
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
