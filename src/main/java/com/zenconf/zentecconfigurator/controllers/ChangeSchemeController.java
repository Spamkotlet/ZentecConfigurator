package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.enums.VarTypes;
import com.zenconf.zentecconfigurator.models.nodes.SchemeTitledPane;

import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.application.Platform;
import javafx.scene.Node;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChangeSchemeController implements Initializable {

    @FXML
    public ChoiceBox<String> schemeNumberChoiceBox;
    @FXML
    public TitledPane schemeChoiceTitledPane;
    @FXML
    public VBox actuatorsVbox;
    @FXML
    public VBox sensorsVbox;
    @FXML
    public ScrollPane changeSchemeScrollPane;

    private List<Scheme> schemes;
    protected static Scheme selectedScheme = null;
    protected static List<Sensor> sensorsInScheme;
    protected static List<Actuator> actuatorsInScheme;
    private ModbusUtilSingleton modbusUtilSingleton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        changeSchemeScrollPane.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Потом надо запихать в метод
                ObservableList<String> schemesItems = getSchemesForSchemeNumberChoiceBox(schemes);
                selectedScheme = readSchemeNumberFromModbus();
                schemeNumberChoiceBox.setValue(schemesItems.get(selectedScheme.getNumber()));
            }
        });

        onOpenedChoiceSchemePane();
        schemeNumberChoiceBox.setOnAction(e -> {
                    System.out.println("Выбор схемы");
                    onSelectedSchemeNumber();
                }
        );
    }

    // Что происходит при открытии экрана
    protected void onOpenedChoiceSchemePane() {
        schemes = getSchemesFromJson();
        ObservableList<String> schemesItems = getSchemesForSchemeNumberChoiceBox(schemes);

        schemeNumberChoiceBox.setItems(schemesItems);
        selectedScheme = readSchemeNumberFromModbus();
        schemeNumberChoiceBox.setValue(schemesItems.get(selectedScheme.getNumber()));
    }

    // Что происходит при выборе схемы из списка
    protected void onSelectedSchemeNumber() {

        selectedScheme = schemes.get(schemeNumberChoiceBox.getSelectionModel().getSelectedIndex());
        sensorsInScheme = selectedScheme.getSensors();
        actuatorsInScheme = selectedScheme.getActuators();

        List<Actuator> actuatorList = getActuatorsFromJson();
        for (Actuator actuatorInScheme : actuatorsInScheme) {
            for (Actuator actuator : actuatorList) {
                if (actuatorInScheme.getName().equals(actuator.getName())) {
                    actuatorInScheme.setAttributes(actuator.getAttributes());
                    actuatorInScheme.setAttributeForMonitoring(actuator.getAttributeForMonitoring());
                }
            }
        }

        List<Sensor> sensorList = getSensorsFromJson();
        for (Sensor sensorInScheme : sensorsInScheme) {
            for (Sensor sensor : sensorList) {
                if (sensorInScheme.getName().equals(sensor.getName())) {
                    sensorInScheme.setAttributes(sensor.getAttributes());
                    sensorInScheme.setAttributeForMonitoring(sensor.getAttributeForMonitoring());
                    sensorInScheme.setAttributeForControlling(sensor.getAttributeForControlling());
                }
            }
        }
        fillingPane();
        writeSchemeNumberByModbus();
    }


    // Заполнение панели устройствами и датчиками
    private void fillingPane() {

        schemeChoiceTitledPane.getContent();
        ObservableList<Node> actuatorSchemeTitledPaneNodes = actuatorsVbox.getChildren();
        if (actuatorSchemeTitledPaneNodes != null) {
            for (Node schemeTitledNode : actuatorSchemeTitledPaneNodes) {
                ((SchemeTitledPane) schemeTitledNode).setAttributeIsUsedOff();
            }
        }

        Thread thread;
        Runnable task = () -> {
            Platform.runLater(() -> {
                actuatorsVbox.getChildren().clear();
                for (Actuator actuator : schemes.get(selectedScheme.getNumber()).getActuators()) {
                    SchemeTitledPane schemeTitledPane = new SchemeTitledPane(actuator);
                    actuatorsVbox.getChildren().add(schemeTitledPane);
                }

                ObservableList<Node> sensorSchemeTitledPaneNodes = sensorsVbox.getChildren();
                if (sensorSchemeTitledPaneNodes != null) {
                    for (Node schemeTitledNode : sensorSchemeTitledPaneNodes) {
                        ((SchemeTitledPane) schemeTitledNode).setAttributeIsUsedOff();
                    }
                }

                sensorsVbox.getChildren().clear();
                for (Sensor sensor : schemes.get(selectedScheme.getNumber()).getSensors()) {
                    SchemeTitledPane schemeTitledPane = new SchemeTitledPane(sensor);
                    sensorsVbox.getChildren().add(schemeTitledPane);
                }
            });
        };
        thread = new Thread(task);
        thread.start();
    }

    // Запись номера схемы в контроллер по Modbus
    private void writeSchemeNumberByModbus() {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        if (modbusUtilSingleton.getMaster() != null) {
            modbusUtilSingleton.writeSingleModbusRegister(5299, selectedScheme.getNumber(), VarTypes.UINT8);
        }
    }

    // Чтение номера схемы из контроллера по Modbus
    private Scheme readSchemeNumberFromModbus() {
        selectedScheme = schemes.get(0);
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        if (modbusUtilSingleton.getMaster() != null) {
            selectedScheme = schemes.get(modbusUtilSingleton.readSingleModbusRegister(5299, VarTypes.UINT8));
        }
        return selectedScheme;
    }

    // Получить список схем для помещения в schemeNumberChoiceBox
    private ObservableList<String> getSchemesForSchemeNumberChoiceBox(List<Scheme> schemes) {
        List<String> schemeStrings = new ArrayList<>();
        for (Scheme scheme : schemes) {
            schemeStrings.add(scheme.getNumber() + 1 + " - " + scheme.getName());
        }
        return FXCollections.observableArrayList(schemeStrings);
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
            actuators = mapper.readValue(jsonObject.get("actuators").toString(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return actuators;
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

    // Чтение файла со схемами schemes.json и сохранение схем
    private List<Scheme> getSchemesFromJson() {
        String file = "src/schemes.json";
        List<Scheme> schemes;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(
                        new FileInputStream(file),
                        "windows-1251"))) {
            JSONParser parser = new JSONParser();
            ObjectMapper mapper = new ObjectMapper();
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            schemes = mapper.readValue(jsonObject.get("schemes").toString(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return schemes;
    }
}
