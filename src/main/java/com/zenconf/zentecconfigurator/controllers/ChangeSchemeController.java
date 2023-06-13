package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.VentSystemSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChangeSchemeController implements Initializable {

    @FXML
    public Label schemeNumberLabel;
    @FXML
    public ChoiceBox<String> schemeNumberChoiceBox;
    @FXML
    public TitledPane schemeChoiceTitledPane;
    @FXML
    public VBox ventSystemSettingsVbox;
    private List<Scheme> schemes;

    @FXML
    protected void onSelectedSchemeNumber() {
        int index = schemeNumberChoiceBox.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            setSchemeNumberLabel(schemes.get(index));
            schemeChoiceTitledPane.getContent();
            ventSystemSettingsVbox.getChildren().clear();
            ventSystemSettingsVbox.getChildren().add(schemeChoiceTitledPane);
            for (String actuator : schemes.get(index).getActuators()) {
                //ventSystemSettingsVbox.getChildren().add(new CustomTitledPane(actuator, "Parameter"));
                TitledPane titledPane = new TitledPane();
                titledPane.setExpanded(true);
                titledPane.setText(actuator);

                CheckBox checkBox = new CheckBox("Используется");
                checkBox.setPadding(new Insets(5, 5, 5, 5));
                checkBox.setAlignment(Pos.CENTER_LEFT);
                titledPane.setContent(checkBox);
                ventSystemSettingsVbox.getChildren().add(titledPane);
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        onOpenedVentSystemSettingsTab();

        schemeNumberChoiceBox.setOnAction(e ->
                onSelectedSchemeNumber()
        );
    }
}
