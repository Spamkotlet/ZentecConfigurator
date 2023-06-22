package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intelligt.modbus.jlibmodbus.Modbus;
import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.intelligt.modbus.jlibmodbus.master.ModbusMasterFactory;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.nodes.SchemeTitledPane;

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

// TEST
import com.intelligt.modbus.jlibmodbus.serial.SerialParameters;
import jssc.SerialPortList;

public class ChangeSchemeController implements Initializable {

    @FXML
    public Label schemeNumberLabel;
    @FXML
    public ChoiceBox<String> schemeNumberChoiceBox;
    @FXML
    public TitledPane schemeChoiceTitledPane;
    @FXML
    public VBox choiceSchemeVbox;
    @FXML
    public ScrollPane changeSchemeScrollPane;
    private List<Scheme> schemes;
    protected static Scheme selectedScheme = null;

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
        for (Sensor sensor : schemes.get(index).getSensors()) {
            SchemeTitledPane schemeTitledPane = new SchemeTitledPane(sensor);
            choiceSchemeVbox.getChildren().add(schemeTitledPane);
        }

        setSchemeNumberModbus();
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

    // TEST
    private void setSchemeNumberModbus() {
        SerialParameters sp = new SerialParameters();
        Modbus.setLogLevel(Modbus.LogLevel.LEVEL_DEBUG);
        try {
            String[] dev_list = SerialPortList.getPortNames();
            if (dev_list.length > 0) {
                sp.setDevice(dev_list[0]);
                sp.setBaudRate(SerialPort.BaudRate.BAUD_RATE_9600);
                sp.setDataBits(8);
                sp.setParity(SerialPort.Parity.EVEN);
                sp.setStopBits(1);

                ModbusMaster master = ModbusMasterFactory.createModbusMasterRTU(sp);
                master.connect();

                int slaveId = 247;
                int offset = 0;
                int quantity = 1;

                try {
                    int[] registerValues = master.readHoldingRegisters(slaveId, offset, quantity);
                    for (int value : registerValues) {
                        System.out.println("Address: " + offset++ + ", Value: " + value);
                    }
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        master.disconnect();
                    } catch (ModbusIOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
