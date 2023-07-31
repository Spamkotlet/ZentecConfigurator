package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SensorsController implements Initializable {

    private List<Sensor> sensorsInScheme;
    private Scheme selectedScheme;

    @FXML
    public VBox sensorsSettingsVbox;

    @FXML
    private ScrollPane sensorsSettingsScrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sensorsSettingsScrollPane.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sensorsInScheme = ChangeSchemeController.sensorsInScheme;
                if (sensorsInScheme != null) {
                    fillSensorsSettingsPane();
                }
            }
        });
    }

    private void fillSensorsSettingsPane() {
        sensorsSettingsVbox.getChildren().clear();
        for (Sensor sensorInScheme : sensorsInScheme) {
            if (sensorInScheme.getIsUsedDefault()) {
                if (sensorInScheme.getAttributes() != null) {
                    ElementTitledPane elementTitledPane = new ElementTitledPane(sensorInScheme);
                    sensorsSettingsVbox.getChildren().add(elementTitledPane);
                }
            }
        }
    }
}
