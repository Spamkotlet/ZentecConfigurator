package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import javafx.concurrent.Task;
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

    private Stage window;
    private Stage primaryStage;
    private ProgressBar progressBar;
    private Label progressLabel;

    protected static final Logger logger = LogManager.getLogger();

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    // Загрузка дочернего узла (страницы)
    private Node loadChildNode(String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource(resourcePath));
        Node node = loader.load();
        CommonController controller = loader.getController();
        controller.setPrimaryStage(MainController.primaryStage);
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
    protected void showLoadWindow(Task<?> task) {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource("loading-view.fxml"));
        Parent parent;
        try {
            parent = loader.load();
            Button cancelLoadButton = (Button) parent.lookup("#cancelLoadButton");
            cancelLoadButton.setOnAction(e -> closeLoadWindow(task));

            progressBar = (ProgressBar) parent.lookup("#progressBar");
            progressBar.progressProperty().bind(task.progressProperty());

            progressLabel = (Label) parent.lookup("#progressLabel");
            progressLabel.textProperty().bind(task.messageProperty());

            window = new Stage();
            window.initModality(Modality.APPLICATION_MODAL);
            window.initStyle(StageStyle.UNDECORATED);
            window.setScene(new Scene(parent, 300, 100));
            window.setX(primaryStage.getWidth() / 2 + primaryStage.getX());
            window.setY(primaryStage.getHeight() / 2 + primaryStage.getY());
            window.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Закрыть модальное окно
    protected void closeLoadWindow(Task<?> task) {
        window.close();
        task.cancel(true);
        if (progressBar != null) {
            progressBar.progressProperty().unbind();
            progressLabel.textProperty().unbind();
        }
    }
}
