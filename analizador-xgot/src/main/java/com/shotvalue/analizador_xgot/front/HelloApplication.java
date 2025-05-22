package com.shotvalue.analizador_xgot.front;

import com.shotvalue.analizador_xgot.config.SpringFXMLLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import com.shotvalue.analizador_xgot.AnalizadorXgotApplication; // Cambiá por tu clase @SpringBootApplication real

public class HelloApplication extends Application {

    private ConfigurableApplicationContext context;
    private final boolean modoPantallaCompleta = false;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(AnalizadorXgotApplication.class).run(); // Acá arrancás Spring
    }

    @Override
    public void start(Stage stage) throws Exception {
        SpringFXMLLoader fxmlLoader = context.getBean(SpringFXMLLoader.class);
        Parent root = fxmlLoader.load("/tfcc/login.fxml");

        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("ShotValue - Jugadores");

        if (modoPantallaCompleta) {
            stage.setMaximized(true);
        } else {
            stage.setWidth(1200);
            stage.setHeight(800);
            stage.centerOnScreen();
        }

        stage.show();
    }

    @Override
    public void stop() {
        context.close(); // Cerrás Spring al salir
    }

    public static void main(String[] args) {
        launch(args);
    }
}
