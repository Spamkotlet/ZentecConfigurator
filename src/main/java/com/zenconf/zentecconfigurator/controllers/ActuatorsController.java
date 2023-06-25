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

                if (selectedScheme != null) {
                    actuatorsInScheme = selectedScheme.getActuators();
                    if (actuatorsInScheme != null) {
                        fillActuatorsSettingsPane();
                    }
                }
            }
        });
    }

    private void fillActuatorsSettingsPane() {
        List<Actuator> allActuators = getActuatorsFromJson();

        actuatorsSettingsVbox.getChildren().clear();
        for (Actuator actuator : actuatorsInScheme) {
            for (Actuator allActuator : allActuators) {
                if (actuator.getName().equals(allActuator.getName())) {
                    actuator.setAttributes(allActuator.getAttributes());
                }
            }
            if (actuator.getIsUsedDefault()) {
                if (actuator.getAttributes() != null) {
                    ElementTitledPane elementTitledPane = new ElementTitledPane(actuator);
                    actuatorsSettingsVbox.getChildren().add(elementTitledPane);
                }
            }
        }
    }

    private List<Actuator> getActuatorsFromJson() {
        String file = "src/actuators_attributes.json";

        List<Actuator> actuators;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            actuators = mapper.readValue(jsonObject.get("actuators").toString(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return actuators;
    }
}
