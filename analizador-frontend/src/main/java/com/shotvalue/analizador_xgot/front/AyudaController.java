package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.front.util.Util;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.*;

public class AyudaController {
    @FXML private TextField faqSearchField;
    @FXML private ListView<String> faqList;
    @FXML private TextField nameField, emailField;
    @FXML private TextArea messageArea;

    private final ObservableList<String> allFaqs = FXCollections.observableArrayList(
            "¿Cómo añado un equipo?",
            "¿Cómo genero un informe?",
            "Atajos de teclado disponibles",
            "Explicación de xGOT",
            "¿Cómo exporto datos?"
    );

    @FXML
    public void initialize() {
        faqList.setItems(FXCollections.observableArrayList(allFaqs));
    }

    @FXML
    private void filterFaqs() {
        String text = faqSearchField.getText().toLowerCase();
        faqList.setItems(allFaqs.filtered(f -> f.toLowerCase().contains(text)));
    }

    @FXML
    private void handleSend() {
        if (nameField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                messageArea.getText().isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Por favor completa todos los campos.").showAndWait();
            return;
        }
        // Aquí llamarías a tu servicio para enviar el mensaje...
        new Alert(Alert.AlertType.INFORMATION, "Tu mensaje ha sido enviado. ¡Gracias!").showAndWait();
        nameField.clear();
        emailField.clear();
        messageArea.clear();
    }

    @FXML private void openDocs()   { Util.openWeb("https://tu-app/docs"); }
    @FXML private void openVideos() { Util.openWeb("https://tu-app/videos"); }
    @FXML private void openSlack()  { Util.openWeb("https://tu-app/support"); }
}
