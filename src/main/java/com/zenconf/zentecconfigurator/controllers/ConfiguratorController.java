package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConfiguratorController implements Initializable {

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
    public AnchorPane splitPaneRight;
    @FXML
    public SplitPane mainSplitPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        schemeButton.setOnAction(this::onClickSchemeButton);
        sensorsButton.setOnAction(this::onClickSensorsButton);
        actuatorsButton.setOnAction(this::onClickActuatorsButton);
        ioButton.setOnAction(this::onClickIOButton);

        Image schemeImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/scheme_button.png")));
        ImageView schemeImageView = new ImageView(schemeImage);
        schemeImageView.setFitHeight(80);
        schemeImageView.setFitWidth(80);
        schemeButton.graphicProperty().setValue(schemeImageView);
        schemeButton.setBackground(Background.EMPTY);
        schemeButton.setText("");

        Image sensorsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/sensors_button.png")));
        ImageView sensorsImageView = new ImageView(sensorsImage);
        sensorsImageView.setFitHeight(80);
        sensorsImageView.setFitWidth(80);
        sensorsButton.graphicProperty().setValue(sensorsImageView);
        sensorsButton.setBackground(Background.EMPTY);
        sensorsButton.setText("");

        Image actuatorsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/actuators_button.png")));
        ImageView actuatorsImageView = new ImageView(actuatorsImage);
        actuatorsImageView.setFitHeight(80);
        actuatorsImageView.setFitWidth(80);
        actuatorsButton.graphicProperty().setValue(actuatorsImageView);
        actuatorsButton.setBackground(Background.EMPTY);
        actuatorsButton.setText("");

        Image inOutsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/io_button.png")));
        ImageView inOutsImageView = new ImageView(inOutsImage);
        inOutsImageView.setFitHeight(80);
        inOutsImageView.setFitWidth(80);
        ioButton.graphicProperty().setValue(inOutsImageView);
        ioButton.setBackground(Background.EMPTY);
        ioButton.setText("");
    }

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

    private Node createNewNode(String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource(resourcePath));
        return loader.load();
    }

    private void showNode(Node node) {
        splitPaneRight.getChildren().clear();
        splitPaneRight.getChildren().add(node);
    }
}
