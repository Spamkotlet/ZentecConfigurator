package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SensorsController implements Initializable {

    private List<Sensor> sensorsInScheme;

    protected static List<Sensor> sensorsIsInUsed = new ArrayList<>();
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
                    System.out.println("Датчики");
                }
            }
        });
    }

    private void fillSensorsSettingsPane() {
        sensorsSettingsVbox.getChildren().clear();
        for (Sensor sensorInScheme : sensorsInScheme) {
            if (sensorInScheme.getIsUsedDefault()) {
                if (sensorInScheme.getAttributes() != null) {
                    sensorsIsInUsed.add(sensorInScheme);
                    ElementTitledPane elementTitledPane = new ElementTitledPane(sensorInScheme);
                    sensorsSettingsVbox.getChildren().add(elementTitledPane);
                }
            }
        }
    }
}
