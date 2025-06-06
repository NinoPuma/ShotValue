package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.EquiposApiClient;
import com.shotvalue.analizador_xgot.api.JugadorApiClient;
import com.shotvalue.analizador_xgot.api.TiroApiClient;
import com.shotvalue.analizador_xgot.model.*;
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
 * Controlador de la vista “Registrar nuevo tiro”.
 * <ul>
 *   <li>Muestra combos y canvas para capturar un disparo.</li>
 *   <li>Convierte las coordenadas del canvas a metros StatsBomb
 *       para que el back calcule xG/xGOT con unidades correctas.</li>
 *   <li>Gestiona validación, persistencia temporal y limpieza de la UI.</li>
 * </ul>
 */
public class RegistrarTiroController implements ViewLifecycle {

    /* ─────────────── Ajusta a TU maqueta ───────────────
       Valores por defecto para un PNG de 1056×680 px
       y una imagen del arco de 260×90 px (poste-poste, césped-larguero). */
    private static final double PITCH_X0 = 0;      // pixel X del borde izq. del césped
    private static final double PITCH_Y0 = 0;      // pixel Y del borde sup. del césped
    private static final double PITCH_W  = 1056;   // ancho  del área de césped (px)
    private static final double PITCH_H  = 680;    // alto   del área de césped (px)

    private static final double GOAL_W_PX = 260;   // px entre postes en la imagen del arco
    private static final double GOAL_H_PX =  90;   // px césped-larguero

    /* ──────────────── FXML FIELDS ──────────────── */
    @FXML private ComboBox<Equipo>  equipoBox;
    @FXML private ComboBox<Jugador> jugadorBox;
    @FXML private ComboBox<String>  periodBox;
    @FXML private TextField         minuteField;
    @FXML private ComboBox<String>  thirdBox, laneBox, areaBox,
            situationBox, bodyPartBox,
            preActionBox, resultBox;
    @FXML private ImageView fieldMap, goalView;
    @FXML private Canvas    canvasTiros, canvasArco;
    @FXML private TextField angleXFieldCampo, angleYFieldCampo,
            angleXFieldArco,  angleYFieldArco;
    @FXML private Button    guardarBtn;

    /* ────────────── Estado interno ────────────── */
    private int minAllowed = 0, maxAllowed = 120;   // rango dinámico del minuto
    private final Tiro ultimo = new Tiro();         // se reaprovecha al navegar

    /* ═════════════════════════════════════════════
     *  0. INIT
     * ═══════════════════════════════════════════ */
    @FXML
    public void initialize() {
        cargarEquipos();
        configurarPeriodos();
        configurarMinuteField();
        configurarCombosEstaticos();
        configurarCanvas();
        guardarBtn.setOnAction(e -> guardarTiro());
    }

    /* ═════════════════════════════════════════════
     *  1. EQUIPOS / JUGADORES
     * ═══════════════════════════════════════════ */
    private void cargarEquipos() {
        EquiposApiClient.getEquiposAsync()
                .thenAccept(this::llenarComboEquipos)
                .exceptionally(ex -> { mostrarError("No se pudieron cargar equipos", ex); return null; });

        equipoBox.setOnAction(e -> {
            Equipo sel = equipoBox.getValue();
            if (sel != null) cargarJugadoresPorEquipo(sel.getTeamId());
        });
    }

    private void llenarComboEquipos(List<Equipo> lista) {
        Platform.runLater(() -> {
            equipoBox.getItems().setAll(lista);
            equipoBox.setConverter(new StringConverter<>() {
                public String toString(Equipo t)  { return t == null ? "" : t.getName(); }
                public Equipo fromString(String s){ return null; }
            });
        });
    }

    private void cargarJugadoresPorEquipo(int teamId) {
        CompletableFuture.supplyAsync(() -> {
                    try { return JugadorApiClient.getJugadoresPorEquipo(teamId); }
                    catch (Exception ex) { throw new RuntimeException(ex); }
                })
                .thenAccept(this::llenarComboJugadores)
                .exceptionally(ex -> { mostrarError("No se pudieron cargar jugadores", ex); return null; });
    }

    private void llenarComboJugadores(List<Jugador> lista) {
        Platform.runLater(() -> {
            jugadorBox.getItems().setAll(lista);
            jugadorBox.setConverter(new StringConverter<>() {
                public String  toString(Jugador j){ return j == null ? "" : j.getPlayerName(); }
                public Jugador fromString(String s){ return null; }
            });
        });
    }

    /* ═════════════════════════════════════════════
     *  2. PERÍODO / MINUTO
     * ═══════════════════════════════════════════ */
    private void configurarPeriodos() {
        periodBox.getItems().setAll("Todos los períodos","1° Tiempo","2° Tiempo",
                "ET - 1° Tiempo","ET - 2° Tiempo","Penales");
        periodBox.setValue("Todos los períodos");
        periodBox.valueProperty().addListener((o,ov,nv) -> updateMinuteRange());
        updateMinuteRange();
    }

