package com.zenconf.zentecconfigurator.controllers;

import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.intelligt.modbus.jlibmodbus.utils.ModbusFunctionCode;
import com.zenconf.zentecconfigurator.models.enums.VarFunctions;
import com.zenconf.zentecconfigurator.models.enums.VarTypes;
import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.TextFlow;
import jssc.SerialPortList;

import java.net.URL;
import java.util.ResourceBundle;

public class SettingsController implements Initializable {

    @FXML
    public ChoiceBox<String> comPortChoiceBox;
    @FXML
    public Spinner<Integer> slaveIdSpinner;
    @FXML
    public ChoiceBox<SerialPort.BaudRate> baudRateChoiceBox;
    @FXML
    public ChoiceBox<Integer> dataBitsChoiceBox;
    @FXML
    public ChoiceBox<SerialPort.Parity> parityChoiceBox;
    @FXML
    public ChoiceBox<Integer> stopBitsChoiceBox;
    @FXML
    public Button connectDeviceButton;
    @FXML
    public Button disconnectDeviceButton;
    @FXML
    public Button refreshComPortsButton;

    // TEST
    @FXML
    public Button testModbusButton;
    @FXML
    public TextField addressTextField;
    @FXML
    public TextField valueTextField;
    @FXML
    public ChoiceBox<VarFunctions> functionChoiceBox;
    @FXML
    public TextFlow functionDescriptionTextFlow;
    @FXML
    public ChoiceBox<VarTypes> varTypeChoiceBox;

    ModbusUtilSingleton modbusUtilSingleton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        baudRateChoiceBox.setItems(getBaudRateObservableList());

        SpinnerValueFactory<Integer> slaveIdSpinnerFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 247, modbusUtilSingleton.getSlaveId());
        slaveIdSpinner.setValueFactory(slaveIdSpinnerFactory);
        baudRateChoiceBox.setValue(modbusUtilSingleton.getBaudRate());
        dataBitsChoiceBox.setItems(getDataBitsObservableList());
        dataBitsChoiceBox.setValue(modbusUtilSingleton.getDataBits());
        parityChoiceBox.setItems(getParityObservableList());
        parityChoiceBox.setValue(modbusUtilSingleton.getParity());
        stopBitsChoiceBox.setItems(getStopBitsObservableList());
        stopBitsChoiceBox.setValue(modbusUtilSingleton.getStopBits());
        functionChoiceBox.setItems(getVarFunctionObservableList());
        functionChoiceBox.setValue(VarFunctions.READ);
        varTypeChoiceBox.setItems(getVarTypesObservableList());
        varTypeChoiceBox.setValue(VarTypes.BOOL);
        addressTextField.setText("5363");
        valueTextField.setText("1");

        connectDeviceButton.setOnAction(this::connectDevice);
        disconnectDeviceButton.setOnAction(this::disconnectDevice);
        refreshComPortsButton.setOnAction(this::refreshComPorts);
        testModbusButton.setOnAction(this::testModbus);
        functionDescriptionTextFlow.setAccessibleText("Описание функций");
    }

    private void connectDevice(ActionEvent actionEvent) {

        modbusUtilSingleton.setDevice(comPortChoiceBox.getValue());
        modbusUtilSingleton.setSlaveId(slaveIdSpinner.getValue());
        modbusUtilSingleton.setBaudRate(baudRateChoiceBox.getValue());
        modbusUtilSingleton.setDataBits(dataBitsChoiceBox.getValue());
        modbusUtilSingleton.setParity(parityChoiceBox.getValue());
        modbusUtilSingleton.setStopBits(stopBitsChoiceBox.getValue());
        modbusUtilSingleton.connect();
    }

    private void disconnectDevice(ActionEvent actionEvent) {
        try {
            modbusUtilSingleton.disconnect();
        } catch (ModbusIOException e) {
            e.printStackTrace();
        }
    }

    private void refreshComPorts(ActionEvent actionEvent) {
        String[] devices = SerialPortList.getPortNames();
        ObservableList<String> devicesList = FXCollections.observableArrayList(devices);
        comPortChoiceBox.setItems(devicesList);
        if (devicesList.size() > 0) {
            comPortChoiceBox.setValue(devicesList.get(0));
        }
    }

    private void testModbus(ActionEvent actionEvent) {
        int address = Integer.parseInt(addressTextField.getText());
        VarTypes varType = varTypeChoiceBox.getValue();
        VarFunctions varFunction = functionChoiceBox.getValue();
        if (varFunction.equals(VarFunctions.READ)) {
            if (varType.equals(VarTypes.BOOL)) {
                valueTextField.setText(String.valueOf(modbusUtilSingleton.readModbusCoil(address)));
            } else if (varType.equals(VarTypes.FLOAT)) {
                valueTextField.setText(String.valueOf(modbusUtilSingleton.readMultipleModbusRegister(address)));
            } else {
                valueTextField.setText(String.valueOf(modbusUtilSingleton.readSingleModbusRegister(address, varType)));
            }
        } else {
            if (varType.equals(VarTypes.BOOL)) {
                boolean value = Boolean.parseBoolean(valueTextField.getText());
                modbusUtilSingleton.writeModbusCoil(address, value);
            } else if (varType.equals(VarTypes.FLOAT)) {
                float value = Float.parseFloat(valueTextField.getText());
                modbusUtilSingleton.writeMultipleModbusRegister(address, value);
            } else {
                int value = Integer.parseInt(valueTextField.getText());
                modbusUtilSingleton.writeSingleModbusRegister(address, value, varType);
            }
        }
    }

    private ObservableList<SerialPort.BaudRate> getBaudRateObservableList() {
        return FXCollections.observableArrayList(SerialPort.BaudRate.values());
    }

    private ObservableList<Integer> getDataBitsObservableList() {
        Integer[] dataBits = new Integer[]{7, 8};
        return FXCollections.observableArrayList(dataBits);
    }

    private ObservableList<SerialPort.Parity> getParityObservableList() {
        return FXCollections.observableArrayList(SerialPort.Parity.values());
    }

    private ObservableList<Integer> getStopBitsObservableList() {
        Integer[] stopBits = new Integer[]{0, 1, 2};
        return FXCollections.observableArrayList(stopBits);
    }

    private ObservableList<ModbusFunctionCode> getModbusFunctionCodeObservableList() {
        return FXCollections.observableArrayList(ModbusFunctionCode.values());
    }

    private ObservableList<VarTypes> getVarTypesObservableList() {
        return FXCollections.observableArrayList(VarTypes.values());
    }

    private ObservableList<VarFunctions> getVarFunctionObservableList() {
        return FXCollections.observableArrayList(VarFunctions.values());
    }
}
