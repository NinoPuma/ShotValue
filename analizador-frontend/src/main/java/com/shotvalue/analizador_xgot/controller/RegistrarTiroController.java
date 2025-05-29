package com.shotvalue.analizador_xgot.controller;

import com.shotvalue.analizador_xgot.api.TiroCreacionApiClient;
import com.shotvalue.analizador_xgot.model.TiroCreacion;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class RegistrarTiroController {

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

    @FXML
    public void initialize() {
        // Valores estáticos
        parteCuerpoBox.getItems().setAll("Pie", "Cabeza", "Otro");
        tipoJugadaBox.getItems().setAll("Tiro", "Contra", "Juego elaborado");
        resultadoBox.getItems().setAll("Gol", "Atajado", "Fuera", "Bloqueado");

        // TODO: cargar jugadorBox y equipoBox vía sus API clientes

        // Capturar clic para coordenadas
        fieldImage.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onFieldClick);

        // Acción del botón
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
            tiro.setDestinoX(destinoXField.getText().isEmpty() ? null
                    : Double.valueOf(destinoXField.getText()));
            tiro.setDestinoY(destinoYField.getText().isEmpty() ? null
                    : Double.valueOf(destinoYField.getText()));

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

            // Llamada al cliente API correcto
            TiroCreacionApiClient.saveTiro(tiro)
                    .thenAccept(saved -> Platform.runLater(() -> {
                        Alert ok = new Alert(Alert.AlertType.INFORMATION,
                                "Tiro guardado con ID: " + saved.getId());
                        ok.showAndWait();
                    }))
                    .exceptionally(ex -> {
                        Platform.runLater(() ->
                                new Alert(Alert.AlertType.ERROR,
                                        "Error al guardar tiro:\n" + ex.getMessage())
                                        .showAndWait()
                        );
                        return null;
                    });
        } catch (Exception ex) {
            new Alert(Alert.AlertType.WARNING,
                    "Revisa los campos: " + ex.getMessage())
                    .showAndWait();
        }
    }
}
