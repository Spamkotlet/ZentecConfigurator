package com.zenconf.zentecconfigurator;

import com.zenconf.zentecconfigurator.controllers.configurator.IOMonitorController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {

    private static final Logger logger = LogManager.getLogger(Application.class);

    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Приложение запускается");

        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setMinHeight(800);
        stage.setMinWidth(1200);
        stage.setTitle("Zentec Configurator");
        stage.setScene(scene);
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/icon-64.png")));
        stage.getIcons().add(icon);
        stage.setMaximized(true);
        stage.show();

        MainController.primaryStage = stage;
        logger.info("Приложение запущено");
    }

    @Override
    public void stop() {
        if (IOMonitorController.executor != null) {
            IOMonitorController.executor.shutdownNow();
            logger.info("IOMonitorController.executor остановлен");
        }
        logger.info("Приложение закрыто");
    }

    public static void main(String[] args) {
        launch();
    }
}