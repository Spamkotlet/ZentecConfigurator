package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.nodes.LabeledSpinner;
import com.zenconf.zentecconfigurator.models.z031.ElectricParameters;
import com.zenconf.zentecconfigurator.models.z031.WaterParameters;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class Z031Controller implements Initializable {

    private ElectricParameters electricParameters;
    private WaterParameters waterParameters;

    @FXML
    public VBox electricParametersVBox;
    @FXML
    public VBox waterParametersVBox;
    @FXML
    public VBox z031ParametersVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        electricParameters = (ElectricParameters) getZ031ParametersFromJson().get(0);
        waterParameters = (WaterParameters) getZ031ParametersFromJson().get(1);

        electricParametersVBox.getChildren().clear();
        if (electricParameters != null) {
            for (Attribute attribute: electricParameters.getAttributes()) {
                LabeledSpinner labeledSpinner = new LabeledSpinner(attribute, 30);
                electricParametersVBox.getChildren().add(labeledSpinner.getSpinner());
            }
        }

        waterParametersVBox.getChildren().clear();
        if (waterParameters != null) {
            for (Attribute attribute: waterParameters.getAttributes()) {
                LabeledSpinner labeledSpinner = new LabeledSpinner(attribute, 30);
                waterParametersVBox.getChildren().add(labeledSpinner.getSpinner());
            }
        }

        z031ParametersVBox.getChildren().clear();
        if (waterParameters != null) {
            for (Attribute attribute: waterParameters.getAttributes()) {
                LabeledSpinner labeledSpinner = new LabeledSpinner(attribute, 30);
                z031ParametersVBox.getChildren().add(labeledSpinner.getSpinner());
            }
        }
    }

    private List<Object> getZ031ParametersFromJson() {
        String file = "src/z031_parameters.json";

        ElectricParameters electricParameters;
        WaterParameters waterParameters;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        StandardCharsets.UTF_8))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            electricParameters = mapper.readValue(jsonObject.get("electric").toString(), new TypeReference<>() {});
            waterParameters = mapper.readValue(jsonObject.get("water").toString(), new TypeReference<>() {});
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return Arrays.asList(electricParameters, waterParameters);
    }
}
