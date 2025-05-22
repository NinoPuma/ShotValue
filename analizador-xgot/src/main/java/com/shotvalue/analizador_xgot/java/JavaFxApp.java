package com.shotvalue.analizador_xgot.java;

import com.shotvalue.analizador_xgot.config.SpringFXMLLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class JavaFxApp extends Application {

    private ConfigurableApplicationContext context;

    @Override
    public void init() {
        context = new SpringApplicationBuilder(JavaFxApp.class).run();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        SpringFXMLLoader springLoader = context.getBean(SpringFXMLLoader.class);
        Parent root = springLoader.load("/tfcc/equipos-view.fxml");  // o login-view.fxml, etc.

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ShotValue");
        primaryStage.show();
    }

    @Override
    public void stop() {
        context.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
