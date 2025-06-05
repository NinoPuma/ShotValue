package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.EquiposApiClient;
import com.shotvalue.analizador_xgot.api.JugadorApiClient;
import com.shotvalue.analizador_xgot.api.TiroApiClient;
import com.shotvalue.analizador_xgot.model.Equipo;
import com.shotvalue.analizador_xgot.model.Jugador;
import com.shotvalue.analizador_xgot.model.Tiro;
import com.shotvalue.analizador_xgot.view.ViewLifecycle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Controlador para la vista “Registrar nuevo tiro”.
 * – Incluye combo de Período y TextField Minuto con validación dinámica.
 * – Se mantiene la funcionalidad de dibujar el punto de tiro y destino.
 */
public class RegistrarTiroController implements ViewLifecycle {

    // ─── Controles principales ──────────────────────────────────────────────────
    @FXML private ComboBox<Equipo> equipoBox;
    @FXML private ComboBox<Jugador> jugadorBox;

    @FXML private ComboBox<String> periodBox;
    @FXML private TextField       minuteField;

    @FXML private ComboBox<String> thirdBox, laneBox;
    @FXML private ComboBox<String> areaBox, situationBox, bodyPartBox, preActionBox, resultBox;

    @FXML private ImageView fieldMap;
    @FXML private Canvas    canvasTiros;
    @FXML private ImageView goalView;
    @FXML private Canvas    canvasArco;

    @FXML private TextField angleXFieldCampo;
    @FXML private TextField angleYFieldCampo;
    @FXML private TextField angleXFieldArco;
    @FXML private TextField angleYFieldArco;

    @FXML private Button guardarBtn;

    // ─── Rango dinámico del minuto ──────────────────────────────────────────────
    private int minAllowed = 0;
    private int maxAllowed = 120;

    private final Tiro ultimoFormulario = new Tiro();

