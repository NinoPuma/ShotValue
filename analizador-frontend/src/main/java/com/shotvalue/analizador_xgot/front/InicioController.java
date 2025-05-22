package com.shotvalue.analizador_xgot.front;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class InicioController {
    @FXML private Label fechaLabel, equiposCount, partidosCount, shotsCount, xgTotal;
    @FXML private ListView<String> recentsList;

    @FXML
    public void initialize() {
        fechaLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        // Aquí irías a tu servicio/DAO para cargar counts reales:
        equiposCount.setText("12");
        partidosCount.setText("35");
        shotsCount.setText("487");
        xgTotal.setText("28.45");

        // Y puebla recentsList con items:
        recentsList.getItems().setAll(
                "Shot Map vs Barcelona",
                "Informe Equipo A",
                "Nuevo jugador Deportivo Alavés"
        );
    }
}
