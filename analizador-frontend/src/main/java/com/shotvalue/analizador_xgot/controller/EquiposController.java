package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.EquiposApiClient;      // <--- importamos
import com.shotvalue.analizador_xgot.api.JugadorApiClient;
import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.model.Jugador;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EquiposController {

    @FXML private ComboBox<Equipo> equipoSelector;
    @FXML private TextField jugadorSearchField;
    @FXML private TableView<Jugador> playerTable;

    private final ObservableList<Jugador> jugadoresOriginales = FXCollections.observableArrayList();
    private final ObservableList<Jugador> jugadoresFiltrados  = FXCollections.observableArrayList();

    private final JugadorApiClient jugadorApi = new JugadorApiClient();

    @FXML
    public void initialize() {
        configurarTabla();
        configurarBuscador();
        cargarEquipos();
    }

    private void configurarTabla() {
        TableColumn<Jugador,String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(j ->
                new ReadOnlyStringWrapper(
                        j.getValue().getPlayerName() != null
                                ? j.getValue().getPlayerName()
                                : "Sin nombre"
                ));
        colNombre.setPrefWidth(200);

        TableColumn<Jugador,String> colDorsal = new TableColumn<>("Dorsal");
        colDorsal.setCellValueFactory(j ->
                new ReadOnlyStringWrapper(
                        j.getValue().getJerseyNumber() != null
                                ? j.getValue().getJerseyNumber()
                                : "-"
                ));
        colDorsal.setPrefWidth(80);

        TableColumn<Jugador,String> colPos = new TableColumn<>("Posición");
        colPos.setCellValueFactory(j ->
                new ReadOnlyStringWrapper(
                        j.getValue().getPosition() != null
                                ? j.getValue().getPosition()
                                : "Sin posición"
                ));
        colPos.setPrefWidth(120);

        playerTable.getColumns().setAll(colNombre, colPos, colDorsal);
        playerTable.setItems(jugadoresFiltrados);
        playerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void cargarEquipos() {
        // Usamos el método asíncrono del API client
        EquiposApiClient.getEquiposAsync()
                .thenAccept(this::poblarComboEquipos)
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }

    private void poblarComboEquipos(List<Equipo> equipos) {
        Platform.runLater(() -> {
            equipoSelector.setItems(FXCollections.observableArrayList(equipos));
            equipoSelector.setOnAction(evt -> {
                Equipo sel = equipoSelector.getValue();
                if (sel != null) {
                    cargarJugadores(sel.getTeamId());
                }
            });
        });
    }

    private void cargarJugadores(int teamId) {
        CompletableFuture.supplyAsync(() -> {
                    try {
                        return jugadorApi.getJugadoresPorEquipo(teamId);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).thenAccept(this::poblarTablaJugadores)
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    private void poblarTablaJugadores(List<Jugador> jugadores) {
        Platform.runLater(() -> {
            jugadoresOriginales.setAll(jugadores);
            jugadoresFiltrados.setAll(jugadores);
        });
    }

    private void configurarBuscador() {
        jugadorSearchField.textProperty()
                .addListener((obs, o, n) ->
                        jugadoresFiltrados.setAll(
                                jugadoresOriginales.stream()
                                        .filter(j -> j.getPlayerName() != null
                                                && j.getPlayerName().toLowerCase().contains(n.toLowerCase()))
                                        .collect(Collectors.toList())
                        )
                );
    }
}
