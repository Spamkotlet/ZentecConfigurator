package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class MainController extends CommonController implements Initializable {

    private Stage primaryStage;

    private final Map<Button, Node> panels = new HashMap<>();
    @FXML
    public Button goToHomeButton;
    @FXML
    public Button goToHelpButton;
    @FXML
    public Button goToSettingsButton;
    @FXML
    public AnchorPane mainAnchorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (!goToHomeButton.getStyleClass().contains("button-main-header-active")) {
            goToHomeButton.getStyleClass().remove("button-main-header");
            goToHomeButton.getStyleClass().add("button-main-header-active");
        }

        Node node = null;
        try {
            node = createNewNode("home-page-view.fxml");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        showNode(node);

        goToHomeButton.setOnAction(this::onClickHomeButton);
        goToHelpButton.setOnAction(this::onClickHelpButton);
        goToSettingsButton.setOnAction(this::onClickSettingsButton1);
    }

    private void onClickHomeButton(ActionEvent actionEvent) {
        if (!goToHelpButton.getStyleClass().contains("button-main-header")) {
            goToHelpButton.getStyleClass().add("button-main-header");
            goToHelpButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToSettingsButton.getStyleClass().contains("button-main-header")) {
            goToSettingsButton.getStyleClass().add("button-main-header");
            goToSettingsButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToHomeButton.getStyleClass().contains("button-main-header-active")) {
            goToHomeButton.getStyleClass().remove("button-main-header");
            goToHomeButton.getStyleClass().add("button-main-header-active");
        }

        Button button = (Button) actionEvent.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("home-page-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    private void onClickHelpButton(ActionEvent actionEvent) {
        if (!goToHomeButton.getStyleClass().contains("button-main-header")) {
            goToHomeButton.getStyleClass().add("button-main-header");
            goToHomeButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToSettingsButton.getStyleClass().contains("button-main-header")) {
            goToSettingsButton.getStyleClass().add("button-main-header");
            goToSettingsButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToHelpButton.getStyleClass().contains("button-main-header-active")) {
            goToHelpButton.getStyleClass().remove("button-main-header");
            goToHelpButton.getStyleClass().add("button-main-header-active");
        }

        Button button = (Button) actionEvent.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("help-page-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    private void onClickSettingsButton1(ActionEvent actionEvent) {
        if (!goToHomeButton.getStyleClass().contains("button-main-header")) {
            goToHomeButton.getStyleClass().add("button-main-header");
            goToHomeButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToHelpButton.getStyleClass().contains("button-main-header")) {
            goToHelpButton.getStyleClass().add("button-main-header");
            goToHelpButton.getStyleClass().remove("button-main-header-active");
        }

        if (!goToSettingsButton.getStyleClass().contains("button-main-header-active")) {
            goToSettingsButton.getStyleClass().remove("button-main-header");
            goToSettingsButton.getStyleClass().add("button-main-header-active");
        }

        Button button = (Button) actionEvent.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("settings.fxml");
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
        mainAnchorPane.getChildren().clear();
        mainAnchorPane.getChildren().add(node);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public AnchorPane getMainAnchorPane() {
        return mainAnchorPane;
    }
}