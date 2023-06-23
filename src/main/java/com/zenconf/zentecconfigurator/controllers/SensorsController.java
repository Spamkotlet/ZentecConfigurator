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
                selectedScheme = ChangeSchemeController.selectedScheme;

                if (selectedScheme != null) {
                    sensorsInScheme = selectedScheme.getSensors();
                    if (sensorsInScheme != null) {
                        fillSensorsSettingsPane();
                    }
                }
                System.out.println("ScrollPane newVal");
            }
        });
    }

    private void fillSensorsSettingsPane() {
        List<Sensor> allSensors = getSensorsFromJson();

        sensorsSettingsVbox.getChildren().clear();
        for (int i = 0; i < sensorsInScheme.size(); i++) {
            Sensor sensor = sensorsInScheme.get(i);
            for (int j = 0; j < allSensors.size(); j++) {
                if (sensor.getName().equals(allSensors.get(j).getName())) {
                    sensor.setAttributes(allSensors.get(j).getAttributes());
                }
            }
            if (sensor.getAttributes() != null) {
                ElementTitledPane elementTitledPane = new ElementTitledPane(sensor);
                sensorsSettingsVbox.getChildren().add(elementTitledPane);
            }
        }
    }

    private List<Sensor> getSensorsFromJson() {
        String file = "src/sensors_attributes.json";

        List<Sensor> sensors;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        "windows-1251"))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            sensors = mapper.readValue(jsonObject.get("sensors").toString(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return sensors;
    }
}
