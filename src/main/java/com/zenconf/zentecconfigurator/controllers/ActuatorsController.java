package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.nodes.ActuatorTitledPane;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.AccessibleRole;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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

    private Stage primaryStage;
    private List<Actuator> actuatorsInScheme;
    private Scheme selectedScheme;
    private List<Actuator> allActuators;

    @FXML
    public VBox actuatorsSettingsVbox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedScheme = ChangeSchemeController.selectedScheme;

        if (selectedScheme != null) {
            actuatorsInScheme = selectedScheme.getActuators();
            if (actuatorsInScheme != null) {
                fillActuatorsSettingsPane();
            }
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void fillActuatorsSettingsPane() {
        allActuators = getActuatorsFromJson();

        actuatorsSettingsVbox.getChildren().clear();
        for (int i = 0; i < actuatorsInScheme.size(); i++) {
            Actuator actuator = actuatorsInScheme.get(i);
            for (int j = 0; j < allActuators.size(); j++) {
                if (actuator.getName().equals(allActuators.get(j).getName())) {
                    actuator.setAttributes(allActuators.get(j).getAttributes());
                }
            }
            if (actuator.getAttributes() != null) {
                ActuatorTitledPane actuatorTitledPane = new ActuatorTitledPane(actuator);
                actuatorsSettingsVbox.getChildren().add(actuatorTitledPane);
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