    // ────────────────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        cargarEquipos();
        configurarPeriodos();
        configurarMinuteField();
        configurarCombosEstaticos();
        configurarCanvas();
        guardarBtn.setOnAction(e -> guardarTiro());
    }

    /* **********************************************************************
     *   CARGA DE EQUIPOS – cuando se selecciona un equipo se cargan jugadores
     * ******************************************************************* */
    private void cargarEquipos() {
        EquiposApiClient.getEquiposAsync()
                .thenAccept(this::llenarComboEquipos)
                .exceptionally(ex -> {
                    Platform.runLater(() ->
                            alerta("Error",
                                    "No se pudieron cargar equipos:\n" + ex.getMessage(),
                                    Alert.AlertType.ERROR));
                    return null;
                });

        equipoBox.setOnAction(e -> {
            Equipo seleccionado = equipoBox.getValue();
            if (seleccionado != null) cargarJugadoresPorEquipo(seleccionado.getTeamId());
        });
    }

    private void llenarComboEquipos(List<Equipo> lista) {
        Platform.runLater(() -> {
            equipoBox.getItems().setAll(lista);
            equipoBox.setConverter(new StringConverter<>() {
                @Override public String toString(Equipo t) { return t == null ? "" : t.getName(); }
                @Override public Equipo fromString(String s) { return null; }
            });
        });
    }

    /* **********************************************************************
     *   JUGADORES POR EQUIPO
     * ******************************************************************* */
    private void cargarJugadoresPorEquipo(int teamId) {
        CompletableFuture.supplyAsync(() -> {
                    try { return JugadorApiClient.getJugadoresPorEquipo(teamId); }
                    catch (Exception ex) { throw new RuntimeException(ex); }
                })
                .thenAccept(this::llenarComboJugadores)
                .exceptionally(ex -> {
                    Platform.runLater(() ->
                            alerta("Error",
                                    "No se pudieron cargar jugadores:\n" + ex.getCause().getMessage(),
                                    Alert.AlertType.ERROR));
                    return null;
                });
    }

    private void llenarComboJugadores(List<Jugador> lista) {
        Platform.runLater(() -> {
            jugadorBox.getItems().setAll(lista);
            jugadorBox.setConverter(new StringConverter<>() {
                @Override public String toString(Jugador j) { return j == null ? "" : j.getPlayerName(); }
                @Override public Jugador fromString(String s) { return null; }
            });
        });
    }

    /* **********************************************************************
     *   PERÍODOS Y VALIDACIÓN DEL MINUTO
     * ******************************************************************* */
    private void configurarPeriodos() {
        periodBox.getItems().setAll(
                "Todos los períodos",
                "1° Tiempo",
                "2° Tiempo",
                "ET - 1° Tiempo",
                "ET - 2° Tiempo",
                "Penales"
        );
        periodBox.setValue("Todos los períodos");
        periodBox.valueProperty().addListener((o, ov, nv) -> updateMinuteRange());
        updateMinuteRange();
    }

    private void configurarMinuteField() {
        TextFormatter<Integer> tf = new TextFormatter<>(
                new IntegerStringConverter(),
                null,
                change -> {
                    if (!change.getControlNewText().matches("\\d{0,3}")) return null;   // solo dígitos
                    if (change.getControlNewText().isEmpty()) return change;           // permitir borrar
                    int v = Integer.parseInt(change.getControlNewText());
                    return (v >= minAllowed && v <= maxAllowed) ? change : null;       // rango válido
                });
        minuteField.setTextFormatter(tf);
    }

    private void updateMinuteRange() {
        switch (periodBox.getValue()) {
            case "1° Tiempo"      -> { minAllowed = 0;   maxAllowed = 45; }
            case "2° Tiempo"      -> { minAllowed = 45;  maxAllowed = 90; }
            case "ET - 1° Tiempo" -> { minAllowed = 90;  maxAllowed = 105; }
            case "ET - 2° Tiempo" -> { minAllowed = 105; maxAllowed = 120; }
            case "Penales"        -> { minAllowed = 0;   maxAllowed = 0; }
            default               -> { minAllowed = 0;   maxAllowed = 120; }
        }
        minuteField.setPromptText(minAllowed == maxAllowed
                ? String.valueOf(minAllowed)
                : minAllowed + "-" + maxAllowed);

        if (!minuteField.getText().isBlank()) {
            int v = Integer.parseInt(minuteField.getText());
            if (v < minAllowed || v > maxAllowed) minuteField.clear();
        }
    }

    /* **********************************************************************
     *   COMBOS ESTÁTICOS (tercio, carril, etc.)
     * ******************************************************************* */
    private void configurarCombosEstaticos() {
        thirdBox.getItems().setAll("Todos", "Defensivo", "Medio", "Ofensivo");
        thirdBox.setValue("Todos");

        laneBox.getItems().setAll("Todos", "Izquierdo", "Central", "Derecho");
        laneBox.setValue("Todos");

        areaBox.getItems().setAll("Cualquier zona", "Área chica", "Área grande", "Fuera del área");
        areaBox.setValue("Cualquier zona");

        situationBox.getItems().setAll("Cualquier situación", "Juego abierto", "Balón parado", "Contraataque");
        situationBox.setValue("Cualquier situación");

        bodyPartBox.getItems().setAll("Cualquier parte", "Pie izquierdo", "Pie derecho", "Cabeza", "Otro");
        bodyPartBox.setValue("Cualquier parte");

        preActionBox.getItems().setAll("Todas las acciones", "Pase", "Regate", "Rebote", "Centro");
        preActionBox.setValue("Todas las acciones");

        resultBox.getItems().setAll("Todos los resultados", "Gol", "Atajado", "Fuera", "Bloqueado", "Poste");
        resultBox.setValue("Todos los resultados");
    }

    /* **********************************************************************
     *   CANVAS: dibujo del tiro y del arco
     * ******************************************************************* */
    private void configurarCanvas() {
        canvasTiros.setOnMouseClicked(e -> {
            double cx = e.getX(), cy = e.getY();
            ultimoFormulario.setX(cx);
            ultimoFormulario.setY(cy);
            dibujarPunto(canvasTiros, cx, cy, Color.YELLOW);

            double dx = cx - canvasTiros.getWidth() / 2.0;
            double dy = canvasTiros.getHeight() / 2.0 - cy;
            angleXFieldCampo.setText(String.format("%.1f", dx));
            angleYFieldCampo.setText(String.format("%.1f", dy));
        });

        canvasArco.setOnMouseClicked(e -> {
            double gx = e.getX(), gy = e.getY();
            ultimoFormulario.setDestinoX(gx);
            ultimoFormulario.setDestinoY(gy);
            dibujarPunto(canvasArco, gx, gy, Color.ORANGE);

            double dx2 = gx - canvasArco.getWidth() / 2.0;
            double dy2 = canvasArco.getHeight() / 2.0 - gy;
            angleXFieldArco.setText(String.format("%.1f", dx2));
            angleYFieldArco.setText(String.format("%.1f", dy2));
        });
    }

    private void dibujarPunto(Canvas canvas, double x, double y, Color color) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gc.setFill(color);
        gc.fillOval(x - 4, y - 4, 8, 8);
    }

    /* **********************************************************************
     *   GUARDAR EL TIRO
     * ******************************************************************* */
    /* ******************************************************************
     *  GUARDAR EL TIRO
     * ******************************************************************/
    private void guardarTiro() {
        try {
            Equipo equipoSel   = equipoBox.getValue();
            Jugador jugadorSel = jugadorBox.getValue();

            if (equipoSel == null)   { alerta("Falta Equipo",   "Selecciona un equipo",   Alert.AlertType.WARNING); return; }
            if (jugadorSel == null)  { alerta("Falta Jugador",  "Selecciona un jugador",  Alert.AlertType.WARNING); return; }
            if (minuteField.getText().isBlank()) {
                alerta("Falta Minuto", "Ingresa el minuto del tiro", Alert.AlertType.WARNING); return;
            }

            /* ─── 1) Construimos el DTO Tiro ─────────────────────────────── */
            Tiro tiro = new Tiro();

            tiro.setEquipoId(equipoSel.getTeamId());
            tiro.setEquipoNombre(equipoSel.getName());
            tiro.setJugadorId(jugadorSel.getPlayerId());
            tiro.setJugadorNombre(jugadorSel.getPlayerName());

            // 1.a  Período → número 1-5
            int periodNumber = switch (periodBox.getValue()) {
                case "1° Tiempo"      -> 1;
                case "2° Tiempo"      -> 2;
                case "ET - 1° Tiempo" -> 3;
                case "ET - 2° Tiempo" -> 4;
                case "Penales"        -> 5;
                default               -> 0;      // “Todos los períodos”
            };
            tiro.setPeriod(periodNumber);

            // 1.b  Minuto
            tiro.setMinuto(Integer.parseInt(minuteField.getText()));

            // 1.c  Demás combos
            tiro.setThird(thirdBox.getValue());
            tiro.setLane(laneBox.getValue());
            tiro.setArea(areaBox.getValue());
            tiro.setSituation(situationBox.getValue());
            tiro.setBodyPart(bodyPartBox.getValue());
            tiro.setPreAction(preActionBox.getValue());
            tiro.setResult(resultBox.getValue());

            // 1.d  Coordenadas de disparo / destino
            tiro.setX(ultimoFormulario.getX());
            tiro.setY(ultimoFormulario.getY());
            tiro.setDestinoX(ultimoFormulario.getDestinoX());
            tiro.setDestinoY(ultimoFormulario.getDestinoY());
            tiro.setDestinoZ(ultimoFormulario.getDestinoZ());   // por si capturas altura

            // 1.e  No enviamos xg/xgot: los calcula el back
            tiro.setXg(0);      // opcional; el back ignorará 0
            tiro.setXgot(0);    // opcional; el back lo sobrescribe

            /* ─── 2) Llamamos al API (el back calcula xGOT) ──────────────── */
            TiroApiClient.saveTiroAsync(tiro)
                    .thenAccept(saved -> Platform.runLater(() -> {
                        alerta("Éxito",
                                "Tiro guardado con ID: " + saved.getId() +
                                        "\nxGOT calculado: " + String.format("%.3f", saved.getXgot()),
                                Alert.AlertType.INFORMATION);
                        limpiarFormulario();
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() ->
                                alerta("Error al guardar", ex.getMessage(), Alert.AlertType.ERROR));
                        return null;
                    });

        } catch (Exception ex) {
            alerta("Error de validación", "Revisa los campos:\n" + ex.getMessage(),
                    Alert.AlertType.WARNING);
        }
    }


    /* **********************************************************************
     *   LIMPIAR EL FORMULARIO
     * ******************************************************************* */
    private void limpiarFormulario() {
        periodBox.setValue("Todos los períodos");
        minuteField.clear();
        updateMinuteRange();

        thirdBox.setValue("Todos");
        laneBox.setValue("Todos");
        areaBox.setValue("Cualquier zona");
        situationBox.setValue("Cualquier situación");
        bodyPartBox.setValue("Cualquier parte");
        preActionBox.setValue("Todas las acciones");
        resultBox.setValue("Todos los resultados");

        canvasTiros.getGraphicsContext2D().clearRect(0,0,canvasTiros.getWidth(),canvasTiros.getHeight());
        canvasArco.getGraphicsContext2D().clearRect(0,0,canvasArco.getWidth(),canvasArco.getHeight());

        angleXFieldCampo.clear(); angleYFieldCampo.clear();
        angleXFieldArco.clear();  angleYFieldArco.clear();
    }

    /* **********************************************************************
     *   UTILIDADES
     * ******************************************************************* */
    private void alerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Window owner = equipoBox.getScene().getWindow();
        Alert alert = new Alert(tipo, mensaje, ButtonType.OK);
        alert.setTitle(titulo);
        alert.initOwner(owner);
        alert.initModality(Modality.WINDOW_MODAL);
        alert.showAndWait();
    }

    /* **********************************************************************
     *   MÉTODOS DEL CICLO DE VIDA DE LA VISTA
     * ******************************************************************* */
    @Override
    public void onShow() {
        // restaurar datos del último formulario (navegación interna)
        if (ultimoFormulario.getEquipoId() != 0) {
            equipoBox.getItems().stream()
                    .filter(e -> e.getTeamId() == ultimoFormulario.getEquipoId())
                    .findFirst()
                    .ifPresent(equipoBox::setValue);
            cargarJugadoresPorEquipo(ultimoFormulario.getEquipoId());
            Platform.runLater(() -> jugadorBox.getItems().stream()
                    .filter(j -> j.getPlayerId() == ultimoFormulario.getJugadorId())
                    .findFirst()
                    .ifPresent(jugadorBox::setValue));
        }

        periodBox.setValue(ultimoFormulario.getPeriod() == null
                ? "Todos los períodos" : ultimoFormulario.getPeriod());
        minuteField.setText(ultimoFormulario.getMinuto() == 0
                ? "" : String.valueOf(ultimoFormulario.getMinuto()));
        updateMinuteRange();

        areaBox.setValue(ultimoFormulario.getArea());
        bodyPartBox.setValue(ultimoFormulario.getBodyPart());
        preActionBox.setValue(ultimoFormulario.getPreAction());
        resultBox.setValue(ultimoFormulario.getResult());

        if (ultimoFormulario.getX() != 0 && ultimoFormulario.getY() != 0) {
            dibujarPunto(canvasTiros, ultimoFormulario.getX(), ultimoFormulario.getY(), Color.YELLOW);
        }
        if (ultimoFormulario.getDestinoX() != 0 && ultimoFormulario.getDestinoY() != 0) {
            dibujarPunto(canvasArco, ultimoFormulario.getDestinoX(), ultimoFormulario.getDestinoY(), Color.ORANGE);
        }
    }

    @Override
    public void onHide() {
        // guardar valores escritos temporalmente
        Equipo eq = equipoBox.getValue();
        if (eq != null) { ultimoFormulario.setEquipoId(eq.getTeamId()); ultimoFormulario.setEquipoNombre(eq.getName()); }
        Jugador ju = jugadorBox.getValue();
        if (ju != null) { ultimoFormulario.setJugadorId(ju.getPlayerId()); ultimoFormulario.setJugadorNombre(ju.getPlayerName()); }

        if (!minuteField.getText().isBlank())
            ultimoFormulario.setMinuto(Integer.parseInt(minuteField.getText()));

        ultimoFormulario.setPeriod(periodBox.getValue());
        ultimoFormulario.setArea(areaBox.getValue());
        ultimoFormulario.setBodyPart(bodyPartBox.getValue());
        ultimoFormulario.setPreAction(preActionBox.getValue());
        ultimoFormulario.setResult(resultBox.getValue());
    }
}
