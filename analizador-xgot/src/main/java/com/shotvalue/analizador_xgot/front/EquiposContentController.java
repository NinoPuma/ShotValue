package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.services.EquipoService;
import com.shotvalue.analizador_xgot.services.JugadorService;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.List;
import java.util.stream.Collectors;

public class EquiposContentController {

    @FXML private ComboBox<String> equipoSelector;
    @FXML private TextField jugadorSearchField;
    @FXML private TableView<Jugador> playerTable;

    private final ObservableList<Jugador> jugadoresOriginales = FXCollections.observableArrayList();
    private final ObservableList<Jugador> jugadoresFiltrados = FXCollections.observableArrayList();

    private EquipoService equipoService;
    private JugadorService jugadorService;

    private List<Equipo> equiposDisponibles;

    // Métodos para inyección manual
    public void setEquipoService(EquipoService equipoService) {
        this.equipoService = equipoService;
    }

    public void setJugadorService(JugadorService jugadorService) {
        this.jugadorService = jugadorService;
    }

    @FXML
    public void initialize() {
        configurarTabla();
        configurarBuscador();
    }

    private void configurarTabla() {
        TableColumn<Jugador, String> colNum = new TableColumn<>("Dorsal");
        colNum.setCellValueFactory(j -> new ReadOnlyStringWrapper(String.valueOf(j.getValue().getDorsal())));

        TableColumn<Jugador, String> colName = new TableColumn<>("Nombre");
        colName.setCellValueFactory(j -> new ReadOnlyStringWrapper(j.getValue().getNombre()));

        TableColumn<Jugador, String> colApellido = new TableColumn<>("Apellido");
        colApellido.setCellValueFactory(j -> new ReadOnlyStringWrapper(j.getValue().getApellido()));

        TableColumn<Jugador, String> colPos = new TableColumn<>("Posición");
        colPos.setCellValueFactory(j -> new ReadOnlyStringWrapper(j.getValue().getPosicion()));

        playerTable.getColumns().setAll(colNum, colName, colApellido, colPos);
        playerTable.setItems(jugadoresFiltrados);
    }

    public void cargarEquipos() {
        equiposDisponibles = equipoService.getAll();
        equipoSelector.setItems(FXCollections.observableArrayList(
                equiposDisponibles.stream().map(Equipo::getNombre).collect(Collectors.toList())
        ));

        equipoSelector.setOnAction(e -> {
            String nombreSeleccionado = equipoSelector.getValue();
            Equipo equipo = equiposDisponibles.stream()
                    .filter(eq -> eq.getNombre().equals(nombreSeleccionado))
                    .findFirst().orElse(null);

            if (equipo != null) {
                cargarJugadores(equipo.getId());
            }
        });
    }

    private void cargarJugadores(String equipoId) {
        List<Jugador> jugadores = jugadorService.getByEquipoId(equipoId);
        jugadoresOriginales.setAll(jugadores);
        jugadoresFiltrados.setAll(jugadores);
    }

    private void configurarBuscador() {
        jugadorSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            jugadoresFiltrados.setAll(
                    jugadoresOriginales.stream()
                            .filter(j -> (j.getNombre() + " " + j.getApellido())
                                    .toLowerCase()
                                    .contains(newVal.toLowerCase()))
                            .collect(Collectors.toList())
            );
        });
    }
}
