package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.EstadisticasApiClient;
import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.model.Tiro;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.List;

public class InicioController {

    @FXML
    private Label equiposCount;
    @FXML
    private Label partidosCount;
    @FXML
    private Label shotsCount;
    @FXML
    private Label xgTotal;
    @FXML
    private ListView<String> recentsList;
    @FXML
    private Label fechaLabel;
    @FXML
    private Label bienvenidaLabel;


    private String nombreUsuario;

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
        bienvenidaLabel.setText("¡Bienvenido, " + nombreUsuario + "!");
        cargarDatos(); // 👈 ahora lo llamamos desde acá
    }


    @FXML
    public void initialize() {
    }

    private void cargarDatos() {
        new Thread(() -> {
            try {
                List<Tiro> tiros = EstadisticasApiClient.getTiros();
                List<Jugador> jugadores = EstadisticasApiClient.getJugadores();
                int equipos = EstadisticasApiClient.getEquiposCount();
                int partidos = EstadisticasApiClient.getPartidosCount();

                Platform.runLater(() -> {
                    equiposCount.setText(String.valueOf(equipos));
                    partidosCount.setText(String.valueOf(partidos));
                    shotsCount.setText(String.valueOf(tiros.size()));
                    xgTotal.setText(String.format("%.2f", tiros.stream().mapToDouble(Tiro::getXgot).sum()));

                    recentsList.getItems().clear();
                    for (int i = 0; i < Math.min(5, tiros.size()); i++) {
                        Tiro tiro = tiros.get(i);
                        String jugadorNombre = jugadores.stream()
                                .filter(j -> j.getPlayerId() == tiro.getJugadorId())
                                .map(Jugador::getPlayerName)
                                .findFirst()
                                .orElse("Desconocido");

                        recentsList.getItems().add(jugadorNombre + " — min " + tiro.getMinuto());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

}
