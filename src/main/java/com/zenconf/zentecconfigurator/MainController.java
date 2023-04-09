package com.zenconf.zentecconfigurator;

import com.zenconf.zentecconfigurator.models.CustomTitledPane;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.VentSystemSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainController {
    private List<Scheme> schemes;
    @FXML
    public ChoiceBox<String> schemeNumberChoiceBox;
    @FXML
    public Label schemeNumberLabel;
    @FXML
    public VBox ventSystemSettingsVbox;

    @FXML
    protected void onSelectedSchemeNumber() {
        int index = schemeNumberChoiceBox.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            setSchemeNumberLabel(schemes.get(index));
            ventSystemSettingsVbox.getChildren().clear();
            for (String actuator: schemes.get(index).getActuators()) {
                //ventSystemSettingsVbox.getChildren().add(new CustomTitledPane(actuator, "Parameter"));
                TitledPane titledPane = new TitledPane();
                titledPane.setText(actuator);

            }
        }
    }

    @FXML
    protected void onOpenedVentSystemSettingsTab() {
        String file = "src/schemes.json";

        schemes = getSchemesFromJson(file);
        ObservableList<String> schemesItems = getSchemeItems(schemes);

        schemeNumberChoiceBox.setItems(schemesItems);
        schemeNumberChoiceBox.setValue(schemesItems.get(22));
        setSchemeNumberLabel(schemes.get(22));
    }

    private List<Scheme> getSchemesFromJson(String file) {
        List<Scheme> schemes;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        "windows-1251"))) {
            ObjectMapper mapper = new ObjectMapper();
            StringBuilder jsonString = new StringBuilder();
            while (reader.ready()) {
                jsonString.append(reader.readLine());
            }
            VentSystemSettings ventSystemSettings = mapper.readValue(
                    new String(jsonString.toString().getBytes(), StandardCharsets.UTF_8),
                    VentSystemSettings.class);
            schemes = ventSystemSettings.getSchemes();
            System.out.println("Number: " + schemes.get(0).getNumber() +
                    " Name: " + schemes.get(0).getName() +
                    " Actuators: " + schemes.get(0).getActuators());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return schemes;
    }

    private ObservableList<String> getSchemeItems(List<Scheme> schemes) {
        List<String> schemeStrings = new ArrayList<>();
        for (Scheme scheme : schemes) {
            schemeStrings.add(Integer.parseInt(scheme.getNumber()) + 1 + " - " + scheme.getName());
        }
        return FXCollections.observableArrayList(schemeStrings);
    }

    private void setSchemeNumberLabel(Scheme scheme) {
        StringBuilder schemeDescription = new StringBuilder();
        List<String> actuators = scheme.getActuators();
        for (int i = 0; i < actuators.size() - 1; i++) {
            schemeDescription.append(actuators.get(i)).append(", ");
        }
        schemeDescription.append(actuators.get(actuators.size() - 1));
        schemeNumberLabel.setText(schemeDescription.toString());
    }
}