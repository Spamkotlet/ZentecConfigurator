package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import com.zenconf.zentecconfigurator.models.CustomTitledPane;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.VentSystemSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainController {
    @FXML
    public ImageView schemeButton;
    @FXML
    public AnchorPane splitPaneRight;
    @FXML
    public SplitPane mainSplitPane;
    @FXML
    public AnchorPane mainAnchor;

    public void onClickSchemeButton() throws IOException {
        Parent schemeChoicePane = FXMLLoader.load(Application.class.getResource("change-scheme.fxml"));
        splitPaneRight.getChildren().clear();
        splitPaneRight.getChildren().add(schemeChoicePane);
    }

    public void onClickSensorsButton() throws IOException {
        Parent sensorsSettingsPane = FXMLLoader.load(Application.class.getResource("sensors-settings.fxml"));
        splitPaneRight.getChildren().clear();
        splitPaneRight.getChildren().add(sensorsSettingsPane);
    }

    public void onClickActuatorsButton() throws IOException {
        Parent actuatorsSettingsPane = FXMLLoader.load(Application.class.getResource("actuators-settings.fxml"));
        splitPaneRight.getChildren().clear();
        splitPaneRight.getChildren().add(actuatorsSettingsPane);
    }

    public void onClickInOutsButton() throws IOException {
        Parent inOutsSettingsPane = FXMLLoader.load(Application.class.getResource("in-outs-view.fxml"));
        splitPaneRight.getChildren().clear();
        splitPaneRight.getChildren().add(inOutsSettingsPane);
    }

    public void onClickStartTestButton() throws IOException {
        Node testingPanel = FXMLLoader.load(Application.class.getResource("fxml/testing/change-scheme-testing.fxml"));
        splitPaneRight.getChildren().clear();
        splitPaneRight.getChildren().add(testingPanel);
    }
}