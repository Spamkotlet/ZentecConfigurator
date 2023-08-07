package com.zenconf.zentecconfigurator.controllers;

import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.nodes.MonitorTextFlow;
import com.zenconf.zentecconfigurator.models.nodes.SetpointSpinner;
import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IOMonitorController implements Initializable {

    @FXML
    public Button startPollingButton;
    @FXML
    public Button stopPollingButton;

    @FXML
    public VBox sensorsMonitorVBox;
    @FXML
    public VBox actuatorsMonitorVBox;
    @FXML
    public VBox setpointsVBox;
    @FXML
    public ScrollPane ioMonitorScrollPane;

    ModbusUtilSingleton modbusUtilSingleton;
    List<Sensor> sensorsInScheme = new ArrayList<>();
    List<Actuator> actuatorsInScheme = new ArrayList<>();
    List<MonitorTextFlow> monitorTextFlowList = new ArrayList<>();

    public static ScheduledExecutorService executor;
    private boolean pollingPreviousState = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();

        startPollingButton.setOnAction(e -> {
            startPolling();
            pollingPreviousState = true;
        });
        stopPollingButton.setOnAction(e -> {
            stopPolling();
            pollingPreviousState = false;
        });

        ioMonitorScrollPane.sceneProperty().addListener((obs, oldVal, newVal) -> {
            // Событие на открытие окна
            if (newVal != null) {
                sensorsInScheme = ChangeSchemeController.sensorsInScheme;
                actuatorsInScheme = ChangeSchemeController.actuatorsInScheme;

                if (sensorsInScheme != null || actuatorsInScheme != null) {
                    if (pollingPreviousState) {
                        startPolling();
                    } else {
                        stopPolling();
                    }
                }

                sensorsMonitorVBox.getChildren().clear();
                monitorTextFlowList.clear();
                setpointsVBox.getChildren().clear();
                if (sensorsInScheme != null) {
                    for (Sensor sensor : sensorsInScheme) {
                        if (sensor.getIsUsedDefault()) {
                            if (sensor.getAttributeForMonitoring() != null) {
                                MonitorTextFlow monitorTextFlow = new MonitorTextFlow(sensor);
                                monitorTextFlowList.add(monitorTextFlow);
                                sensorsMonitorVBox.getChildren().add(monitorTextFlow.getTextFlow());
                            }
                            if (sensor.getAttributeForControlling() != null) {
                                SetpointSpinner setpointSpinner = new SetpointSpinner(sensor.getAttributeForControlling());
                                setpointsVBox.getChildren().add(setpointSpinner.getSpinner());
                            }
                        }
                    }
                }

                actuatorsMonitorVBox.getChildren().clear();
                if (actuatorsInScheme != null) {
                    for (Actuator actuator : actuatorsInScheme) {
                        if (actuator.getIsUsedDefault()) {
                            if (actuator.getAttributeForMonitoring() != null) {
                                MonitorTextFlow monitorTextFlow = new MonitorTextFlow(actuator);
                                monitorTextFlowList.add(monitorTextFlow);
                                actuatorsMonitorVBox.getChildren().add(monitorTextFlow.getTextFlow());
                            }
                        }
                    }
                }
            }

            // Событие на закрытие окна
            if (oldVal != null) {
                stopPolling();
            }
        });
    }

    private void startPolling() {
        ModbusMaster master = modbusUtilSingleton.getMaster();
        if (master != null) {
            System.out.println(Thread.currentThread().getName());
            stopPolling();

            executor = Executors.newSingleThreadScheduledExecutor();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    for (MonitorTextFlow monitorTextFlow : monitorTextFlowList) {
                        monitorTextFlow.update();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    System.out.println(Thread.currentThread());
                }
            };

            executor.scheduleWithFixedDelay(timerTask, 1, 500, TimeUnit.MILLISECONDS);
        }
    }

    private void stopPolling() {
        if (executor != null) {
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        }
    }
}
