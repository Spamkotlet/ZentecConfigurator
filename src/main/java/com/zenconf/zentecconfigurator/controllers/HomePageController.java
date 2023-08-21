package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {

    private final Map<Button, Node> panels = new HashMap<>();

    @FXML
    public Button goToConfiguratorButton;
    @FXML
    public Button goToZ031Button;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        goToConfiguratorButton.setOnAction(this::onClickConfiguratorButton);
        goToZ031Button.setOnAction(this::onClickZ031Button);
    }

    private void onClickConfiguratorButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("configurator-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    private void onClickZ031Button(ActionEvent event) {
        Button button = (Button) event.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("z031-settings.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    private Node createNewNode(String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource(resourcePath));
        return loader.load();
    }

    private void showNode(Node node) {
//        MainController.mainAnchorPane.getChildren().clear();
//        MainController.mainAnchorPane.getChildren().add(node);
    }
}
