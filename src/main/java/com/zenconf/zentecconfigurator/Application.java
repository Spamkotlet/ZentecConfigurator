package com.zenconf.zentecconfigurator;

import com.zenconf.zentecconfigurator.controllers.configurator.IOMonitorController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setMinHeight(800);
        stage.setMinWidth(1200);
        stage.setTitle("Zentec Configurator");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();

        MainController mainController = fxmlLoader.getController();
        mainController.setPrimaryStage(stage);
    }

    @Override
    public void stop() {
        if (IOMonitorController.executor != null) {
            IOMonitorController.executor.shutdownNow();
        }
    }
    public static void main(String[] args) {
        launch();
    }
}