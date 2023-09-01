package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class ConfiguratorController implements Initializable {

    private final Map<String, Node> panels = new HashMap<>();

    @FXML
    public Button schemeButton;
    @FXML
    public Button sensorsButton;
    @FXML
    public Button actuatorsButton;
    @FXML
    public Button peripheryButton;
    @FXML
    public Button ioButton;

    @FXML
    public AnchorPane splitPaneRight;
    @FXML
    public SplitPane mainSplitPane;

    private static final Logger logger = LogManager.getLogger(ConfiguratorController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        schemeButton.setOnAction(
                e -> onClickButton("Выбор схемы", "/com/zenconf/zentecconfigurator/fxml/configurator/change-scheme.fxml")
        );
        sensorsButton.setOnAction(
                e -> onClickButton("Датчики", "/com/zenconf/zentecconfigurator/fxml/configurator/sensors-settings.fxml")
        );
        actuatorsButton.setOnAction(
                e -> onClickButton("Испонительные устройства", "/com/zenconf/zentecconfigurator/fxml/configurator/actuators-settings.fxml")
        );
        ioButton.setOnAction(
                e -> onClickButton("Мониторинг", "/com/zenconf/zentecconfigurator/fxml/configurator/io-monitor.fxml")
        );
        peripheryButton.setOnAction(
                e -> onClickButton("Периферия", "/com/zenconf/zentecconfigurator/fxml/configurator/periphery.fxml")
        );

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

        Image peripheryImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/periphery.png")));
        ImageView peripheryImageView = new ImageView(peripheryImage);
        peripheryImageView.setFitHeight(80);
        peripheryImageView.setFitWidth(80);
        peripheryButton.graphicProperty().setValue(peripheryImageView);
        peripheryButton.setBackground(Background.EMPTY);
        peripheryButton.setText("");

        Image inOutsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/io_button.png")));
        ImageView inOutsImageView = new ImageView(inOutsImage);
        inOutsImageView.setFitHeight(80);
        inOutsImageView.setFitWidth(80);
        ioButton.graphicProperty().setValue(inOutsImageView);
        ioButton.setBackground(Background.EMPTY);
        ioButton.setText("");
    }

    private void onClickButton(String panelName, String resourcePath) {
        Node node = panels.get(panelName);
        if (node == null) {
            try {
                node = createNewNode(resourcePath);
            } catch (IOException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }
            panels.put(panelName, node);
        }
        logger.info("Открыть окно <" + panelName + ">");
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
