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
import org.springframework.beans.factory.annotation.Autowired;

import java.net.URL;
import java.util.*;


public class VisualizarController implements Initializable {

    @FXML
    private ComboBox<String> periodBox;
    @FXML
    private Spinner<Integer> minuteFromSpinner, minuteToSpinner;
    @FXML
    private ComboBox<String> teamSideBox;
    @FXML
    private TextField playerSearchField;
    @FXML
    private ComboBox<String> thirdBox, laneBox;
    @FXML
    private ComboBox<String> eventTypeBox, visualizationTypeBox;
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
    private Canvas canvasTiros;
    @FXML
    private Canvas canvasArco;
    @FXML
    private ImageView fieldMap;

    private JugadorApiClient jugadorApiClient = new JugadorApiClient();


    private List<Tiro> ultimoTiros = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        areaBox.getItems().addAll("Cualquier zona", "Área chica", "Área grande", "Fuera del área");
        situationBox.getItems().addAll("Cualquier situación", "Juego abierto", "Balón parado", "Contraataque");
        bodyPartBox.getItems().addAll("Cualquier parte", "Pie izquierdo", "Pie derecho", "Cabeza", "Otro");
        preActionBox.getItems().addAll("Todas las acciones", "Pase", "Regate", "Rebote", "Centro");
        resultBox.getItems().addAll("Todos los resultados", "Gol", "Atajado", "Fuera", "Bloqueado");

        minuteFromSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 0));
        minuteToSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 90));

        eventTypeBox.getItems().setAll("Tiros", "Pases", "Duelos");
        eventTypeBox.setValue("Tiros");

        visualizationTypeBox.getItems().setAll("Mapa de tiros", "Análisis de goles", "Tiros + Goles", "Mapa de calor", "Zonas xGOT");
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

        xgField.setTextFormatter(new TextFormatter<>(change -> change.getControlNewText().matches("\\d{0,3}(\\.\\d{0,2})?") ? change : null));

        canvasTiros.toFront();
        canvasTiros.setMouseTransparent(true);
        canvasArco.toFront();
        canvasArco.setMouseTransparent(true);

        applyFiltersBtn.setOnAction(event -> aplicarFiltros());

        new Thread(() -> {
            try {
                List<String> nombres = jugadorApiClient.getNombresCompletos();
                Platform.runLater(() -> TextFields.bindAutoCompletion(playerSearchField, nombres));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

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
        filtros.put("jugador", playerSearchField.getText().trim());

        new Thread(() -> {
            try {
                List<Tiro> tiros = TiroApiClient.filtrarTiros(filtros);
                Platform.runLater(() -> {
                    ultimoTiros = tiros;
                    drawTirosInternal(tiros);
                    dibujarEnArco(tiros);
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

        double escalaX = 354.4 / 120.0;
        double escalaY = 244.0 / 80.0;
        double offsetX = 73.6;
        double offsetY = 4.0;

        for (Tiro tiro : tiros) {
            if (tiro.getDestinoX() == null || tiro.getDestinoY() == null) continue;

            double x1 = offsetX + tiro.getX() * escalaX;
            double y1 = offsetY + tiro.getY() * escalaY;
            double x2 = offsetX + tiro.getDestinoX() * escalaX;
            double y2 = offsetY + tiro.getDestinoY() * escalaY;

            String resultadoRaw = tiro.getResultado();
            String resultado = resultadoRaw != null ? resultadoRaw.toLowerCase().trim() : "";

            Color color;
            switch (resultado) {
                case "goal" -> color = Color.LIMEGREEN;
                case "saved" -> color = Color.GOLD;
                case "off t" -> color = Color.CRIMSON;
                case "blocked" -> color = Color.ORANGE;
                case "post" -> color = Color.GRAY;
                default -> color = Color.WHITE;
            }


            System.out.println("Resultado raw: '" + resultadoRaw + "', normalizado: '" + resultado + "', color: " + color);

            gc.setStroke(color);
            gc.setLineWidth(2.0);
            gc.strokeLine(x1, y1, x2, y2);
            gc.setFill(color);
            gc.fillOval(x1 - 3, y1 - 3, 6, 6);
            gc.fillOval(x2 - 3, y2 - 3, 6, 6);
        }
    }

    private void dibujarEnArco(List<Tiro> tiros) {
        GraphicsContext gc = canvasArco.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasArco.getWidth(), canvasArco.getHeight());

        // 🔧 Medidas del arco real
        double arcoAncho = 7.32;
        double arcoAlto = 2.44;

        // 🔧 Datos obtenidos con los clics
        double offsetX = 119.20;
        double offsetY = 16.80;
        double escalaX = 264.0 / arcoAncho;
        double escalaY = 104.0 / arcoAlto;

        double arcoInicioX = 120 - arcoAncho / 2;
        double arcoInicioY = 40 - arcoAlto / 2;

        for (Tiro tiro : tiros) {
            if (tiro.getDestinoX() == null || tiro.getDestinoY() == null) continue;

            double xRelativo = tiro.getDestinoX() - arcoInicioX;
            double yRelativo = tiro.getDestinoY() - arcoInicioY;

            if (xRelativo < 0 || xRelativo > arcoAncho || yRelativo < 0 || yRelativo > arcoAlto)
                continue;

            double x = offsetX + xRelativo * escalaX;
            double y = offsetY + yRelativo * escalaY;

            Color color = switch (tiro.getResultado().toLowerCase()) {
                case "gol" -> Color.LIME;
                case "atajado" -> Color.DODGERBLUE;
                default -> Color.GREY;
            };

            gc.setFill(color);
            gc.fillOval(x - 4, y - 4, 8, 8);
        }
    }

}