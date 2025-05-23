package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.model.Tiro;
import com.shotvalue.analizador_xgot.services.*;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Component
public class InicioController {

    @FXML private Label equiposCount;
    @FXML private Label partidosCount;
    @FXML private Label shotsCount;
    @FXML private Label xgTotal;
    @FXML private ListView<String> recentsList;

    @Autowired private JugadorService jugadorService;
    @Autowired private EquipoService equipoService;
    @Autowired private PartidoService partidoService;
    @Autowired private TiroService tiroService;

    @FXML
    public void initialize() {
        System.out.println("InicioController: initialize()");
        cargarEstadisticas();
        cargarRecientes();
    }

    private void cargarEstadisticas() {
        try {
            equiposCount.setText(String.valueOf(equipoService.getAll().size()));
            partidosCount.setText(String.valueOf(partidoService.getAll().size()));
            shotsCount.setText(String.valueOf(tiroService.getAll().size()));
            xgTotal.setText(String.format("%.2f", tiroService.getAll().stream()
                    .mapToDouble(t -> t.getXgot()).sum()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarRecientes() {
        List<Tiro> tiros = tiroService.getAll();
        int limite = Math.min(5, tiros.size());

        for (int i = 0; i < limite; i++) {
            Tiro tiro = tiros.get(i);
            String nombreJugador;

            Optional<Jugador> jugadorOpt = jugadorService.getById(tiro.getJugadorId());
            if (jugadorOpt.isPresent()) {
                Jugador jugador = jugadorOpt.get();
                nombreJugador = jugador.getPlayer_name();
            } else {
                nombreJugador = "Desconocido";
            }

            String descripcion = nombreJugador + " — min " + tiro.getMinuto();
            recentsList.getItems().add(descripcion);
        }
    }

    public InicioController() {
        System.out.println("InicioController: constructor invocado");
    }

    @Autowired
    private ImportadorPartidosService importador;

    @PostConstruct
    public void init() {
        try {
            if (partidoService.getAll().isEmpty()) {
                System.out.println("Importando partidos...");
                Path carpeta = Paths.get("C:/Users/Santi/Downloads/open-data-master/open-data-master/data/matches");
                importador.importarPartidosDesdeCarpeta(carpeta);
            } else {
                System.out.println("Partidos ya importados, no se vuelve a cargar.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
