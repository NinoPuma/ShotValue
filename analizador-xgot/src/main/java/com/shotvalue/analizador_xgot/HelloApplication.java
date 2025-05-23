package com.shotvalue.analizador_xgot;

import com.shotvalue.analizador_xgot.config.SpringFXMLLoader;
import com.shotvalue.analizador_xgot.services.ImportadorEventosService;
import com.shotvalue.analizador_xgot.services.ImportadorPartidosService;
import jakarta.annotation.PostConstruct;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.nio.file.Path;

@SpringBootApplication
public class HelloApplication extends Application {

    private static ConfigurableApplicationContext springContext;
    private static SpringFXMLLoader fxmlLoader;

    @Autowired
    private ImportadorEventosService importadorEventos;
    @Autowired
    private ImportadorPartidosService importadorPartidosService;

    @PostConstruct
    public void iniciarImportacion() {
        try {
            importadorPartidosService.importarPartidosDesdeCarpeta(
                    Path.of("C:/Users/Santi/Desktop/Partidos")
            );
            importadorEventos.importarDesdeCarpeta(
                    "C:/Users/Santi/Downloads/open-data-master/open-data-master/data/events"
            );
            System.out.println("✅ Importación completa.");
        } catch (IOException e) {
            System.err.println("❌ Error en la importación de partidos: " + e.getMessage());
        }
    }


    @Override
    public void init() {
        springContext = new SpringApplicationBuilder(HelloApplication.class).run();
        fxmlLoader = springContext.getBean(SpringFXMLLoader.class); //
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = fxmlLoader.load("/tfcc/login.fxml");
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("ShotValue");
        stage.centerOnScreen();
        stage.setMaximized(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
