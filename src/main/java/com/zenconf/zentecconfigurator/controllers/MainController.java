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
    public Button goToHomeButton;
    private String homeButtonPrevStyle;
    @FXML
    public Button goToHelpButton;
    private String helpButtonPrevStyle;
    @FXML
    public Button goToSettingsButton;
    private String settingsButtonPrevStyle;
    @FXML
    public AnchorPane splitPaneRight;
    @FXML
    public SplitPane mainSplitPane;

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
        homeButtonPrevStyle = goToHomeButton.getStyleClass().toString();
        goToHomeButton.setOnAction(this::onClickHomeButton);

        helpButtonPrevStyle = goToHelpButton.getStyleClass().toString();
        goToHelpButton.setOnAction(this::onClickHelpButton);

        settingsButtonPrevStyle = goToSettingsButton.getStyleClass().toString();
        goToSettingsButton.setOnAction(this::onClickSettingsButton1);

        schemeButton.setOnAction(this::onClickSchemeButton);
        sensorsButton.setOnAction(this::onClickSensorsButton);
        actuatorsButton.setOnAction(this::onClickActuatorsButton);
        ioButton.setOnAction(this::onClickIOButton);
        settingsButton.setOnAction(this::onClickSettingsButton);
        z031Button.setOnAction(this::onClickZ031Button);
        testButton.setOnAction(this::onClickTestButton);

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

        Image settingsImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/settings_button.png")));
        ImageView settingsImageView = new ImageView(settingsImage);
        settingsImageView.setFitHeight(80);
        settingsImageView.setFitWidth(80);
        settingsButton.graphicProperty().setValue(settingsImageView);
        settingsButton.setBackground(Background.EMPTY);
        settingsButton.setText("");

        Image z031Image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/z031_button.png")));
        ImageView z031ImageView = new ImageView(z031Image);
        z031ImageView.setFitHeight(80);
        z031ImageView.setFitWidth(80);
        z031Button.graphicProperty().setValue(z031ImageView);
        z031Button.setBackground(Background.EMPTY);
        z031Button.setText("");
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}