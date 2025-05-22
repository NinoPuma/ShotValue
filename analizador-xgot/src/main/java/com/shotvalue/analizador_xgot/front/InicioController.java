package com.shotvalue.analizador_xgot.front;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shotvalue.analizador_xgot.model.Tiro;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.springframework.stereotype.Component;

import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class InicioController {

    @FXML private Label fechaLabel, equiposCount, partidosCount, shotsCount, xgTotal;
    @FXML private ListView<String> recentsList;

    @FXML
    public void initialize() {
        fechaLabel.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy")));
        cargarEstadisticas();
        cargarRecientes();
    }

    private void cargarEstadisticas() {
        Gson gson = new Gson();
        Type listaGenerica = new TypeToken<List<?>>() {}.getType();

        try {
            // Cantidad de equipos
            URL urlEquipos = new URL("http://localhost:8080/api/equipos");
            HttpURLConnection con1 = (HttpURLConnection) urlEquipos.openConnection();
            con1.setRequestMethod("GET");
            List<?> equipos = gson.fromJson(new InputStreamReader(con1.getInputStream()), listaGenerica);
            equiposCount.setText(String.valueOf(equipos.size()));

            // Cantidad de partidos
            URL urlPartidos = new URL("http://localhost:8080/api/partidos");
            HttpURLConnection con2 = (HttpURLConnection) urlPartidos.openConnection();
            con2.setRequestMethod("GET");
            List<?> partidos = gson.fromJson(new InputStreamReader(con2.getInputStream()), listaGenerica);
            partidosCount.setText(String.valueOf(partidos.size()));

            // Cantidad de tiros y xG total
            URL urlTiros = new URL("http://localhost:8080/api/tiros");
            HttpURLConnection con3 = (HttpURLConnection) urlTiros.openConnection();
            con3.setRequestMethod("GET");
            Type tipoTiros = new TypeToken<List<Tiro>>() {}.getType();
            List<Tiro> tiros = gson.fromJson(new InputStreamReader(con3.getInputStream()), tipoTiros);
            shotsCount.setText(String.valueOf(tiros.size()));
            double totalXg = tiros.stream().mapToDouble(Tiro::getXgot).sum();
            xgTotal.setText(String.format("%.2f", totalXg));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarRecientes() {
        recentsList.getItems().setAll(
                "Shot Map vs Barcelona",
                "Informe Equipo A",
                "Nuevo jugador Deportivo Alavés"
        );
    }
}
