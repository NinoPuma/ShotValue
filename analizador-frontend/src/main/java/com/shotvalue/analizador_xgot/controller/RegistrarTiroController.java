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
 * Ahora convierte las coordenadas de píxel a metros StatsBomb (120×80 m)
 * antes de enviarlas al backend para que calcule xGOT correctamente.
 */
public class RegistrarTiroController implements ViewLifecycle {

    // ─── Controles principales ──────────────────────────────────────────
    @FXML
    private ComboBox<Equipo> equipoBox;
    @FXML
    private ComboBox<Jugador> jugadorBox;
    @FXML
    private ComboBox<String> periodBox;
    @FXML
    private TextField minuteField;
    @FXML
    private ComboBox<String> situationBox,
            bodyPartBox, preActionBox, resultBox;
    @FXML
    private ImageView fieldMap, goalView;
    @FXML
    private Canvas canvasTiros, canvasArco;
    @FXML
    private TextField angleXFieldCampo, angleYFieldCampo,
            angleXFieldArco, angleYFieldArco;
    @FXML
    private Button guardarBtn;

    // ─── Estado dinámico ────────────────────────────────────────────────
    private int minAllowed = 0, maxAllowed = 120;
    private final Tiro ultimoFormulario = new Tiro();     // “borrador” persistente

    // ────────────────────────────────────────────────────────────────────
    @FXML
    public void initialize() {
        cargarEquipos();
        configurarPeriodos();
        configurarMinuteField();
        configurarCombosEstaticos();
        configurarCanvas();
        guardarBtn.setOnAction(e -> guardarTiro());
    }

    /* *******************************************************************
     *   1. EQUIPOS / JUGADORES
     * ******************************************************************/
    private void cargarEquipos() {
        EquiposApiClient.getEquiposAsync()
                .thenAccept(this::llenarComboEquipos)
                .exceptionally(ex -> {
                    mostrarError("No se pudieron cargar equipos", ex);
                    return null;
                });

        equipoBox.setOnAction(e -> {
            Equipo sel = equipoBox.getValue();
            if (sel != null) cargarJugadoresPorEquipo(sel.getTeamId());
        });
    }

    private void llenarComboEquipos(List<Equipo> lista) {
        Platform.runLater(() -> {
            equipoBox.getItems().setAll(lista);
            equipoBox.setConverter(new StringConverter<>() {
                public String toString(Equipo t) {
                    return t == null ? "" : t.getName();
                }

                public Equipo fromString(String s) {
                    return null;
                }
            });
        });
    }

