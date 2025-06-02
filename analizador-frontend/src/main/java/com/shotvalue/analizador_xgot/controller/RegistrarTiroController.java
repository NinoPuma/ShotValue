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

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class RegistrarTiroController implements ViewLifecycle {

    @FXML private ComboBox<Equipo> equipoBox;
    @FXML private ComboBox<Jugador> jugadorBox;

    @FXML private Spinner<Integer> minuteFromSpinner, minuteToSpinner;
    @FXML private ComboBox<String> thirdBox, laneBox;

    @FXML private ComboBox<String> areaBox, situationBox, bodyPartBox, preActionBox, resultBox;

    @FXML private ImageView fieldMap;
    @FXML private Canvas canvasTiros;
    @FXML private ImageView goalView;
    @FXML private Canvas canvasArco;

    @FXML private TextField angleXFieldCampo;
    @FXML private TextField angleYFieldCampo;
    @FXML private TextField angleXFieldArco;
    @FXML private TextField angleYFieldArco;

    @FXML private Button guardarBtn;

    private Tiro ultimoFormulario = new Tiro();

    @FXML
    public void initialize() {
        EquiposApiClient.getEquiposAsync()
                .thenAccept(this::llenarComboEquipos)
                .exceptionally(ex -> {
                    Platform.runLater(() ->
                            alerta("Error", "No se pudieron cargar equipos:\n" + ex.getMessage(),
                                    javafx.scene.control.Alert.AlertType.ERROR)
                    );
                    return null;
                });

        equipoBox.setOnAction(e -> {
            Equipo seleccionado = equipoBox.getValue();
            if (seleccionado != null) {
                cargarJugadoresPorEquipo(seleccionado.getTeamId());
            }
        });

        minuteFromSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 0)
        );
        minuteToSpinner.setValueFactory(
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 120, 90)
        );

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

        canvasTiros.setOnMouseClicked(e -> {
            double cx = e.getX();
            double cy = e.getY();
            ultimoFormulario.setX(cx);
            ultimoFormulario.setY(cy);
            dibujarPuntoCancha(cx, cy);

            double centerX = canvasTiros.getWidth() / 2.0;
            double centerY = canvasTiros.getHeight() / 2.0;
            double dx = cx - centerX;
            double dy = centerY - cy;

            angleXFieldCampo.setText(String.format("%.1f", dx));
            angleYFieldCampo.setText(String.format("%.1f", dy));
        });

        canvasArco.setOnMouseClicked(e -> {
            double gx = e.getX();
            double gy = e.getY();
            ultimoFormulario.setDestinoX(gx);
            ultimoFormulario.setDestinoY(gy);

            dibujarPuntoArco(gx, gy);

            double centerXArco = canvasArco.getWidth() / 2.0;
            double centerYArco = canvasArco.getHeight() / 2.0;
            double dx2 = gx - centerXArco;
            double dy2 = centerYArco - gy;

            angleXFieldArco.setText(String.format("%.1f", dx2));
            angleYFieldArco.setText(String.format("%.1f", dy2));
        });

        // 8) Botón “Guardar tiro”
        guardarBtn.setOnAction(evt -> guardarTiro());
    }

    private void llenarComboEquipos(List<Equipo> lista) {
        Platform.runLater(() -> {
            equipoBox.getItems().setAll(lista);
            equipoBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Equipo team) {
                    return (team == null ? "" : team.getName());
                }
                @Override
                public Equipo fromString(String string) {
                    return null;
                }
            });
        });
    }

    private void cargarJugadoresPorEquipo(int teamId) {
        CompletableFuture
                .supplyAsync(() -> {
                    try {
                        return JugadorApiClient.getJugadoresPorEquipo(teamId);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .thenAccept(this::llenarComboJugadores)
                .exceptionally(ex -> {
                    Platform.runLater(() ->
                            alerta("Error",
                                    "No se pudieron cargar jugadores:\n" + ex.getCause().getMessage(),
                                    javafx.scene.control.Alert.AlertType.ERROR
                            )
                    );
                    return null;
                });
    }

    private void llenarComboJugadores(List<Jugador> lista) {
        Platform.runLater(() -> {
            jugadorBox.getItems().setAll(lista);
            jugadorBox.setConverter(new StringConverter<>() {
                @Override
                public String toString(Jugador jug) {
                    return (jug == null ? "" : jug.getPlayerName());
                }
                @Override
                public Jugador fromString(String string) {
                    return null;
                }
            });
        });
    }

    private void dibujarPuntoCancha(double x, double y) {
        GraphicsContext gc = canvasTiros.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasTiros.getWidth(), canvasTiros.getHeight());
        gc.setFill(Color.YELLOW);
        gc.fillOval(x - 4, y - 4, 8, 8);
    }

    private void dibujarPuntoArco(double x, double y) {
        GraphicsContext gc = canvasArco.getGraphicsContext2D();
        gc.clearRect(0, 0, canvasArco.getWidth(), canvasArco.getHeight());
        gc.setFill(Color.ORANGE);
        gc.fillOval(x - 4, y - 4, 8, 8);
    }

    private void guardarTiro() {
        try {
            Equipo selEq = equipoBox.getValue();
            Jugador selJ = jugadorBox.getValue();
            if (selEq == null) {
                alerta("Falta Equipo", "Selecciona un equipo", javafx.scene.control.Alert.AlertType.WARNING);
                return;
            }
            if (selJ == null) {
                alerta("Falta Jugador", "Selecciona un jugador", javafx.scene.control.Alert.AlertType.WARNING);
                return;
            }

            Tiro tiro = new Tiro();
            tiro.setEquipoId(selEq.getTeamId());
            tiro.setEquipoNombre(selEq.getName());
            tiro.setJugadorId(selJ.getPlayerId());
            tiro.setJugadorNombre(selJ.getPlayerName());

            tiro.setMinuto(minuteFromSpinner.getValue());

            tiro.setThird(thirdBox.getValue());
            tiro.setLane(laneBox.getValue());

            tiro.setArea(areaBox.getValue());
            tiro.setSituation(situationBox.getValue());
            tiro.setBodyPart(bodyPartBox.getValue());
            tiro.setPreAction(preActionBox.getValue());
            tiro.setResult(resultBox.getValue());

            tiro.setXgot(0.0);

            tiro.setX(ultimoFormulario.getX());
            tiro.setY(ultimoFormulario.getY());
            tiro.setDestinoX(ultimoFormulario.getDestinoX());
            tiro.setDestinoY(ultimoFormulario.getDestinoY());

            TiroApiClient.saveTiroAsync(tiro)
                    .thenAccept(saved -> Platform.runLater(() -> {
                        alerta("Éxito",
                                "Tiro guardado con ID: " + saved.getId() +
                                        "\n xGOT calculado: " + saved.getXgot(),
                                javafx.scene.control.Alert.AlertType.INFORMATION
                        );
                        limpiarFormulario();
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() ->
                                alerta("Error al guardar", ex.getMessage(), javafx.scene.control.Alert.AlertType.ERROR)
                        );
                        return null;
                    });

        } catch (Exception e) {
            alerta("Error Validación", "Revisa los campos:\n" + e.getMessage(),
                    javafx.scene.control.Alert.AlertType.WARNING);
        }
    }

    private void limpiarFormulario() {
        minuteFromSpinner.getValueFactory().setValue(0);
        minuteToSpinner.getValueFactory().setValue(90);

        thirdBox.setValue("Todos");
        laneBox.setValue("Todos");

        areaBox.setValue("Cualquier zona");
        situationBox.setValue("Cualquier situación");
        bodyPartBox.setValue("Cualquier parte");
        preActionBox.setValue("Todas las acciones");
        resultBox.setValue("Todos los resultados");

        GraphicsContext gc1 = canvasTiros.getGraphicsContext2D();
        gc1.clearRect(0, 0, canvasTiros.getWidth(), canvasTiros.getHeight());
        GraphicsContext gc2 = canvasArco.getGraphicsContext2D();
        gc2.clearRect(0, 0, canvasArco.getWidth(), canvasArco.getHeight());

        angleXFieldCampo.clear();
        angleYFieldCampo.clear();
        angleXFieldArco.clear();
        angleYFieldArco.clear();

        ultimoFormulario = new Tiro();
    }

    private void alerta(String titulo, String mensaje, javafx.scene.control.Alert.AlertType tipo) {
        Window owner = equipoBox.getScene().getWindow();
        javafx.scene.control.Alert a = new javafx.scene.control.Alert(tipo, mensaje, ButtonType.OK);
        a.setTitle(titulo);
        a.initOwner(owner);
        a.initModality(Modality.WINDOW_MODAL);
        a.showAndWait();
    }

    @Override
    public void onShow() {
        if (ultimoFormulario != null) {
            if (ultimoFormulario.getEquipoId() != 0) {
                for (Equipo eq : equipoBox.getItems()) {
                    if (eq.getTeamId() == ultimoFormulario.getEquipoId()) {
                        equipoBox.setValue(eq);
                        break;
                    }
                }
                cargarJugadoresPorEquipo(ultimoFormulario.getEquipoId());
                Platform.runLater(() -> {
                    for (Jugador j : jugadorBox.getItems()) {
                        if (j.getPlayerId() == ultimoFormulario.getJugadorId()) {
                            jugadorBox.setValue(j);
                            break;
                        }
                    }
                });
            }

            minuteFromSpinner.getValueFactory().setValue(ultimoFormulario.getMinuto());
            minuteToSpinner.getValueFactory().setValue(ultimoFormulario.getMinuto());

            areaBox.setValue(ultimoFormulario.getArea());
            bodyPartBox.setValue(ultimoFormulario.getBodyPart());
            preActionBox.setValue(ultimoFormulario.getPreAction());
            resultBox.setValue(ultimoFormulario.getResult());

            if (ultimoFormulario.getX() != 0.0 && ultimoFormulario.getY() != 0.0) {
                dibujarPuntoCancha(ultimoFormulario.getX(), ultimoFormulario.getY());
                double cx = ultimoFormulario.getX();
                double cy = ultimoFormulario.getY();
                double centerX = canvasTiros.getWidth() / 2.0;
                double centerY = canvasTiros.getHeight() / 2.0;
                double dx = cx - centerX;
                double dy = centerY - cy;
                angleXFieldCampo.setText(String.format("%.1f", dx));
                angleYFieldCampo.setText(String.format("%.1f", dy));
            }
            if (ultimoFormulario.getDestinoX() != 0.0 && ultimoFormulario.getDestinoY() != 0.0) {
                dibujarPuntoArco(ultimoFormulario.getDestinoX(), ultimoFormulario.getDestinoY());
                double gx = ultimoFormulario.getDestinoX();
                double gy = ultimoFormulario.getDestinoY();
                double centerXA = canvasArco.getWidth() / 2.0;
                double centerYA = canvasArco.getHeight() / 2.0;
                double dx2 = gx - centerXA;
                double dy2 = centerYA - gy;
                angleXFieldArco.setText(String.format("%.1f", dx2));
                angleYFieldArco.setText(String.format("%.1f", dy2));
            }
        }
    }

    @Override
    public void onHide() {
        try {
            Equipo selEq = equipoBox.getValue();
            Jugador selJ = jugadorBox.getValue();
            if (selEq != null) {
                ultimoFormulario.setEquipoId(selEq.getTeamId());
                ultimoFormulario.setEquipoNombre(selEq.getName());
            }
            if (selJ != null) {
                ultimoFormulario.setJugadorId(selJ.getPlayerId());
                ultimoFormulario.setJugadorNombre(selJ.getPlayerName());
            }

            ultimoFormulario.setMinuto(minuteFromSpinner.getValue());
            ultimoFormulario.setArea(areaBox.getValue());
            ultimoFormulario.setBodyPart(bodyPartBox.getValue());
            ultimoFormulario.setPreAction(preActionBox.getValue());
            ultimoFormulario.setResult(resultBox.getValue());
        } catch (Exception ignored) {
        }
    }
}
