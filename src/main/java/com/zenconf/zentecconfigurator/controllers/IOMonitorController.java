package com.zenconf.zentecconfigurator.controllers;

import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.nodes.MonitorTextFlow;
import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class IOMonitorController implements Initializable {

    @FXML
    public Button startPollingButton;
    @FXML
    public Button stopPollingButton;

    @FXML
    public VBox sensorsMonitorVBox;
    @FXML
    public ScrollPane ioMonitorScrollPane;

    ModbusUtilSingleton modbusUtilSingleton;
    Timer myTimer;
    List<Sensor> sensorsInScheme = new ArrayList<>();
    List<MonitorTextFlow> monitorTextFlowList = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();

        startPollingButton.setOnAction(this::startPolling);
        stopPollingButton.setOnAction(this::stopPolling);

        ioMonitorScrollPane.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sensorsInScheme = ChangeSchemeController.sensorsInScheme;
                sensorsMonitorVBox.getChildren().clear();
                if (sensorsInScheme != null) {
                    for (Sensor sensor : sensorsInScheme) {
                        if (sensor.getIsUsedDefault()) {
                            if (sensor.getAttributeForMonitoring() != null) {
                                MonitorTextFlow monitorTextFlow = new MonitorTextFlow(sensor);
                                monitorTextFlowList.add(monitorTextFlow);
                                sensorsMonitorVBox.getChildren().add(monitorTextFlow.getTextFlow());
                            }
                        }
                    }
                }
            }
        });
    }

    private void startPolling(ActionEvent actionEvent) {
        ModbusMaster master = modbusUtilSingleton.getMaster();
        if (master != null) {
            if (myTimer != null) {
                myTimer.cancel();
            }

            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    for (MonitorTextFlow monitorTextFlow : monitorTextFlowList) {
                        monitorTextFlow.update();
                    }

                }
            }, 0, 1000);

        }
    }

    private void stopPolling(ActionEvent actionEvent) {
        if (myTimer != null) {
            myTimer.cancel();
            myTimer = null;
        }
    }
}
