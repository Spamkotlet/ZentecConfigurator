package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
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
    public ImageView schemeImageView;
    @FXML
    public ImageView sensorsImageView;
    @FXML
    public ImageView actuatorsImageView;
    @FXML
    public ImageView peripheryImageView;
    @FXML
    public ImageView ioImageView;

    @FXML
    public BorderPane schemeBorderPane;
    @FXML
    public BorderPane sensorsBorderPane;
    @FXML
    public BorderPane actuatorsBorderPane;
    @FXML
    public BorderPane peripheryBorderPane;
    @FXML
    public BorderPane ioBorderPane;

    @FXML
    public AnchorPane splitPaneRight;
    @FXML
    public SplitPane mainSplitPane;
    @FXML
    public VBox disabledButtonsVBox;

    static BooleanProperty isEnabled = new SimpleBooleanProperty();

    private static final Logger logger = LogManager.getLogger(ConfiguratorController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        schemeBorderPane.onMouseClickedProperty()
                .setValue(
                        e -> {
                            onClickButton("Выбор схемы", "/com/zenconf/zentecconfigurator/fxml/configurator/change-scheme.fxml");
                            if (!schemeBorderPane.getStyleClass().contains("button-configurator-active")) {
                                schemeBorderPane.getStyleClass().remove("button-configurator");
                                schemeBorderPane.getStyleClass().add("button-configurator-active");
                            }

                            if (!sensorsBorderPane.getStyleClass().contains("button-configurator")) {
                                sensorsBorderPane.getStyleClass().remove("button-configurator-active");
                                sensorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!actuatorsBorderPane.getStyleClass().contains("button-configurator")) {
                                actuatorsBorderPane.getStyleClass().remove("button-configurator-active");
                                actuatorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!ioBorderPane.getStyleClass().contains("button-configurator")) {
                                ioBorderPane.getStyleClass().remove("button-configurator-active");
                                ioBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!peripheryBorderPane.getStyleClass().contains("button-configurator")) {
                                peripheryBorderPane.getStyleClass().remove("button-configurator-active");
                                peripheryBorderPane.getStyleClass().add("button-configurator");
                            }
                        }
                );
        sensorsBorderPane.onMouseClickedProperty()
                .setValue(
                        e -> {
                            onClickButton("Датчики", "/com/zenconf/zentecconfigurator/fxml/configurator/sensors-settings.fxml");
                            if (!sensorsBorderPane.getStyleClass().contains("button-configurator-active")) {
                                sensorsBorderPane.getStyleClass().remove("button-configurator");
                                sensorsBorderPane.getStyleClass().add("button-configurator-active");
                            }

                            if (!schemeBorderPane.getStyleClass().contains("button-configurator")) {
                                schemeBorderPane.getStyleClass().remove("button-configurator-active");
                                schemeBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!actuatorsBorderPane.getStyleClass().contains("button-configurator")) {
                                actuatorsBorderPane.getStyleClass().remove("button-configurator-active");
                                actuatorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!ioBorderPane.getStyleClass().contains("button-configurator")) {
                                ioBorderPane.getStyleClass().remove("button-configurator-active");
                                ioBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!peripheryBorderPane.getStyleClass().contains("button-configurator")) {
                                peripheryBorderPane.getStyleClass().remove("button-configurator-active");
                                peripheryBorderPane.getStyleClass().add("button-configurator");
                            }
                        }
                );
        actuatorsBorderPane.onMouseClickedProperty()
                .setValue(
                        e -> {
                            onClickButton("Испонительные устройства", "/com/zenconf/zentecconfigurator/fxml/configurator/actuators-settings.fxml");
                            if (!actuatorsBorderPane.getStyleClass().contains("button-configurator-active")) {
                                actuatorsBorderPane.getStyleClass().remove("button-configurator");
                                actuatorsBorderPane.getStyleClass().add("button-configurator-active");
                            }

                            if (!sensorsBorderPane.getStyleClass().contains("button-configurator")) {
                                sensorsBorderPane.getStyleClass().remove("button-configurator-active");
                                sensorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!schemeBorderPane.getStyleClass().contains("button-configurator")) {
                                schemeBorderPane.getStyleClass().remove("button-configurator-active");
                                schemeBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!ioBorderPane.getStyleClass().contains("button-configurator")) {
                                ioBorderPane.getStyleClass().remove("button-configurator-active");
                                ioBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!peripheryBorderPane.getStyleClass().contains("button-configurator")) {
                                peripheryBorderPane.getStyleClass().remove("button-configurator-active");
                                peripheryBorderPane.getStyleClass().add("button-configurator");
                            }
                        }
                );
        ioBorderPane.onMouseClickedProperty()
                .setValue(
                        e -> {
                            onClickButton("Мониторинг", "/com/zenconf/zentecconfigurator/fxml/configurator/io-monitor.fxml");
                            if (!ioBorderPane.getStyleClass().contains("button-configurator-active")) {
                                ioBorderPane.getStyleClass().remove("button-configurator");
                                ioBorderPane.getStyleClass().add("button-configurator-active");
                            }

                            if (!sensorsBorderPane.getStyleClass().contains("button-configurator")) {
                                sensorsBorderPane.getStyleClass().remove("button-configurator-active");
                                sensorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!schemeBorderPane.getStyleClass().contains("button-configurator")) {
                                schemeBorderPane.getStyleClass().remove("button-configurator-active");
                                schemeBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!actuatorsBorderPane.getStyleClass().contains("button-configurator")) {
                                actuatorsBorderPane.getStyleClass().remove("button-configurator-active");
                                actuatorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!peripheryBorderPane.getStyleClass().contains("button-configurator")) {
                                peripheryBorderPane.getStyleClass().remove("button-configurator-active");
                                peripheryBorderPane.getStyleClass().add("button-configurator");
                            }
                        }
                );
        peripheryBorderPane.onMouseClickedProperty()
                .setValue(
                        e -> {
                            onClickButton("Периферия", "/com/zenconf/zentecconfigurator/fxml/configurator/periphery.fxml");
                            if (!peripheryBorderPane.getStyleClass().contains("button-configurator-active")) {
                                peripheryBorderPane.getStyleClass().remove("button-configurator");
                                peripheryBorderPane.getStyleClass().add("button-configurator-active");
                            }

                            if (!sensorsBorderPane.getStyleClass().contains("button-configurator")) {
                                sensorsBorderPane.getStyleClass().remove("button-configurator-active");
                                sensorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!schemeBorderPane.getStyleClass().contains("button-configurator")) {
                                schemeBorderPane.getStyleClass().remove("button-configurator-active");
                                schemeBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!actuatorsBorderPane.getStyleClass().contains("button-configurator")) {
                                actuatorsBorderPane.getStyleClass().remove("button-configurator-active");
                                actuatorsBorderPane.getStyleClass().add("button-configurator");
                            }

                            if (!ioBorderPane.getStyleClass().contains("button-configurator")) {
                                ioBorderPane.getStyleClass().remove("button-configurator-active");
                                ioBorderPane.getStyleClass().add("button-configurator");
                            }
                        }
                );

        disabledButtonsVBox.visibleProperty().bind(isEnabled);

        Image schemeImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/scheme_button.png")));
        schemeImageView.setFitHeight(80);
        schemeImageView.setFitWidth(80);
        schemeImageView.setMouseTransparent(true);
        schemeImageView.setImage(schemeImage);

        Image sensorsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/sensors_button.png")));
        sensorsImageView.setFitHeight(80);
        sensorsImageView.setFitWidth(80);
        sensorsImageView.setMouseTransparent(true);
        sensorsImageView.setImage(sensorsImage);

        Image actuatorsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/actuators_button.png")));
        actuatorsImageView.setFitHeight(80);
        actuatorsImageView.setFitWidth(80);
        actuatorsImageView.setMouseTransparent(true);
        actuatorsImageView.setImage(actuatorsImage);

        Image peripheryImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/periphery.png")));
        peripheryImageView.setFitHeight(80);
        peripheryImageView.setFitWidth(80);
        peripheryImageView.setMouseTransparent(true);
        peripheryImageView.setImage(peripheryImage);

        Image ioImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/io_button.png")));
        ioImageView.setFitHeight(80);
        ioImageView.setFitWidth(80);
        ioImageView.setMouseTransparent(true);
        ioImageView.setImage(ioImage);
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

    public static void selectScheme() {
        isEnabled.setValue(true);
    }
}
