package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.EquipoJugadorApiClient;
import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.model.Jugador;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

public class EquiposController {

    @FXML private ComboBox<String> equipoSelector;
    @FXML private TextField jugadorSearchField;
    @FXML private TableView<Jugador> playerTable;

    private final ObservableList<Jugador> jugadoresOriginales = FXCollections.observableArrayList();
    private final ObservableList<Jugador> jugadoresFiltrados = FXCollections.observableArrayList();

    private List<Equipo> equiposDisponibles;

    @FXML
    public void initialize() {
        configurarTabla();
        configurarBuscador();
        cargarEquipos();
    }

    private void configurarTabla() {
        TableColumn<Jugador, String> colNum = new TableColumn<>("Dorsal");
        colNum.setCellValueFactory(j -> new ReadOnlyStringWrapper(j.getValue().getJerseyNumber()));

        TableColumn<Jugador, String> colName = new TableColumn<>("Nombre completo");
        colName.setCellValueFactory(j -> new ReadOnlyStringWrapper(j.getValue().getPlayerName()));

        TableColumn<Jugador, String> colPos = new TableColumn<>("Posición");
        colPos.setCellValueFactory(j -> new ReadOnlyStringWrapper(j.getValue().getPosition()));

        playerTable.getColumns().setAll(colNum, colName, colPos);
        playerTable.setItems(jugadoresFiltrados);
    }

    private void cargarEquipos() {
        new Thread(() -> {
            try {
                equiposDisponibles = EquipoJugadorApiClient.getEquipos();
                Platform.runLater(() -> {
                    equipoSelector.setItems(FXCollections.observableArrayList(
                            equiposDisponibles.stream().map(Equipo::getTeamName).collect(Collectors.toList())
                    ));

                    equipoSelector.setOnAction(e -> {
                        String nombreSeleccionado = equipoSelector.getValue();
                        Equipo equipo = equiposDisponibles.stream()
                                .filter(eq -> eq.getTeamName().equals(nombreSeleccionado))
                                .findFirst().orElse(null);

                        if (equipo != null) {
                            cargarJugadores(equipo.getTeamId());
                        }
                    });
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void cargarJugadores(int teamId) {
        new Thread(() -> {
            try {
                List<Jugador> jugadores = EquipoJugadorApiClient.getJugadoresPorEquipo(teamId);
                Platform.runLater(() -> {
                    jugadoresOriginales.setAll(jugadores);
                    jugadoresFiltrados.setAll(jugadores);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void configurarBuscador() {
        jugadorSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            jugadoresFiltrados.setAll(
                    jugadoresOriginales.stream()
                            .filter(j -> j.getPlayerName().toLowerCase().contains(newVal.toLowerCase()))
                            .collect(Collectors.toList())
            );
        });
    }
}
