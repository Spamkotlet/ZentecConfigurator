package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Scheme;
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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;

public class ActuatorsController implements Initializable {
    private List<Actuator> actuatorsInScheme;
    private Scheme selectedScheme;

    @FXML
    public VBox actuatorsSettingsVbox;
    @FXML
    public ScrollPane actuatorsSettingsScrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actuatorsSettingsScrollPane.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedScheme = ChangeSchemeController.selectedScheme;
                actuatorsInScheme = ChangeSchemeController.actuatorsInScheme;

                if (actuatorsInScheme != null) {
                    fillActuatorsSettingsPane();
                }
            }
        });
    }

    private void fillActuatorsSettingsPane() {
        actuatorsSettingsVbox.getChildren().clear();
        for (Actuator actuatorInScheme : actuatorsInScheme) {
            if (actuatorInScheme.getIsUsedDefault()) {
                if (actuatorInScheme.getAttributes() != null) {
                    ElementTitledPane elementTitledPane = new ElementTitledPane(actuatorInScheme);
                    actuatorsSettingsVbox.getChildren().add(elementTitledPane);
                }
            }
        }
    }
}
