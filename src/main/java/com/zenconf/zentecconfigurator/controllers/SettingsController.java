package com.zenconf.zentecconfigurator.controllers;

import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.serial.SerialPort;
import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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

    ModbusUtilSingleton modbusUtilSingleton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        baudRateChoiceBox.setItems(getBaudRateObservableList());

        SpinnerValueFactory<Integer> slaveIdSpinnerFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 247, modbusUtilSingleton.getSlaveId());
        slaveIdSpinner.setValueFactory(slaveIdSpinnerFactory);
        baudRateChoiceBox.setValue(modbusUtilSingleton.getBaudRate());
        dataBitsChoiceBox.setItems(getDataBitsObservableList());
        dataBitsChoiceBox.setValue(modbusUtilSingleton.getDataBits());
        parityChoiceBox.setItems(getParityObservableList());
        parityChoiceBox.setValue(modbusUtilSingleton.getParity());
        stopBitsChoiceBox.setItems(getStopBitsObservableList());
        stopBitsChoiceBox.setValue(modbusUtilSingleton.getStopBits());

        connectDeviceButton.setOnAction(this::connectDevice);
        disconnectDeviceButton.setOnAction(this::disconnectDevice);
        refreshComPortsButton.setOnAction(this::refreshComPorts);
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
        if (!devicesList.isEmpty()) {
            comPortChoiceBox.setValue(devicesList.get(0));
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
}
