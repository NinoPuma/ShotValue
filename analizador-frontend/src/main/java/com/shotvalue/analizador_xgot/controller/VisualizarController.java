package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.JugadorApiClient;
import com.shotvalue.analizador_xgot.api.TiroApiClient;
import com.shotvalue.analizador_xgot.model.Tiro;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.*;

public class VisualizarController implements Initializable {

    @FXML
    private ComboBox<String> periodBox;
    @FXML
    private Spinner<Integer> minuteFromSpinner, minuteToSpinner;
    @FXML
    private TextField playerSearchField;
    @FXML
    private ComboBox<String> thirdBox, laneBox;
    @FXML
    private Button applyFiltersBtn;
    @FXML
    private ComboBox<String> areaBox, situationBox, bodyPartBox, preActionBox, resultBox;
    @FXML
    private TextField xgField;
    @FXML
    private ImageView goalView;
    @FXML
    private Label legendLabel;
    @FXML
    private Label xgotInfoLabel;
    @FXML
    private Canvas canvasTiros;
    @FXML
    private Canvas canvasArco;
    @FXML
    private ImageView fieldMap;

    private JugadorApiClient jugadorApiClient = new JugadorApiClient();

    private List<Tiro> ultimoTiros = new ArrayList<>();

    private static final double ESCALA_X = 354.4 / 120.0;
    private static final double ESCALA_Y = 244.0 / 80.0;
    private static final double OFFSET_X = 73.6;
    private static final double OFFSET_Y = 4.0;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        areaBox.getItems().addAll("Cualquier zona", "Área chica", "Área grande", "Fuera del área");
        situationBox.getItems().addAll("Cualquier situación", "Juego abierto", "Balón parado");
        bodyPartBox.getItems().addAll("Cualquier parte", "Pie izquierdo", "Pie derecho", "Cabeza", "Otro");
        preActionBox.getItems().addAll("Todas las acciones", "Pase", "Regate", "Rebote", "Centro", "Penal", "No definido");
        resultBox.getItems().addAll("Todos los resultados", "Gol", "Atajado", "Fuera", "Bloqueado");

        minuteFromSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 0));
        minuteToSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 120));

        periodBox.getItems().setAll("Todos los períodos", "1° Tiempo", "2° Tiempo", "ET - 1° Tiempo", "ET - 2° Tiempo", "Penales");
        periodBox.setValue("Todos los períodos");

        thirdBox.getItems().setAll("Todos", "Defensivo", "Medio", "Ofensivo");
        thirdBox.setValue("Todos");

        laneBox.getItems().setAll("Todos", "Izquierdo", "Central", "Derecho");
        laneBox.setValue("Todos");

        areaBox.setValue("Cualquier zona");
        situationBox.setValue("Cualquier situación");
        bodyPartBox.setValue("Cualquier parte");
        preActionBox.setValue("Todas las acciones");
        resultBox.setValue("Todos los resultados");

        xgField.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d{0,3}(\\.\\d{0,2})?") ? change : null));

        canvasTiros.toFront();
        canvasTiros.setMouseTransparent(false);
        canvasArco.toFront();
        canvasArco.setMouseTransparent(false);

        canvasTiros.setOnMouseClicked(e -> manejarClickTiro(e.getX(), e.getY()));
        canvasArco.setOnMouseClicked(e -> manejarClickArco(e.getX(), e.getY()));

        canvasTiros.setOnMouseClicked(e -> manejarClickTiro(e.getX(), e.getY()));

        applyFiltersBtn.setOnAction(event -> aplicarFiltros());

        periodBox.setOnAction(e -> {
            String selected = periodBox.getValue();
            boolean disableMinutos = false;

            switch (selected) {
                case "1° Tiempo" -> {
                    minuteFromSpinner.getValueFactory().setValue(0);
                    minuteToSpinner.getValueFactory().setValue(45);
                }
                case "2° Tiempo" -> {
                    minuteFromSpinner.getValueFactory().setValue(45);
                    minuteToSpinner.getValueFactory().setValue(90);
                }
                case "ET - 1° Tiempo" -> {
                    minuteFromSpinner.getValueFactory().setValue(90);
                    minuteToSpinner.getValueFactory().setValue(105);
                }
                case "ET - 2° Tiempo" -> {
                    minuteFromSpinner.getValueFactory().setValue(105);
                    minuteToSpinner.getValueFactory().setValue(120);
                }
                case "Penales" -> {
                    disableMinutos = true;
                    minuteFromSpinner.getValueFactory().setValue(120);
                    minuteToSpinner.getValueFactory().setValue(120);
                }
                case "Todos los períodos" -> {
                    minuteFromSpinner.getValueFactory().setValue(0);
                    minuteToSpinner.getValueFactory().setValue(120);
                }
                default -> {
                    minuteFromSpinner.setOpacity(1.0);
                    minuteToSpinner.setOpacity(1.0);
                }
            }

            minuteFromSpinner.setDisable(disableMinutos);
            minuteToSpinner.setDisable(disableMinutos);
            minuteFromSpinner.setOpacity(disableMinutos ? 0.5 : 1.0);
            minuteToSpinner.setOpacity(disableMinutos ? 0.5 : 1.0);
        });

        new Thread(() -> {
            try {
                List<String> nombres = jugadorApiClient.getNombresCompletos();
                Platform.runLater(() -> TextFields.bindAutoCompletion(playerSearchField, nombres));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        xgotInfoLabel.setText("Promedio xGOT: 0.00");
    }

    private void aplicarFiltros() {
        Map<String, String> filtros = new HashMap<>();
        String periodoSeleccionado = periodBox.getValue();

        String periodo = switch (periodBox.getValue()) {
            case "1° Tiempo" -> "1";
            case "2° Tiempo" -> "2";
            case "ET - 1° Tiempo" -> "3";
            case "ET - 2° Tiempo" -> "4";
            case "Penales" -> "5";
            default -> "";
        };

        if (!periodo.isEmpty()) filtros.put("period", periodo);

        filtros.put("minutoDesde", String.valueOf(minuteFromSpinner.getValue()));
        filtros.put("minutoHasta", String.valueOf(minuteToSpinner.getValue()));

        filtros.put("third", thirdBox.getValue());
        filtros.put("lane", laneBox.getValue());
        filtros.put("situation", situationBox.getValue());

        filtros.put("bodyPart", bodyPartBox.getValue());
        filtros.put("preAction", preActionBox.getValue());
        filtros.put("result", resultBox.getValue());
        filtros.put("area", areaBox.getValue());
        filtros.put("xg", xgField.getText().trim());
        filtros.put("jugador", playerSearchField.getText().trim());

        new Thread(() -> {
            try {
                List<Tiro> tiros = TiroApiClient.filtrarTiros(filtros);
                Platform.runLater(() -> {
                    ultimoTiros = tiros;
                    drawTirosInternal(tiros);
                    dibujarEnArco(tiros);
                    double avg = tiros.stream().mapToDouble(Tiro::getXgot).average().orElse(0.0);
                    xgotInfoLabel.setText(String.format("Promedio xGOT: %.2f", avg));
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void drawTirosInternal(List<Tiro> tiros) {
        GraphicsContext gc = canvasTiros.getGraphicsContext2D();
        double width = canvasTiros.getWidth();
        double height = canvasTiros.getHeight();
        gc.clearRect(0, 0, width, height);

        double escalaX = ESCALA_X;
        double escalaY = ESCALA_Y;
        double offsetX = OFFSET_X;
        double offsetY = OFFSET_Y;

        for (Tiro tiro : tiros) {
            if (tiro.getDestinoX() == null || tiro.getDestinoY() == null) continue;

            double x1 = offsetX + tiro.getX() * escalaX;
            double y1 = offsetY + tiro.getY() * escalaY;
            double x2 = offsetX + tiro.getDestinoX() * escalaX;
            double y2 = offsetY + tiro.getDestinoY() * escalaY;

            String resultado = tiro.getResultado() != null ? tiro.getResultado().trim().toLowerCase() : "";

            Color color = switch (resultado) {
                case "gol" -> Color.LIMEGREEN;
                case "atajado" -> Color.GOLD;
                case "fuera" -> Color.CRIMSON;
                case "bloqueado" -> Color.ORANGE;
                case "poste" -> Color.GRAY;
                default -> Color.WHITE;
            };

            gc.setStroke(color);
            gc.setLineWidth(2.0);
            gc.strokeLine(x1, y1, x2, y2);
            gc.setFill(color);
            gc.fillOval(x1 - 3, y1 - 3, 6, 6);
            gc.fillOval(x2 - 3, y2 - 3, 6, 6);
        }
    }

    private void manejarClickArco(double x, double y) {
        double canvasWidth = canvasArco.getWidth();
        double canvasHeight = canvasArco.getHeight();

        double paddingX = 8.0;
        double paddingY = 8.0;
        double drawWidth = canvasWidth - 2 * paddingX;
        double drawHeight = canvasHeight - 2 * paddingY;

        double arcoYMin = 30.0;
        double arcoYMax = 50.0;
        double arcoZMin = 0.0;
        double arcoZMax = 2.44;

        for (Tiro tiro : ultimoTiros) {
            String resultado = tiro.getResultado() != null ? tiro.getResultado().trim().toLowerCase() : "";
            if (!(resultado.equals("gol") || resultado.equals("atajadoo") || resultado.equals("atajado") || resultado.equals("poste")))
                continue;

            Double destinoY = tiro.getDestinoY();
            Double destinoZ = tiro.getDestinoZ();
            if (destinoY == null) continue;

            if ("poste".equalsIgnoreCase(resultado)) {
                destinoY = (destinoY < 40.0) ? 34.5 : 45.5;
                if (destinoZ == null || destinoZ < 0.2) destinoZ = 0.2;
            }

            if (destinoZ == null || destinoZ < 0.2) destinoZ = 0.2;
            if (destinoZ > arcoZMax) destinoZ = arcoZMax;

            double xRel = (destinoY - arcoYMin) / (arcoYMax - arcoYMin);
            double yRel = 1.0 - (destinoZ - arcoZMin) / (arcoZMax - arcoZMin);

            double xCanvas = paddingX + xRel * drawWidth;
            double yCanvas = paddingY + yRel * drawHeight;

            if (Math.hypot(x - xCanvas, y - yCanvas) <= 6) {
                xgotInfoLabel.setText(String.format("xGOT del tiro: %.2f", tiro.getXgot()));
                return;
            }
        }

        double avg = ultimoTiros.stream().mapToDouble(Tiro::getXgot).average().orElse(0.0);
        xgotInfoLabel.setText(String.format("Promedio xGOT: %.2f", avg));
    }

    private void manejarClickTiro(double x, double y) {
        for (Tiro tiro : ultimoTiros) {
            if (tiro.getDestinoX() == null || tiro.getDestinoY() == null) continue;

            double sx = OFFSET_X + tiro.getX() * ESCALA_X;
            double sy = OFFSET_Y + tiro.getY() * ESCALA_Y;

            if (Math.hypot(x - sx, y - sy) <= 6) {
                xgotInfoLabel.setText(String.format("xGOT del tiro: %.2f", tiro.getXgot()));
                return;
            }
        }

        double avg = ultimoTiros.stream().mapToDouble(Tiro::getXgot).average().orElse(0.0);
        xgotInfoLabel.setText(String.format("Promedio xGOT: %.2f", avg));
    }

    private void dibujarEnArco(List<Tiro> tiros) {
        GraphicsContext gc = canvasArco.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasArco.getWidth(), canvasArco.getHeight());

        double canvasWidth = canvasArco.getWidth();
        double canvasHeight = canvasArco.getHeight();

        double paddingX = 8.0;
        double paddingY = 8.0;
        double drawWidth = canvasWidth - 2 * paddingX;
        double drawHeight = canvasHeight - 2 * paddingY;

        double arcoYMin = 30.0;
        double arcoYMax = 50.0;
        double arcoZMin = 0.0;
        double arcoZMax = 2.44;

        for (Tiro tiro : tiros) {
            String resultado = tiro.getResultado() != null ? tiro.getResultado().trim().toLowerCase() : "";
            if (!(resultado.equals("gol") || resultado.equals("atajadoo") || resultado.equals("atajado") || resultado.equals("poste")))
                continue;

            Double destinoY = tiro.getDestinoY();
            Double destinoZ = tiro.getDestinoZ();

            if (destinoY == null) continue;

            if ("poste".equalsIgnoreCase(resultado)) {
                destinoY = (destinoY < 40.0) ? 34.5 : 45.5;
                if (destinoZ == null || destinoZ < 0.2) destinoZ = 0.2;
            }

            if (destinoZ == null || destinoZ < 0.2) destinoZ = 0.2;
            if (destinoZ > arcoZMax) destinoZ = arcoZMax;

            double xRel = (destinoY - arcoYMin) / (arcoYMax - arcoYMin);
            double yRel = 1.0 - (destinoZ - arcoZMin) / (arcoZMax - arcoZMin);

            double xCanvas = paddingX + xRel * drawWidth;
            double yCanvas = paddingY + yRel * drawHeight;

            Color color = switch (resultado) {
                case "gol" -> Color.LIMEGREEN;
                case "atajado" -> Color.GOLD;
                case "poste" -> Color.GRAY;
                default -> Color.DARKGRAY;
            };

            gc.setFill(color);
            gc.fillOval(xCanvas - 4, yCanvas - 4, 8, 8);
        }
    }
}
