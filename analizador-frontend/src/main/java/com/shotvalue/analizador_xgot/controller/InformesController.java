package com.shotvalue.analizador_xgot.controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;

import java.time.LocalDate;

public class InformesController {

    @FXML private DatePicker fromDatePicker, toDatePicker;
    @FXML private ComboBox<String> reportTypeBox;
    @FXML private TableView<?> reportTable;
    @FXML private LineChart<String, Number> reportChart;

    @FXML
    public void initialize() {
        reportTypeBox.getItems().addAll(
                "Resumen de equipos",
                "Comparativa de jugadores",
                "Evolución xGOT",
                "Heatmap de zonas"
        );
        fromDatePicker.setValue(LocalDate.now().minusMonths(1));
        toDatePicker.setValue(LocalDate.now());
    }

    @FXML
    private void handleGenerate() {
        System.out.println("Generar informe: " + reportTypeBox.getValue());
    }

    @FXML
    private void exportPDF() {
        System.out.println("Exportar PDF");
    }

    @FXML
    private void exportCSV() {
        System.out.println("Exportar CSV");
    }

    @FXML
    private void scheduleDaily() {
        System.out.println("Informe diario programado");
    }

    @FXML
    private void scheduleWeekly() {
        System.out.println("Informe semanal programado");
    }
}
