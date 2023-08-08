package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ActuatorsController implements Initializable {
    private List<Actuator> actuatorsInScheme;

    @FXML
    public VBox actuatorsSettingsVbox;
    @FXML
    public ScrollPane actuatorsSettingsScrollPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actuatorsSettingsScrollPane.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
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
