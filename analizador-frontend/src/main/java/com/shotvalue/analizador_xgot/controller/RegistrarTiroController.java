package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.TiroCreacionApiClient;
import com.shotvalue.analizador_xgot.model.TiroCreacion;
import com.shotvalue.analizador_xgot.view.ViewLifecycle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class RegistrarTiroController implements ViewLifecycle {

    @FXML private ComboBox<String> jugadorBox;
    @FXML private ComboBox<String> equipoBox;
    @FXML private ComboBox<String> parteCuerpoBox;
    @FXML private ComboBox<String> tipoJugadaBox;
    @FXML private ComboBox<String> resultadoBox;
    @FXML private TextField minutoField;

    @FXML private CheckBox porteroNoSeMueveCheck;
    @FXML private CheckBox brazosExtendidosCheck;
    @FXML private CheckBox presionDefensivaCheck;
    @FXML private CheckBox reboteCheck;
    @FXML private CheckBox manoDominanteCheck;
    @FXML private CheckBox dentroDelAreaCheck;
    @FXML private CheckBox jugadaElaboradaCheck;
    @FXML private CheckBox tiroConBoteCheck;
    @FXML private CheckBox porteroTapadoCheck;

    @FXML private TextField anguloField;
    @FXML private TextField velocidadField;
    @FXML private TextField piesSueloField;
    @FXML private TextField defensasCercaField;
    @FXML private TextField zonaDisparoField;

    @FXML private ImageView fieldImage;
    @FXML private TextField xField;
    @FXML private TextField yField;
    @FXML private TextField destinoXField;
    @FXML private TextField destinoYField;

    @FXML private Button guardarBtn;

    private TiroCreacion ultimoFormulario = new TiroCreacion();

    @FXML
    public void initialize() {
        parteCuerpoBox.getItems().setAll("Pie", "Cabeza", "Otro");
        tipoJugadaBox.getItems().setAll("Tiro", "Contra", "Juego elaborado");
        resultadoBox.getItems().setAll("Gol", "Atajado", "Fuera", "Bloqueado");

        // TODO: Cargar jugadorBox y equipoBox desde API si es necesario

        fieldImage.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onFieldClick);
        guardarBtn.setOnAction(evt -> guardarTiro());
    }

    private void onFieldClick(MouseEvent e) {
        xField.setText(String.format("%.1f", e.getX()));
        yField.setText(String.format("%.1f", e.getY()));
    }

    private void guardarTiro() {
        try {
            TiroCreacion tiro = new TiroCreacion();
            tiro.setX(Double.parseDouble(xField.getText()));
            tiro.setY(Double.parseDouble(yField.getText()));
            tiro.setDestinoX(destinoXField.getText().isEmpty() ? null : Double.valueOf(destinoXField.getText()));
            tiro.setDestinoY(destinoYField.getText().isEmpty() ? null : Double.valueOf(destinoYField.getText()));

            tiro.setJugadorNombre(jugadorBox.getValue());
            tiro.setEquipoNombre(equipoBox.getValue());
            tiro.setParteDelCuerpo(parteCuerpoBox.getValue());
            tiro.setTipoDeJugada(tipoJugadaBox.getValue());
            tiro.setResultado(resultadoBox.getValue());
            tiro.setMinuto(Integer.parseInt(minutoField.getText()));

            tiro.setPorteroNoSeMueve(porteroNoSeMueveCheck.isSelected());
            tiro.setBrazosExtendidos(brazosExtendidosCheck.isSelected());
            tiro.setPresionDefensiva(presionDefensivaCheck.isSelected());
            tiro.setRebote(reboteCheck.isSelected());
            tiro.setManoDominante(manoDominanteCheck.isSelected());
            tiro.setDentroDelArea(dentroDelAreaCheck.isSelected());
            tiro.setJugadaElaborada(jugadaElaboradaCheck.isSelected());
            tiro.setTiroConBote(tiroConBoteCheck.isSelected());
            tiro.setPorteroTapado(porteroTapadoCheck.isSelected());

            tiro.setAnguloDisparo(Double.parseDouble(anguloField.getText()));
            tiro.setVelocidadDisparo(Double.parseDouble(velocidadField.getText()));
            tiro.setPiesEnSuelo(Integer.parseInt(piesSueloField.getText()));
            tiro.setCantidadDefensasCerca(Integer.parseInt(defensasCercaField.getText()));
            tiro.setZonaDelDisparo(zonaDisparoField.getText());

            TiroCreacionApiClient.saveTiro(tiro)
                    .thenAccept(saved -> Platform.runLater(() -> {
                        Alert ok = new Alert(Alert.AlertType.INFORMATION, "Tiro guardado con ID: " + saved.getId());
                        ok.showAndWait();
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() ->
                                new Alert(Alert.AlertType.ERROR, "Error al guardar tiro:\n" + ex.getMessage()).showAndWait()
                        );
                        return null;
                    });

        } catch (Exception ex) {
            new Alert(Alert.AlertType.WARNING, "Revisa los campos: " + ex.getMessage()).showAndWait();
        }
    }

    @Override
    public void onHide() {
        try {
            TiroCreacion t = new TiroCreacion();
            t.setJugadorNombre(jugadorBox.getValue());
            t.setEquipoNombre(equipoBox.getValue());
            t.setParteDelCuerpo(parteCuerpoBox.getValue());
            t.setTipoDeJugada(tipoJugadaBox.getValue());
            t.setResultado(resultadoBox.getValue());
            t.setMinuto(Integer.parseInt(minutoField.getText()));

            t.setX(Double.parseDouble(xField.getText()));
            t.setY(Double.parseDouble(yField.getText()));
            t.setDestinoX(destinoXField.getText().isEmpty() ? null : Double.parseDouble(destinoXField.getText()));
            t.setDestinoY(destinoYField.getText().isEmpty() ? null : Double.parseDouble(destinoYField.getText()));

            t.setPorteroNoSeMueve(porteroNoSeMueveCheck.isSelected());
            t.setBrazosExtendidos(brazosExtendidosCheck.isSelected());
            t.setPresionDefensiva(presionDefensivaCheck.isSelected());
            t.setRebote(reboteCheck.isSelected());
            t.setManoDominante(manoDominanteCheck.isSelected());
            t.setDentroDelArea(dentroDelAreaCheck.isSelected());
            t.setJugadaElaborada(jugadaElaboradaCheck.isSelected());
            t.setTiroConBote(tiroConBoteCheck.isSelected());
            t.setPorteroTapado(porteroTapadoCheck.isSelected());

            t.setAnguloDisparo(Double.parseDouble(anguloField.getText()));
            t.setVelocidadDisparo(Double.parseDouble(velocidadField.getText()));
            t.setPiesEnSuelo(Integer.parseInt(piesSueloField.getText()));
            t.setCantidadDefensasCerca(Integer.parseInt(defensasCercaField.getText()));
            t.setZonaDelDisparo(zonaDisparoField.getText());

            ultimoFormulario = t;
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onShow() {
        if (ultimoFormulario == null) return;

        jugadorBox.setValue(ultimoFormulario.getJugadorNombre());
        equipoBox.setValue(ultimoFormulario.getEquipoNombre());
        parteCuerpoBox.setValue(ultimoFormulario.getParteDelCuerpo());
        tipoJugadaBox.setValue(ultimoFormulario.getTipoDeJugada());
        resultadoBox.setValue(ultimoFormulario.getResultado());
        minutoField.setText(String.valueOf(ultimoFormulario.getMinuto()));

        xField.setText(String.valueOf(ultimoFormulario.getX()));
        yField.setText(String.valueOf(ultimoFormulario.getY()));
        destinoXField.setText(ultimoFormulario.getDestinoX() != null ? String.valueOf(ultimoFormulario.getDestinoX()) : "");
        destinoYField.setText(ultimoFormulario.getDestinoY() != null ? String.valueOf(ultimoFormulario.getDestinoY()) : "");

        porteroNoSeMueveCheck.setSelected(ultimoFormulario.isPorteroNoSeMueve());
        brazosExtendidosCheck.setSelected(ultimoFormulario.isBrazosExtendidos());
        presionDefensivaCheck.setSelected(ultimoFormulario.isPresionDefensiva());
        reboteCheck.setSelected(ultimoFormulario.isRebote());
        manoDominanteCheck.setSelected(ultimoFormulario.isManoDominante());
        dentroDelAreaCheck.setSelected(ultimoFormulario.isDentroDelArea());
        jugadaElaboradaCheck.setSelected(ultimoFormulario.isJugadaElaborada());
        tiroConBoteCheck.setSelected(ultimoFormulario.isTiroConBote());
        porteroTapadoCheck.setSelected(ultimoFormulario.isPorteroTapado());

        anguloField.setText(String.valueOf(ultimoFormulario.getAnguloDisparo()));
        velocidadField.setText(String.valueOf(ultimoFormulario.getVelocidadDisparo()));
        piesSueloField.setText(String.valueOf(ultimoFormulario.getPiesEnSuelo()));
        defensasCercaField.setText(String.valueOf(ultimoFormulario.getCantidadDefensasCerca()));
        zonaDisparoField.setText(ultimoFormulario.getZonaDelDisparo());
    }
}