    private void configurarMinuteField() {
        minuteField.setTextFormatter(new TextFormatter<>(new IntegerStringConverter(),
                null,
                c -> c.getControlNewText().matches("\\d{0,3}") ? c : null));
        minuteField.focusedProperty().addListener((obs,was,now) -> { if(!now) validarMinuto(); });
    }

    private boolean validarMinuto() {
        String txt = minuteField.getText().trim();
        if (txt.isEmpty()) return false;
        int v = Integer.parseInt(txt);
        if (v < minAllowed || v > maxAllowed) {
            alerta("Minuto inválido",
                    "Debe estar entre "+minAllowed+" y "+maxAllowed,
                    Alert.AlertType.WARNING);
            minuteField.requestFocus();
            return false;
        }
        return true;
    }

    private void updateMinuteRange() {
        switch (periodBox.getValue()) {
            case "1° Tiempo"      -> { minAllowed = 0;   maxAllowed = 45;  }
            case "2° Tiempo"      -> { minAllowed = 45;  maxAllowed = 90;  }
            case "ET - 1° Tiempo" -> { minAllowed = 90;  maxAllowed = 105; }
            case "ET - 2° Tiempo" -> { minAllowed = 105; maxAllowed = 120; }
            case "Penales"        -> { minAllowed = 0;   maxAllowed = 0;   }
            default               -> { minAllowed = 0;   maxAllowed = 120; }
        }
        boolean penales = "Penales".equals(periodBox.getValue());
        minuteField.setDisable(penales);
        minuteField.setPromptText(
                minAllowed == maxAllowed ? String.valueOf(minAllowed)
                        : minAllowed + "-" + maxAllowed);
    }

    /* ═════════════════════════════════════════════
     *  3. COMBOS ESTÁTICOS
     * ═══════════════════════════════════════════ */
    private void configurarCombosEstaticos() {
        thirdBox.getItems().setAll("Todos","Defensivo","Medio","Ofensivo");                    thirdBox.setValue("Todos");
        laneBox .getItems().setAll("Todos","Izquierdo","Central","Derecho");                   laneBox .setValue("Todos");
        areaBox .getItems().setAll("Cualquier zona","Área chica","Área grande","Fuera del área"); areaBox.setValue("Cualquier zona");
        situationBox.getItems().setAll("Cualquier situación","Juego abierto","Balón parado","Contraataque"); situationBox.setValue("Cualquier situación");
        bodyPartBox.getItems().setAll("Cualquier parte","Pie izquierdo","Pie derecho","Cabeza","Otro");      bodyPartBox.setValue("Cualquier parte");
        preActionBox.getItems().setAll("Todas las acciones","Pase","Regate","Rebote","Centro");             preActionBox.setValue("Todas las acciones");
        resultBox   .getItems().setAll("Todos los resultados","Gol","Atajado","Fuera","Bloqueado","Poste"); resultBox.setValue("Todos los resultados");
    }

    /* ═════════════════════════════════════════════
     *  4. CANVAS – Conversión píxel → metros StatsBomb
     * ═══════════════════════════════════════════ */
    private void configurarCanvas() {

        /* —— Campo de juego completo (visto desde arriba) —— */
        canvasTiros.setOnMouseClicked(e -> {
            double px = e.getX(), py = e.getY();

            /* 1) Transformación a coordenadas StatsBomb (120 × 80 m) */
            double sbX = (px - PITCH_X0) / PITCH_W * 120.0;   // 0 (propia portería) → 120 (rival)
            double sbY = (py - PITCH_Y0) / PITCH_H *  80.0;   // 0 = banda superior

            ultimo.setX(sbX);
            ultimo.setY(sbY);

            /* 2) Dibujo del punto y feedback de ángulo en m (relativo al centro) */
            dibujarPunto(canvasTiros, px, py, Color.YELLOW);
            angleXFieldCampo.setText(String.format("%.1f", sbX - 60.0)); // centrado en 60
            angleYFieldCampo.setText(String.format("%.1f", 40.0 - sbY)); // centrado en 40
        });

        /* —— Vista frontal del arco —— */
        canvasArco.setOnMouseClicked(e -> {
            double px = e.getX(), py = e.getY();

            /* lateral -3.66 ↔ +3.66 m, altura 0 ↔ 2.44 m */
            double lateral = (px - GOAL_W_PX / 2.0) / (GOAL_W_PX / 2.0) * 3.66;
            double altura  = (GOAL_H_PX - py) / GOAL_H_PX * 2.44;
            altura = Math.max(0, Math.min(altura, 2.44));

            ultimo.setDestinoX(120.0);                 // siempre en línea de gol rival
            ultimo.setDestinoY(40.0 + lateral);
            ultimo.setDestinoZ(altura);

            dibujarPunto(canvasArco, px, py, Color.ORANGE);
            angleXFieldArco.setText(String.format("%.2f", lateral));
            angleYFieldArco.setText(String.format("%.2f", altura));
        });
    }

