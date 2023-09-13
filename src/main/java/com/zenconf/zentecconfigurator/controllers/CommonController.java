package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class CommonController {

    private Stage primaryStage;

    private final Map<String, Node> panels = new HashMap<>();
    private Node activePanel = null;

    protected static final Logger logger = LogManager.getLogger();

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private Node createChildNode(String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource(resourcePath));
        Node node = loader.load();
        CommonController controller = loader.getController();
        controller.setPrimaryStage(this.getPrimaryStage());
        return node;
    }

    private void showChildNode(Pane parentPane, Node childNode) {
        if (activePanel != null) {
            parentPane.getChildren().remove(activePanel);
        }
        parentPane.getChildren().add(childNode);
        activePanel = childNode;
    }

    protected void onCreateChildNode(Pane parentNode, String panelName, String resourcePath) {
        Node node = panels.get(panelName);
        if (node == null) {
            try {
                node = createChildNode(resourcePath);
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
            panels.put(panelName, node);
        }

        if (activePanel != node) {
            logger.info("Открыть окно <" + panelName + ">");
            showChildNode(parentNode, node);
        }
    }
}
