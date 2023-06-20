package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.SchemeTitledPane;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
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
    public VBox choiceSchemeVbox;
    private List<Scheme> schemes;
    static Scheme selectedScheme = null;

    // Чтение файла со схемами schemes.json
    protected void onOpenedChoiceSchemePane() {
        String file = "src/schemes.json";

        schemes = getSchemesFromJson(file);
        ObservableList<String> schemesItems = getSchemeItems(schemes);

        schemeNumberChoiceBox.setItems(schemesItems);
        schemeNumberChoiceBox.setValue(schemesItems.get(4));
        setSchemeNumberLabel(schemes.get(4));
    }

    @FXML
    protected void onSelectedSchemeNumber() {
        int index = schemeNumberChoiceBox.getSelectionModel().getSelectedIndex();
        selectedScheme = schemes.get(index);

        setSchemeNumberLabel(schemes.get(index));
        schemeChoiceTitledPane.getContent();
        choiceSchemeVbox.getChildren().clear();
        choiceSchemeVbox.getChildren().add(schemeChoiceTitledPane);
        for (Actuator actuator : schemes.get(index).getActuators()) {
            SchemeTitledPane schemeTitledPane = new SchemeTitledPane(actuator);
            choiceSchemeVbox.getChildren().add(schemeTitledPane);
        }
    }

    private List<Scheme> getSchemesFromJson(String file) {
        List<Scheme> schemes;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        "windows-1251"))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            schemes = mapper.readValue(jsonObject.get("schemes").toString(), new TypeReference<>() {});
            System.out.println("Number: " + schemes.get(0).getNumber() +
                    " Name: " + schemes.get(0).getName() +
                    " Actuators: " + schemes.get(0).getActuators());
        } catch (Exception e) {
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
        List<Actuator> actuators = scheme.getActuators();
        for (int i = 0; i < actuators.size() - 1; i++) {
            schemeDescription.append(actuators.get(i).getName()).append(", ");
        }
        schemeDescription.append(actuators.get(actuators.size() - 1));
        schemeNumberLabel.setText(schemeDescription.toString());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        onOpenedChoiceSchemePane();

        schemeNumberChoiceBox.setOnAction(e -> {
                    System.out.println("Выбор схемы");
                    onSelectedSchemeNumber();
                }
        );
    }
}
