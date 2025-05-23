package com.shotvalue.analizador_xgot.front;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class VisualizarController implements Initializable {

    // FILTROS SUPERIORES
    @FXML private ComboBox<String> periodBox;
    @FXML private Spinner<Integer> minuteFromSpinner;
    @FXML private Spinner<Integer> minuteToSpinner;
    @FXML private ComboBox<String> teamSideBox;
    @FXML private TextField playerSearchField;
    @FXML private ComboBox<String> thirdBox;
    @FXML private ComboBox<String> laneBox;

    // OPCIONES DE EVENTO Y VISUALIZACIÓN
    @FXML private ComboBox<String> eventTypeBox;
    @FXML private ComboBox<String> visualizationTypeBox;
    @FXML private Button applyFiltersBtn;

    // FILTROS DEL EVENTO
    @FXML private ComboBox<String> areaBox;
    @FXML private ComboBox<String> situationBox;
    @FXML private ComboBox<String> bodyPartBox;
    @FXML private ComboBox<String> preActionBox;
    @FXML private ComboBox<String> resultBox;
    @FXML private TextField xgField;



    // IMÁGENES
    @FXML private ImageView fieldMap;
    @FXML private ImageView goalView;
    @FXML private Label legendLabel;

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
        String periodo = periodBox.getValue();
        String localVisitante = teamSideBox.getValue();
        String jugador = playerSearchField.getText().trim();
        String tercio = thirdBox.getValue();
        String carril = laneBox.getValue();

        int minutoDesde = minuteFromSpinner.getValue();
        int minutoHasta = minuteToSpinner.getValue();

        String tipoEvento = eventTypeBox.getValue();
        String tipoVisualizacion = visualizationTypeBox.getValue();

        String area = areaBox.getValue();
        String situacion = situationBox.getValue();
        String parteCuerpo = bodyPartBox.getValue();
        String accionPrevia = preActionBox.getValue();
        String resultado = resultBox.getValue();
        String npxg = xgField.getText().trim();


        System.out.println("Filtros aplicados:");
        System.out.println("- Período: " + periodo);
        System.out.println("- Minutos: " + minutoDesde + " a " + minutoHasta);
        System.out.println("- Local/Visitante: " + localVisitante);
        System.out.println("- Jugador: " + jugador);
        System.out.println("- Tercio: " + tercio);
        System.out.println("- Carril: " + carril);
        System.out.println("- Evento: " + tipoEvento);
        System.out.println("- Visualización: " + tipoVisualizacion);
        System.out.println("- Área: " + area);
        System.out.println("- Situación: " + situacion);
        System.out.println("- Parte del cuerpo: " + parteCuerpo);
        System.out.println("- Acción previa: " + accionPrevia);
        System.out.println("- Resultado: " + resultado);
        System.out.println("- NPxG: " + npxg);
    }
}
