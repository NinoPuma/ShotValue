package com.shotvalue.analizador_xgot.front;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;

import java.time.LocalDate;

public class InformesController {
    @FXML private DatePicker fromDatePicker, toDatePicker;
    @FXML private ComboBox<String> reportTypeBox;
    @FXML private TableView<?> reportTable;
    @FXML private LineChart<String, Number> reportChart;

    @FXML
    public void initialize() {
        // Inicializa tipos de informe
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
        // Lógica: llama a tu servicio/DAO para obtener datos y poblar
        // reportTable.getItems().setAll(...);
        // reportChart.getData().setAll(...);
    }

    @FXML
    private void exportPDF() { /* tu implementación */ }

    @FXML
    private void exportCSV() { /* tu implementación */ }

    @FXML
    private void scheduleDaily() { /* agenda tarea diaria */ }

    @FXML
    private void scheduleWeekly() { /* agenda tarea semanal */ }
}
