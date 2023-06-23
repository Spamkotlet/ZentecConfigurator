package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController extends CommonController implements Initializable {

    private Stage primaryStage;

    private Map<Button, Node> panels = new HashMap<>();

    @FXML
    public Button schemeButton;
    @FXML
    public Button sensorsButton;
    @FXML
    public Button actuatorsButton;
    @FXML
    public Button ioButton;
    @FXML
    public Button settingsButton;
    @FXML
    public Button testButton;
    @FXML
    public AnchorPane splitPaneRight;
    @FXML
    public SplitPane mainSplitPane;
    @FXML
    public AnchorPane mainAnchor;

    @FXML
    public void onClickSchemeButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("change-scheme.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    @FXML
    public void onClickSensorsButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("sensors-settings.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    @FXML
    public void onClickActuatorsButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("actuators-settings.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    @FXML
    public void onClickIOButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("io-monitor.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    @FXML
    public void onClickSettingsButton(ActionEvent event) {
        Button button = (Button) event.getSource();
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

    @FXML
    public void onClickTestButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("fxml/testing/change-scheme-testing.fxml");
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

//    private Node createNewNode(String resourcePath) throws IOException {
//        return FXMLLoader.<Parent>load(Objects.requireNonNull(Application.class.getResource(resourcePath)));
//    }

    private void showNode(Node node) {
        splitPaneRight.getChildren().clear();
        splitPaneRight.getChildren().add(node);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        schemeButton.setOnAction(this::onClickSchemeButton);
        sensorsButton.setOnAction(this::onClickSensorsButton);
        actuatorsButton.setOnAction(this::onClickActuatorsButton);
        ioButton.setOnAction(this::onClickIOButton);
        settingsButton.setOnAction(this::onClickSettingsButton);
        testButton.setOnAction(this::onClickTestButton);
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}