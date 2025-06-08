package com.shotvalue.analizador_xgot.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class AyudaController {

    @FXML
    private ListView<String> faqList;
    @FXML
    private Label faqTitle;
    @FXML
    private Label faqContent;

    private final ObservableList<String> faqItems = FXCollections.observableArrayList(
            "XG y XGot",
            "Datos",
            "Cálculo XGot"
    );

    @FXML
    public void initialize() {
        faqList.setItems(faqItems);
        faqList.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-font-size: 14px; -fx-padding: 8px;");
                }
            }
        });

        faqList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> handleFaqSelection());

        if (!faqItems.isEmpty()) {
            faqList.getSelectionModel().select(0);
        }
    }

    @FXML
    private void handleFaqSelection() {
        String selected = faqList.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        String key = selected.substring(selected.indexOf(' ') + 1);

        switch (key) {
            case "XG y XGot":
                faqTitle.setText("¿XG y XGot, qué significan?");
                faqContent.setText(
                        "La métrica xG (goles esperados) estima la probabilidad de que un tiro termine en gol dado el contexto del disparo, " +
                                "mientras que xGOT (goles esperados a puerta) extiende este concepto considerando la calidad de la ejecución del tiro " +
                                "cuando este va entre los tres palos."
                );
                break;

            case "Datos":
                faqTitle.setText("¿De dónde extraemos los datos?");
                faqContent.setText(
                        "Los datos utilizados en esta aplicación provienen del repositorio abierto de StatsBomb, " +
                                "una de las empresas líderes en el análisis de datos aplicados al fútbol. " +
                                "StatsBomb ofrece un conjunto de datos públicos de alta calidad que incluye información " +
                                "detallada sobre eventos ocurridos durante los partidos."
                );
                break;

            case "Cálculo XGot":
                faqTitle.setText("¿Cómo se calcula el xGOT?");
                faqContent.setText(
                        "El xGOT se calcula combinando la información contextual del disparo (posición en el campo, ángulo, parte del cuerpo, etc.) " +
                                "con la ubicación exacta donde termina el balón en la portería cuando el tiro va a puerta. " +
                                "A partir de los datos de StatsBomb, transformamos las coordenadas del disparo y su destino en la portería, " +
                                "y aplicamos un modelo que estima la probabilidad de gol teniendo en cuenta esa colocación. Así, se mide no solo la calidad " +
                                "de la oportunidad, sino también la calidad real de la ejecución del remate."
                );
                break;
            default:
                faqTitle.setText(key);
                faqContent.setText("Estamos trabajando para darte una mejor descripción sobre este tema.");
        }
    }
}
