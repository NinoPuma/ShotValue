package com.shotvalue.analizador_xgot.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.model.Jugador;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EquiposController {

    // ─── URL base del backend ─────────────────────────────────────────
    private static final String BASE_URL = "http://localhost:8080/api";

    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final Type listEqTy = new TypeToken<List<Equipo>>() {
    }.getType();
    private final Type listJuTy = new TypeToken<List<Jugador>>() {
    }.getType();

    // ─── UI ───────────────────────────────────────────────────────────
    @FXML
    private ComboBox<Equipo> equipoSelector;          // ¡ahora genérico!
    @FXML
    private TextField jugadorSearchField;
    @FXML
    private TableView<Jugador> playerTable;

    private final ObservableList<Jugador> jugadoresOriginales = FXCollections.observableArrayList();
    private final ObservableList<Jugador> jugadoresFiltrados = FXCollections.observableArrayList();

    // ──────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        configurarTabla();
        configurarBuscador();
        cargarEquipos();
    }

    // ─── Configura columnas de la tabla ───────────────────────────────
    private void configurarTabla() {
        TableColumn<Jugador, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(j -> {
            String nombre = j.getValue().getPlayerName();
            return new ReadOnlyStringWrapper(nombre != null ? nombre : "Sin nombre");
        });
        colNombre.setPrefWidth(300);

        TableColumn<Jugador, String> colDorsal = new TableColumn<>("Dorsal");
        colDorsal.setCellValueFactory(j -> {
            String dorsal = j.getValue().getJerseyNumber();
            return new ReadOnlyStringWrapper(dorsal != null ? dorsal : "-");
        });
        colDorsal.setPrefWidth(100);

        TableColumn<Jugador, String> colPos = new TableColumn<>("Posición");
        colPos.setCellValueFactory(j -> {
            String posicion = j.getValue().getPosition();
            return new ReadOnlyStringWrapper(posicion != null ? posicion : "Sin posición");
        });
        colPos.setPrefWidth(200);

        playerTable.getColumns().setAll(colNombre, colPos, colDorsal);
        playerTable.setItems(jugadoresFiltrados);
        
        playerTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    }



    // ─── Descarga la lista de equipos ─────────────────────────────────
    private void cargarEquipos() {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/equipos"))
                .GET()
                .build();

        http.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    Type tipoLista = new TypeToken<List<Equipo>>() {
                    }.getType();
                    return gson.fromJson(json, tipoLista); // ✅ tipo ahora sí reconocido
                })
                .thenAccept(lista -> poblarComboEquipos((List<Equipo>) lista))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });

    }

    private void poblarComboEquipos(List<Equipo> equipos) {
        Platform.runLater(() -> {
            equipoSelector.setItems(FXCollections.observableArrayList(equipos));

            // manejador de selección
            equipoSelector.setOnAction(e -> {
                Equipo seleccionado = equipoSelector.getSelectionModel().getSelectedItem();
                if (seleccionado != null) {
                    cargarJugadores(seleccionado.getTeamId());
                }
            });
        });
    }

    // ─── Descarga los jugadores del equipo seleccionado ──────────────
    private void cargarJugadores(int teamId) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/api/jugadores/porEquipo/" + teamId))
                .GET()
                .build();

        http.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(json -> {
                    Type tipoLista = new TypeToken<List<Jugador>>() {
                    }.getType();
                    return gson.fromJson(json, tipoLista);
                })
                .thenAccept(lista -> poblarTablaJugadores((List<Jugador>) lista))
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    return null;
                });
    }


    private void poblarTablaJugadores(List<Jugador> jugadores) {
        Platform.runLater(() -> {
            jugadoresOriginales.setAll(jugadores);
            jugadoresFiltrados.setAll(jugadores);
        });
    }

    // ─── Filtro de búsqueda por nombre ────────────────────────────────
    private void configurarBuscador() {
        jugadorSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            jugadoresFiltrados.setAll(
                    jugadoresOriginales.stream()
                            .filter(j -> j.getPlayerName()
                                    .toLowerCase()
                                    .contains(newVal.toLowerCase()))
                            .collect(Collectors.toList())
            );
        });
    }
}
