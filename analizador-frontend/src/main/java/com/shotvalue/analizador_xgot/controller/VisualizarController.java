package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.TiroApiClient;
import com.shotvalue.analizador_xgot.model.Tiro;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.*;
import java.util.ResourceBundle;

public class VisualizarController implements Initializable {

    @FXML private ComboBox<String> periodBox;
    @FXML private Spinner<Integer> minuteFromSpinner, minuteToSpinner;
    @FXML private ComboBox<String> teamSideBox;
    @FXML private TextField playerSearchField;
    @FXML private ComboBox<String> thirdBox, laneBox;
    @FXML private ComboBox<String> eventTypeBox, visualizationTypeBox;
    @FXML private Button applyFiltersBtn;
    @FXML private ComboBox<String> areaBox, situationBox, bodyPartBox, preActionBox, resultBox;
    @FXML private TextField xgField;
    @FXML private ImageView fieldMap, goalView;
    @FXML private Label legendLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        areaBox.getItems().addAll("Cualquier zona", "Área chica", "Área grande", "Fuera del área");
        situationBox.getItems().addAll("Cualquier situación", "Juego abierto", "Balón parado", "Contraataque");
        bodyPartBox.getItems().addAll("Cualquier parte", "Pie izquierdo", "Pie derecho", "Cabeza", "Otro");
        preActionBox.getItems().addAll("Todas las acciones", "Pase", "Regate", "Rebote", "Centro");
        resultBox.getItems().addAll("Todos los resultados", "Gol", "Atajado", "Fuera", "Bloqueado");

        SpinnerValueFactory<Integer> fromFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 0);
        SpinnerValueFactory<Integer> toFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 90);
        minuteFromSpinner.setValueFactory(fromFactory);
        minuteToSpinner.setValueFactory(toFactory);

        eventTypeBox.getItems().setAll("Tiros", "Pases", "Duelos");
        eventTypeBox.setValue("Tiros");

        visualizationTypeBox.getItems().setAll(
                "Mapa de tiros",
                "Análisis de goles",
                "Tiros + Goles",
                "Mapa de calor",
                "Zonas xGOT"
        );
        visualizationTypeBox.setValue("Tiros + Goles");

        periodBox.getItems().setAll("Todos los períodos", "1° Tiempo", "2° Tiempo", "ET", "Penales");
        periodBox.setValue("Todos los períodos");

        teamSideBox.getItems().setAll("Ambos equipos", "Local", "Visitante");
        teamSideBox.setValue("Ambos equipos");

        thirdBox.getItems().setAll("Todos", "Defensivo", "Medio", "Ofensivo");
        thirdBox.setValue("Todos");

        laneBox.getItems().setAll("Todos", "Izquierdo", "Central", "Derecho");
        laneBox.setValue("Todos");

        areaBox.setValue("Cualquier zona");
        situationBox.setValue("Cualquier situación");
        bodyPartBox.setValue("Cualquier parte");
        preActionBox.setValue("Todas las acciones");
        resultBox.setValue("Todos los resultados");

        xgField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            return newText.matches("\\d{0,3}(\\.\\d{0,2})?") ? change : null;
        }));

        applyFiltersBtn.setOnAction(event -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        Map<String, String> filtros = new HashMap<>();
        filtros.put("minutoDesde", String.valueOf(minuteFromSpinner.getValue()));
        filtros.put("minutoHasta", String.valueOf(minuteToSpinner.getValue()));
        filtros.put("bodyPart", bodyPartBox.getValue());
        filtros.put("preAction", preActionBox.getValue());
        filtros.put("result", resultBox.getValue());
        filtros.put("area", areaBox.getValue());
        filtros.put("xg", xgField.getText().trim());

        new Thread(() -> {
            try {
                List<Tiro> tiros = TiroApiClient.filtrarTiros(filtros);
                Platform.runLater(() -> {
                    System.out.println("🔎 Tiros encontrados: " + tiros.size());
                    for (Tiro t : tiros) {
                        System.out.println("- " + t);
                    }
                    // Acá podés actualizar el gráfico/mapa/etc.
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
