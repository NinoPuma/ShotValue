package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.services.TiroService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class VisualizarController implements Initializable {


    @Autowired
    private TiroService tiroService;

    // FILTROS SUPERIORES
    @FXML
    private ComboBox<String> periodBox;
    @FXML
    private Spinner<Integer> minuteFromSpinner;
    @FXML
    private Spinner<Integer> minuteToSpinner;
    @FXML
    private ComboBox<String> teamSideBox;
    @FXML
    private TextField playerSearchField;
    @FXML
    private ComboBox<String> thirdBox;
    @FXML
    private ComboBox<String> laneBox;

    // OPCIONES DE EVENTO Y VISUALIZACIÓN
    @FXML
    private ComboBox<String> eventTypeBox;
    @FXML
    private ComboBox<String> visualizationTypeBox;
    @FXML
    private Button applyFiltersBtn;

    // FILTROS DEL EVENTO
    @FXML
    private ComboBox<String> areaBox;
    @FXML
    private ComboBox<String> situationBox;
    @FXML
    private ComboBox<String> bodyPartBox;
    @FXML
    private ComboBox<String> preActionBox;
    @FXML
    private ComboBox<String> resultBox;
    @FXML
    private TextField xgField;


    // IMÁGENES
    @FXML
    private ImageView fieldMap;
    @FXML
    private ImageView goalView;
    @FXML
    private Label legendLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // FILTROS DEL EVENTO
        areaBox.getItems().addAll("Cualquier zona", "Área chica", "Área grande", "Fuera del área");
        situationBox.getItems().addAll("Cualquier situación", "Juego abierto", "Balón parado", "Contraataque");
        bodyPartBox.getItems().addAll("Cualquier parte", "Pie izquierdo", "Pie derecho", "Cabeza", "Otro");
        preActionBox.getItems().addAll("Todas las acciones", "Pase", "Regate", "Rebote", "Centro");
        resultBox.getItems().addAll("Todos los resultados", "Gol", "Atajado", "Fuera", "Bloqueado");

        // MINUTOS
        SpinnerValueFactory<Integer> fromFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 0);
        SpinnerValueFactory<Integer> toFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 90);
        minuteFromSpinner.setValueFactory(fromFactory);
        minuteToSpinner.setValueFactory(toFactory);

        // OPCIONES DE EVENTO Y VISUALIZACIÓN
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

        // VALORES POR DEFECTO EN FILTROS SUPERIORES
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


        // BOTÓN APLICAR
        applyFiltersBtn.setOnAction(event -> aplicarFiltros());
    }

    private void aplicarFiltros() {
        int minutoDesde = minuteFromSpinner.getValue();
        int minutoHasta = minuteToSpinner.getValue();

        String parteCuerpo = bodyPartBox.getValue();
        String tipoJugada = preActionBox.getValue();
        String resultado = resultBox.getValue();
        String zona = areaBox.getValue();
        String xgotStr = xgField.getText().trim();

        var tiros = tiroService.filtrarTiros(
                minutoDesde, minutoHasta,
                parteCuerpo, tipoJugada,
                resultado, zona, xgotStr
        );

        System.out.println("🔎 Tiros encontrados: " + tiros.size());
        for (var t : tiros) {
            System.out.println("- " + t);
        }
    }

}
