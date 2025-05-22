package com.shotvalue.analizador_xgot;

import com.shotvalue.analizador_xgot.config.SpringFXMLLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class HelloApplication extends Application {

    private static ConfigurableApplicationContext springContext;
    private static SpringFXMLLoader fxmlLoader;

    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(HelloApplication.class).run();
        fxmlLoader = springContext.getBean(SpringFXMLLoader.class); // 💡 OBTENERLO DESDE SPRING
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = fxmlLoader.load("/tfcc/login.fxml");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("ShotValue");
        stage.centerOnScreen();
        stage.setMaximized(false); // o true si querés fullscreen
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
