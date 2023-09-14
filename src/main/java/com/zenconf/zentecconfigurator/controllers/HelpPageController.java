package com.zenconf.zentecconfigurator.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class HelpPageController extends CommonController implements Initializable {

    @FXML
    public WebView helpPageWebView;
    @FXML
    public VBox progressBarVBoxPane;
    @FXML
    public Button cancelLoadButton;
    private Thread thread;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Runnable task = () -> {
            progressBarVBoxPane.setVisible(true);

            try {
                WebEngine webEngine = helpPageWebView.getEngine();
                Platform.runLater(() -> webEngine.load("https://spamkotlet.github.io/ZentecConfigurator/"));
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Невозможно выполнить операцию");
                    alert.setContentText("- отсутствует файл справки или интернет-соединение");
                    alert.show();
                });
            }

            progressBarVBoxPane.setVisible(false);
        };
        thread = new Thread(task);
        thread.start();

        cancelLoadButton.setOnAction(e -> {
            thread.stop();
            progressBarVBoxPane.setVisible(false);
        });
    }
}
