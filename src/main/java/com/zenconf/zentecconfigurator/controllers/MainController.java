package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class MainController extends CommonController implements Initializable {

    private Stage primaryStage;

    private final Map<Button, Node> panels = new HashMap<>();

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
    public Button z031Button;
    @FXML
    public Button testButton;
    @FXML
    public AnchorPane splitPaneRight;
    @FXML
    public SplitPane mainSplitPane;
    @FXML
    public AnchorPane mainAnchor;

    private void onClickSchemeButton(ActionEvent event) {
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

    private void onClickSensorsButton(ActionEvent event) {
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

    private void onClickActuatorsButton(ActionEvent event) {
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

    private void onClickIOButton(ActionEvent event) {
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

    private void onClickSettingsButton(ActionEvent event) {
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

    private void onClickTestButton(ActionEvent event) {
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
        z031Button.setOnAction(this::onClickZ031Button);
        testButton.setOnAction(this::onClickTestButton);

        Image schemeImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/scheme_button.png")));
        schemeButton.graphicProperty().setValue(new ImageView(schemeImage));
        schemeButton.setBackground(Background.EMPTY);
        schemeButton.setText("");

        Image sensorsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/sensors_button.png")));
        sensorsButton.graphicProperty().setValue(new ImageView(sensorsImage));
        sensorsButton.setBackground(Background.EMPTY);
        sensorsButton.setText("");

        Image actuatorsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/actuators_button.png")));
        actuatorsButton.graphicProperty().setValue(new ImageView(actuatorsImage));
        actuatorsButton.setBackground(Background.EMPTY);
        actuatorsButton.setText("");

        Image inOutsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/io_button.png")));
        ioButton.graphicProperty().setValue(new ImageView(inOutsImage));
        ioButton.setBackground(Background.EMPTY);
        ioButton.setText("");

        Image settingsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/settings_button.png")));
        settingsButton.graphicProperty().setValue(new ImageView(settingsImage));
        settingsButton.setBackground(Background.EMPTY);
        settingsButton.setText("");

        Image z031Image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/z031_button.png")));
        z031Button.graphicProperty().setValue(new ImageView(z031Image));
        z031Button.setBackground(Background.EMPTY);
        z031Button.setText("");
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}