    private void cargarJugadoresPorEquipo(int teamId) {
        CompletableFuture.supplyAsync(() -> {
                    try {
                        return JugadorApiClient.getJugadoresPorEquipo(teamId);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }).thenAccept(this::llenarComboJugadores)
                .exceptionally(ex -> {
                    mostrarError("No se pudieron cargar jugadores", ex);
                    return null;
                });
    }

    private void llenarComboJugadores(List<Jugador> lista) {
        Platform.runLater(() -> {
            jugadorBox.getItems().setAll(lista);
            jugadorBox.setConverter(new StringConverter<>() {
                public String toString(Jugador j) {
                    return j == null ? "" : j.getPlayerName();
                }

                public Jugador fromString(String s) {
                    return null;
                }
            });
        });
    }

    /* *******************************************************************
     *   2. PERÍODO  &  MINUTO
     * ******************************************************************/
    private void configurarPeriodos() {
        periodBox.getItems().setAll(
                "Todos los períodos", "1° Tiempo", "2° Tiempo",
                "ET - 1° Tiempo", "ET - 2° Tiempo", "Penales"
        );
        periodBox.setValue("Todos los períodos");
        periodBox.valueProperty().addListener((o, ov, nv) -> updateMinuteRange());
        updateMinuteRange();
    }

    private void configurarMinuteField() {
        TextFormatter<Integer> tf = new TextFormatter<>(
                new IntegerStringConverter(), null,
                c -> c.getControlNewText().matches("\\d{0,3}") ? c : null);
        minuteField.setTextFormatter(tf);
    }

    private void updateMinuteRange() {
        switch (periodBox.getValue()) {
            case "1° Tiempo" -> {
                minAllowed = 0;
                maxAllowed = 45;
            }
            case "2° Tiempo" -> {
                minAllowed = 45;
                maxAllowed = 90;
            }
            case "ET - 1° Tiempo" -> {
                minAllowed = 90;
                maxAllowed = 105;
            }
            case "ET - 2° Tiempo" -> {
                minAllowed = 105;
                maxAllowed = 120;
            }
            case "Penales" -> {
                minAllowed = 0;
                maxAllowed = 0;
            }
            default -> {
                minAllowed = 0;
                maxAllowed = 120;
            }
        }
        minuteField.setPromptText(minAllowed == maxAllowed
                ? String.valueOf(minAllowed)
                : minAllowed + "-" + maxAllowed);

        if (!minuteField.getText().isBlank()) {
            int v = Integer.parseInt(minuteField.getText());
            if (v < minAllowed || v > maxAllowed) minuteField.clear();
        }
    }

    private boolean validarMinuto() {
        if (minuteField.getText().isBlank()) return false;
        int v = Integer.parseInt(minuteField.getText());
        return v >= minAllowed && v <= maxAllowed;
    }

    /* *******************************************************************
     *   3. COMBOS ESTÁTICOS
     * ******************************************************************/
    private void configurarCombosEstaticos() {
        situationBox.getItems().setAll("Cualquier situación", "Juego abierto", "Balón parado");
        situationBox.setValue("Cualquier situación");
        bodyPartBox.getItems().setAll("Cualquier parte", "Pie izquierdo", "Pie derecho", "Cabeza", "Otro");
        bodyPartBox.setValue("Cualquier parte");
        preActionBox.getItems().setAll("Todas las acciones", "Pase", "Regate", "Rebote", "Centro", "Penal");
        preActionBox.setValue("Todas las acciones");
        resultBox.getItems().setAll("Todos los resultados", "Gol", "Atajado", "Fuera", "Bloqueado", "Poste");
        resultBox.setValue("Todos los resultados");
    }

    /* *******************************************************************
     *   4. CANVAS (ahora con escalado px→StatsBomb)
     * ******************************************************************/
    private void configurarCanvas() {
        // 4.a) campo 120×80 m
        canvasTiros.setOnMouseClicked(e -> {
            double px = e.getX(), py = e.getY();
            double sbX = px / canvasTiros.getWidth() * 120.0;

            // invertimos Y para StatsBomb
            double sbY = (canvasTiros.getHeight() - py) / canvasTiros.getHeight() * 80.0;

            // Calcular tercio según X
            String tercio = (sbX <= 40) ? "Defensivo" : (sbX <= 80 ? "Medio" : "Ofensivo");

// Calcular carril según Y
            String carril = (sbY <= 26.6) ? "Izquierdo" : (sbY <= 53.3 ? "Central" : "Derecho");

// Calcular área
            String area;
            if (sbX >= 102 && sbY >= 27 && sbY <= 53) {
                area = "Área chica";
            } else if (sbX >= 90 && sbY >= 18 && sbY <= 62) {
                area = "Área grande";
            } else {
                area = "Fuera del área";
            }


            ultimoFormulario.setX(sbX);
            ultimoFormulario.setY(sbY);
            dibujarPunto(canvasTiros, px, py, Color.YELLOW);

            // ángulos visuales (m)
            angleXFieldCampo.setText(String.format("%.1f", sbX - 60.0));
            angleYFieldCampo.setText(String.format("%.1f", 40.0 - sbY));
        });

        // 4.b) arco 7.32m×3.66m centrado en y=40
        canvasArco.setOnMouseClicked(e -> {
            double gx = e.getX(), gy = e.getY();
            // lateral: de -3.66 a +3.66
            double lateral = (gx / canvasArco.getWidth() * 7.32) - 3.66;
            // altura: de 0 a 3.66
            double altura = (canvasArco.getHeight() - gy) / canvasArco.getHeight() * 3.66;

            // guardamos destino en StatsBomb
            ultimoFormulario.setDestinoX(120.0);
            ultimoFormulario.setDestinoY(40.0 + lateral);
            ultimoFormulario.setDestinoZ(altura);

            dibujarPunto(canvasArco, gx, gy, Color.ORANGE);

            angleXFieldArco.setText(String.format("%.1f", lateral));
            angleYFieldArco.setText(String.format("%.1f", altura));
        });
    }

    private void dibujarPunto(Canvas c, double x, double y, Color col) {
        GraphicsContext g = c.getGraphicsContext2D();
        g.clearRect(0, 0, c.getWidth(), c.getHeight());
        g.setFill(col);
        g.fillOval(x - 4, y - 4, 8, 8);
    }

    /* *******************************************************************
     *   5. GUARDAR TIRO
     * ******************************************************************/
    private void guardarTiro() {
        if (!validarMinuto()) {
            alerta("Falta Minuto", "Ingresa un minuto válido", Alert.AlertType.WARNING);
            return;
        }
        try {
            Equipo eq = equipoBox.getValue();
            Jugador ju = jugadorBox.getValue();
            if (eq == null) {
                alerta("Falta Equipo", "Selecciona un equipo", Alert.AlertType.WARNING);
                return;
            }
            if (ju == null) {
                alerta("Falta Jugador", "Selecciona un jugador", Alert.AlertType.WARNING);
                return;
            }

            Tiro t = new Tiro();
            t.setEquipoId(eq.getTeamId());
            t.setEquipoNombre(eq.getName());
            t.setJugadorId(ju.getPlayerId());
            t.setJugadorNombre(ju.getPlayerName());
            t.setPeriod(labelToPeriod(periodBox.getValue()));
            t.setMinuto(Integer.parseInt(minuteField.getText()));
            t.setThird(ultimoFormulario.getThird());
            t.setLane(ultimoFormulario.getLane());
            t.setArea(ultimoFormulario.getArea());
            t.setSituation(situationBox.getValue());
            t.setBodyPart(bodyPartBox.getValue());
            t.setPreAction(preActionBox.getValue());
            t.setResult(resultBox.getValue());

            // X/Y ya están en metros StatsBomb
            t.setX(ultimoFormulario.getX());
            t.setY(ultimoFormulario.getY());
            t.setDestinoX(ultimoFormulario.getDestinoX());
            t.setDestinoY(ultimoFormulario.getDestinoY());
            t.setDestinoZ(ultimoFormulario.getDestinoZ());

            TiroApiClient.saveTiroAsync(t)
                    .thenAccept(saved -> Platform.runLater(() -> {
                        alerta("Éxito",
                                "Tiro guardado con ID: " + saved.getId() +
                                        "\nxGOT: " + String.format("%.3f", saved.getXgot()),
                                Alert.AlertType.INFORMATION);
                        limpiarFormulario();
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() ->
                                alerta("Error al guardar", ex.getMessage(), Alert.AlertType.ERROR));
                        return null;
                    });

        } catch (Exception ex) {
            alerta("Error de validación", ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    /* *******************************************************************
     *   6. LIMPIAR FORMULARIO
     * ******************************************************************/
    private void limpiarFormulario() {
        periodBox.setValue("Todos los períodos");
        minuteField.clear();
        updateMinuteRange();
        situationBox.setValue("Cualquier situación");
        bodyPartBox.setValue("Cualquier parte");
        preActionBox.setValue("Todas las acciones");
        resultBox.setValue("Todos los resultados");
        canvasTiros.getGraphicsContext2D().clearRect(0, 0, canvasTiros.getWidth(), canvasTiros.getHeight());
        canvasArco.getGraphicsContext2D().clearRect(0, 0, canvasArco.getWidth(), canvasArco.getHeight());
        angleXFieldCampo.clear();
        angleYFieldCampo.clear();
        angleXFieldArco.clear();
        angleYFieldArco.clear();
    }

    /* *******************************************************************
     *   7. UTILIDADES
     * ******************************************************************/
    private int labelToPeriod(String l) {
        return switch (l) {
            case "1° Tiempo" -> 1;
            case "2° Tiempo" -> 2;
            case "ET - 1° Tiempo" -> 3;
            case "ET - 2° Tiempo" -> 4;
            case "Penales" -> 5;
            default -> 0;
        };
    }

    private String periodToLabel(int p) {
        return switch (p) {
            case 1 -> "1° Tiempo";
            case 2 -> "2° Tiempo";
            case 3 -> "ET - 1° Tiempo";
            case 4 -> "ET - 2° Tiempo";
            case 5 -> "Penales";
            default -> "Todos los períodos";
        };
    }

    private void alerta(String t, String m, Alert.AlertType tp) {
        Window w = equipoBox.getScene().getWindow();
        Alert a = new Alert(tp, m, ButtonType.OK);
        a.setTitle(t);
        a.initOwner(w);
        a.initModality(Modality.WINDOW_MODAL);
        a.showAndWait();
    }

    private void mostrarError(String msg, Throwable ex) {
        Platform.runLater(() ->
                alerta("Error", msg + "\n" + ex.getMessage(), Alert.AlertType.ERROR));
    }

    /* *******************************************************************
     *   8. CICLO DE VIDA
     * ******************************************************************/
    @Override
    public void onShow() {
        // restaurar valores del borrador
        if (ultimoFormulario.getEquipoId() != 0) {
            equipoBox.getItems().stream()
                    .filter(e -> e.getTeamId() == ultimoFormulario.getEquipoId())
                    .findFirst().ifPresent(equipoBox::setValue);
            cargarJugadoresPorEquipo(ultimoFormulario.getEquipoId());
            Platform.runLater(() -> jugadorBox.getItems().stream()
                    .filter(j -> j.getPlayerId() == ultimoFormulario.getJugadorId())
                    .findFirst().ifPresent(jugadorBox::setValue));
        }
        periodBox.setValue(periodToLabel(ultimoFormulario.getPeriod()));
        minuteField.setText(ultimoFormulario.getMinuto() == 0 ? "" : String.valueOf(ultimoFormulario.getMinuto()));
        updateMinuteRange();
    }

    @Override
    public void onHide() {
        Equipo e = equipoBox.getValue();
        if (e != null) ultimoFormulario.setEquipoId(e.getTeamId());
        Jugador j = jugadorBox.getValue();
        if (j != null) ultimoFormulario.setJugadorId(j.getPlayerId());
        if (!minuteField.getText().isBlank())
            ultimoFormulario.setMinuto(Integer.parseInt(minuteField.getText()));
        ultimoFormulario.setPeriod(labelToPeriod(periodBox.getValue()));
    }
}
