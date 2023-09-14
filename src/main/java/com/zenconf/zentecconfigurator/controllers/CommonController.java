package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public abstract class CommonController {

    private Stage primaryStage;
    private Stage window;
    protected Thread loadingThread;
    private ProgressBar progressBar;
    private Label progressLabel;
    private int progressSize;
    private double progressStep;
    private double currentProgress;


    protected static final Logger logger = LogManager.getLogger();

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Загрузка дочернего узла (страницы)
    private Node loadChildNode(String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource(resourcePath));
        Node node = loader.load();
        CommonController controller = loader.getController();
        controller.setPrimaryStage(this.getPrimaryStage());
        return node;
    }

    // Показать дочерний узел (страницу)
    private void showChildNode(Pane parentPane, Node childNode) {
        if (MainController.activePanel != null) {
            parentPane.getChildren().clear();
        }
        parentPane.getChildren().add(childNode);
        MainController.activePanel = childNode;
    }

    // Создать дочерний узел (страницу)
    protected void onCreateChildNode(Pane parentNode, String panelName, String resourcePath) {
        Node node = MainController.panels.get(panelName);
        if (node == null) {
            try {
                node = loadChildNode(resourcePath);
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
            MainController.panels.put(panelName, node);
        }

        if (MainController.activePanel != node) {
            logger.info("Открыть окно <" + panelName + ">");
            showChildNode(parentNode, node);
        }
    }

    // Показать простое модальное окно на время загрузки наполнения
    protected void showLoadWindow() {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("loading-view.fxml"));
        Parent parent;
        try {
            parent = loader.load();
            Button cancelLoadButton = (Button) parent.lookup("#cancelLoadButton");
            cancelLoadButton.setOnAction(e -> closeLoadWindow());
            window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.initStyle(StageStyle.UNDECORATED);
            window.setScene(new Scene(parent, 300, 100));
            window.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Закрыть модальное окно
    protected void closeLoadWindow() {
        loadingThread.stop();
        if (progressBar != null) {
            progressBar.setProgress(0.0);
            progressBar = null;
        }
        window.close();
    }

    // Показать модальное окно с выводом прогресса на время загрузки наполнения
    protected void showProgressWindow(int progressSize) {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("loading-progress-view.fxml"));
        Parent parent;
        try {
            parent = loader.load();
            Button cancelLoadButton = (Button) parent.lookup("#cancelLoadButton");
            cancelLoadButton.setOnAction(e -> closeLoadWindow());
            progressBar= null;
            progressBar = (ProgressBar) parent.lookup("#progressBar");
            progressBar.setProgress(0.0);
            progressLabel = (Label) parent.lookup("#progressLabel");
            this.progressSize = progressSize;
            this.progressStep = 1d / progressSize;
            window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.initStyle(StageStyle.UNDECORATED);
            window.setScene(new Scene(parent, 300, 100));
            window.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected void updateProgress() {
        currentProgress += progressStep;
        progressBar.setProgress(currentProgress);
    }
}