    private void dibujarPunto(Canvas c, double x, double y, Color col) {
        GraphicsContext g = c.getGraphicsContext2D();
        g.clearRect(0, 0, c.getWidth(), c.getHeight());
        g.setFill(col);
        g.fillOval(x - 4, y - 4, 8, 8);
    }

    /* ═════════════════════════════════════════════
     *  5. GUARDAR TIRO – POST al backend
     * ═══════════════════════════════════════════ */
    private void guardarTiro() {
        if (!validarMinuto()) return;

        try {
            Equipo  eqSel = equipoBox.getValue();
            Jugador juSel = jugadorBox.getValue();
            if (eqSel == null) { alerta("Falta Equipo","Selecciona un equipo",Alert.AlertType.WARNING); return; }
            if (juSel == null) { alerta("Falta Jugador","Selecciona un jugador",Alert.AlertType.WARNING); return; }

            Tiro t = new Tiro();
            t.setEquipoId(eqSel.getTeamId());      t.setEquipoNombre(eqSel.getName());
            t.setJugadorId(juSel.getPlayerId());   t.setJugadorNombre(juSel.getPlayerName());

            t.setPeriod(labelToPeriod(periodBox.getValue()));
            t.setMinuto(Integer.parseInt(minuteField.getText()));

            t.setThird(thirdBox.getValue());   t.setLane(laneBox.getValue());
            t.setArea(areaBox.getValue());     t.setSituation(situationBox.getValue());
            t.setBodyPart(bodyPartBox.getValue());
            t.setPreAction(preActionBox.getValue()); t.setResult(resultBox.getValue());

            t.setX(ultimo.getX());             t.setY(ultimo.getY());
            t.setDestinoX(ultimo.getDestinoX());
            t.setDestinoY(ultimo.getDestinoY());
            t.setDestinoZ(ultimo.getDestinoZ());

            /* POST – back calcula xG/xGOT */
            TiroApiClient.saveTiroAsync(t)
                    .thenAccept(saved -> Platform.runLater(() -> {
                        alerta("Éxito",
                                "Tiro guardado con ID: "+saved.getId()+
                                        "\nxGOT: "+String.format("%.3f", saved.getXgot()),
                                Alert.AlertType.INFORMATION);
                        limpiarFormulario();
                    }))
                    .exceptionally(ex -> { mostrarError("Error al guardar", ex); return null; });

        } catch (Exception ex) {
            alerta("Error de validación", ex.getMessage(), Alert.AlertType.WARNING);
        }
    }

    /* ═════════════════════════════════════════════
     *  6. LIMPIAR FORMULARIO
     * ═══════════════════════════════════════════ */
    private void limpiarFormulario() {
        periodBox.setValue("Todos los períodos");
        minuteField.clear();
        updateMinuteRange();

        thirdBox.setValue("Todos"); laneBox.setValue("Todos");
        areaBox.setValue("Cualquier zona");
        situationBox.setValue("Cualquier situación");
        bodyPartBox.setValue("Cualquier parte");
        preActionBox.setValue("Todas las acciones");
        resultBox.setValue("Todos los resultados");

        canvasTiros.getGraphicsContext2D().clearRect(0,0,canvasTiros.getWidth(),canvasTiros.getHeight());
        canvasArco .getGraphicsContext2D().clearRect(0,0,canvasArco .getWidth(),canvasArco .getHeight());

        angleXFieldCampo.clear();  angleYFieldCampo.clear();
        angleXFieldArco .clear();  angleYFieldArco .clear();
    }

    /* ═════════════════════════════════════════════
     *  7. UTILIDADES
     * ═══════════════════════════════════════════ */
    private int labelToPeriod(String l){ return switch(l){
        case "1° Tiempo"->1; case "2° Tiempo"->2;
        case "ET - 1° Tiempo"->3; case "ET - 2° Tiempo"->4;
        case "Penales"->5; default->0; }; }

    private String periodToLabel(int p){ return switch(p){
        case 1->"1° Tiempo"; case 2->"2° Tiempo";
        case 3->"ET - 1° Tiempo"; case 4->"ET - 2° Tiempo";
        case 5->"Penales"; default->"Todos los períodos"; }; }

    private void alerta(String t, String m, Alert.AlertType tp) {
        Window w = equipoBox.getScene().getWindow();
        Alert a = new Alert(tp, m, ButtonType.OK);
        a.setTitle(t); a.initOwner(w); a.initModality(Modality.WINDOW_MODAL);
        a.showAndWait();
    }

    private void mostrarError(String msg, Throwable ex) {
        Platform.runLater(() ->
                alerta("Error", msg + "\n" + ex.getMessage(), Alert.AlertType.ERROR));
    }

    /* ═════════════════════════════════════════════
     *  8. CICLO DE VIDA (opcional)
     * ═══════════════════════════════════════════ */
    @Override public void onShow() { }
    @Override public void onHide() { }
}
