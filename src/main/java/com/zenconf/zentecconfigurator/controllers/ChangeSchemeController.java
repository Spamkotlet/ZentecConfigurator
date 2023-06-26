package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.nodes.SchemeTitledPane;

import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
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

    private ModbusUtilSingleton modbusUtilSingleton;

    // Что происходит при открытии экрана
    protected void onOpenedChoiceSchemePane() {
        schemes = getSchemesFromJson();
        ObservableList<String> schemesItems = getSchemesForSchemeNumberChoiceBox(schemes);

        schemeNumberChoiceBox.setItems(schemesItems);
        selectedScheme = readSchemeNumberFromModbus();
        schemeNumberChoiceBox.setValue(schemesItems.get(selectedScheme.getNumber()));
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

    // Что происходит при выборе схемы из списка
    @FXML
    protected void onSelectedSchemeNumber() {
        selectedScheme = schemes.get(schemeNumberChoiceBox.getSelectionModel().getSelectedIndex());

        fillingPane();
        writeSchemeNumberByModbus();
    }

    // Заполнение панели устройствами и датчиками
    private void fillingPane() {
        schemeChoiceTitledPane.getContent();
        actuatorsVbox.getChildren().clear();
        for (Actuator actuator : schemes.get(selectedScheme.getNumber()).getActuators()) {
            SchemeTitledPane schemeTitledPane = new SchemeTitledPane(actuator);
            actuatorsVbox.getChildren().add(schemeTitledPane);
        }
        sensorsVbox.getChildren().clear();
        for (Sensor sensor : schemes.get(selectedScheme.getNumber()).getSensors()) {
            SchemeTitledPane schemeTitledPane = new SchemeTitledPane(sensor);
            sensorsVbox.getChildren().add(schemeTitledPane);
        }
    }

    // Получить список схем для помещения в schemeNumberChoiceBox
    private ObservableList<String> getSchemesForSchemeNumberChoiceBox(List<Scheme> schemes) {
        List<String> schemeStrings = new ArrayList<>();
        for (Scheme scheme : schemes) {
            schemeStrings.add(scheme.getNumber() + 1 + " - " + scheme.getName());
        }
        return FXCollections.observableArrayList(schemeStrings);
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

    // Запись номера схемы в контроллер по Modbus
    private void writeSchemeNumberByModbus() {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        if (modbusUtilSingleton.getMaster() != null) {
            modbusUtilSingleton.writeModbusRegister(5299, selectedScheme.getNumber());
        }
    }

    // Чтение номера схемы из контроллера по Modbus
    private Scheme readSchemeNumberFromModbus() {
        selectedScheme = schemes.get(0);
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        if (modbusUtilSingleton.getMaster() != null) {
            selectedScheme = schemes.get(modbusUtilSingleton.readModbusRegister(5299));
        }
        return selectedScheme;
    }
}
