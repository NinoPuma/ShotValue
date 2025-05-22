package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.config.SpringFXMLLoader;
import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.services.EquipoService;
import com.shotvalue.analizador_xgot.services.JugadorService;
import jakarta.annotation.PostConstruct;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EquiposContentController {

    @FXML private ComboBox<String> equipoSelector;
    @FXML private TextField jugadorSearchField;
    @FXML private TableView<Jugador> playerTable;

    private final ObservableList<Jugador> jugadoresOriginales = FXCollections.observableArrayList();
    private final ObservableList<Jugador> jugadoresFiltrados = FXCollections.observableArrayList();

    @Autowired
    private EquipoService equipoService;

    @Autowired
    private JugadorService jugadorService;

    @Autowired
    private SpringFXMLLoader springFXMLLoader;

    private List<Equipo> equiposDisponibles;

    @FXML
    public void initialize() {
        configurarTabla();
        configurarBuscador();
        cargarEquipos();
    }

    private void configurarTabla() {
        TableColumn<Jugador, String> colNum = new TableColumn<>("Dorsal");
        colNum.setCellValueFactory(j -> new ReadOnlyStringWrapper(j.getValue().getJersey_number()));

        TableColumn<Jugador, String> colName = new TableColumn<>("Nombre completo");
        colName.setCellValueFactory(j -> new ReadOnlyStringWrapper(j.getValue().getPlayer_name()));

        TableColumn<Jugador, String> colPos = new TableColumn<>("Posición");
        colPos.setCellValueFactory(j -> new ReadOnlyStringWrapper(j.getValue().getPosition()));

        playerTable.getColumns().setAll(colNum, colName, colPos);
        playerTable.setItems(jugadoresFiltrados);
    }

    private void cargarEquipos() {
        equiposDisponibles = equipoService.getAll();


        equipoSelector.setItems(FXCollections.observableArrayList(
                equiposDisponibles.stream().map(Equipo::getTeam_name).collect(Collectors.toList())
        ));

        equipoSelector.setOnAction(e -> {
            String nombreSeleccionado = equipoSelector.getValue();
            System.out.println(">> Seleccionado: " + nombreSeleccionado);

            Equipo equipo = equiposDisponibles.stream()
                    .filter(eq -> eq.getTeam_name().equals(nombreSeleccionado))
                    .findFirst().orElse(null);

            if (equipo != null) {
                cargarJugadores(String.valueOf(equipo.getTeam_id()));
            }
        });
    }

    private void cargarJugadores(String equipoId) {
        int teamId = Integer.parseInt(equipoId);
        List<Jugador> jugadores = jugadorService.getByTeamId(teamId);
        jugadoresOriginales.setAll(jugadores);
        jugadoresFiltrados.setAll(jugadores);
    }

    private void configurarBuscador() {
        jugadorSearchField.textProperty().addListener((obs, oldVal, newVal) -> {
            jugadoresFiltrados.setAll(
                    jugadoresOriginales.stream()
                            .filter(j -> j.getPlayer_name().toLowerCase().contains(newVal.toLowerCase()))
                            .collect(Collectors.toList())
            );
        });
    }

}
