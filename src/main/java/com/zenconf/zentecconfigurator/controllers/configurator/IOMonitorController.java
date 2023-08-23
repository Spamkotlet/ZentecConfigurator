package com.zenconf.zentecconfigurator.controllers.configurator;

import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.MainParameters;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.enums.Seasons;
import com.zenconf.zentecconfigurator.models.nodes.MonitorTextFlow;
import com.zenconf.zentecconfigurator.models.nodes.SetpointSpinner;
import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

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
    public VBox ioMonitorVBox;
    @FXML
    public Button startStopButton;
    @FXML
    public Button resetAlarmsButton;
    @FXML
    public ChoiceBox<String> controlModeChoiceBox;
    @FXML
    public ChoiceBox<String> seasonChoiceBox;
    @FXML
    public Label statusLabel;
    @FXML
    public AnchorPane statusAnchorPane;
    @FXML
    public SplitPane verticalSplitPane;
    @FXML
    public GridPane alarmsGridPane;

    ModbusUtilSingleton modbusUtilSingleton;
    List<Sensor> sensorsInScheme = new ArrayList<>();
    List<Actuator> actuatorsInScheme = new ArrayList<>();
    List<MonitorTextFlow> monitorTextFlowList = new ArrayList<>();
    private MainParameters mainParameters;

    public static ScheduledExecutorService executor;
    private boolean pollingPreviousState = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();

        initializationPollingElements();
        initializationPLCControlElements();
        ioMonitorVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
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

                initializationPLCMonitoringElements();
            }

            // Событие на закрытие окна
            if (oldVal != null) {
                stopPolling();
            }
        });

        statusLabel.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (!newVal.equals(oldVal)) {
                    FillTransition fillTransition = new FillTransition(Duration.seconds(1.0), statusAnchorPane.getShape());
                    fillTransition.setDelay(Duration.seconds(1.0));
                    fillTransition.setAutoReverse(true);
                    fillTransition.setFromValue(Color.GRAY);
                    fillTransition.setToValue(Color.RED);
                    fillTransition.setCycleCount(3);
                    fillTransition.play();
                }
            }
        });
    }

    private void startPolling() {
        ModbusMaster master = modbusUtilSingleton.getMaster();
        if (master != null) {

            stopPolling();

            executor = Executors.newSingleThreadScheduledExecutor();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    for (MonitorTextFlow monitorTextFlow : monitorTextFlowList) {
                        Platform.runLater(monitorTextFlow::update);
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    Platform.runLater(() -> updateStatusLabel());
                }
            };

            executor.scheduleWithFixedDelay(timerTask, 1, 500, TimeUnit.MILLISECONDS);
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Невозможно запустить опрос контроллера");
            alert.setContentText("- установите соединение с контроллером");
            alert.show();
        }
    }

    private void stopPolling() {
        if (executor != null) {
            if (!executor.isShutdown()) {
                executor.shutdown();
            }
        }
    }

    private void initializationPollingElements() {
        startPollingButton.setOnAction(e -> {
            startPolling();
            pollingPreviousState = true;
        });
        stopPollingButton.setOnAction(e -> {
            stopPolling();
            pollingPreviousState = false;
        });
    }

    private void initializationPLCControlElements() {
        mainParameters = MainController.mainParameters;

        startStopButton.setOnAction(e -> startStop());

        resetAlarmsButton.setOnAction(e -> {
            Attribute resetAlarmsAttribute = mainParameters.getResetAlarmsAttribute();
            resetAlarmsAttribute.writeModbusParameter(true);
        });

        Attribute controlModeAttribute = mainParameters.getControlModeAttribute();
        List<String> controlModeValues = mainParameters.getControlModeAttribute().getValues();
        controlModeChoiceBox.setItems(getChoiceBoxStringItems(controlModeValues));
        controlModeChoiceBox.setValue(
                getChoiceBoxStringItems(controlModeValues)
                        .get(Integer.parseInt(controlModeAttribute.readModbusParameter()))
        );
        controlModeChoiceBox.setOnAction(e -> {
            controlModeAttribute.writeModbusParameter(controlModeValues.indexOf(controlModeChoiceBox.getValue()));
            System.out.println("Index: " + controlModeValues.indexOf(controlModeChoiceBox.getValue()) + " Value: " + controlModeChoiceBox.getValue());
        });

        Attribute seasonAttribute = mainParameters.getSeasonAttribute();
        seasonChoiceBox.setItems(getSeasonsChoiceBoxItems());
        seasonChoiceBox.setValue(getSeasonsChoiceBoxItems()
                .get(Integer.parseInt(seasonAttribute.readModbusParameter()))
        );
        seasonChoiceBox.setOnAction(e -> {
            seasonAttribute.writeModbusParameter(Seasons.values()[seasonChoiceBox.getSelectionModel().getSelectedIndex()].getNumber());
            System.out.println("Index: " + seasonChoiceBox.getValue() + " Value: " + seasonChoiceBox.getValue());
        });
    }

    private void initializationPLCMonitoringElements() {
        monitorTextFlowList.clear();
        sensorsMonitorVBox.getChildren().clear();
        setpointsVBox.getChildren().clear();
        if (sensorsInScheme != null) {
            for (Sensor sensor : sensorsInScheme) {
                if (sensor.getIsUsedDefault()) {
                    if (sensor.getAttributeForControlling() != null) {
                        SetpointSpinner setpointSpinner = new SetpointSpinner(sensor.getAttributeForControlling());
                        setpointsVBox.getChildren().add(setpointSpinner.getSpinner());
                    }
                    if (sensor.getAttributeForMonitoring() != null) {
                        MonitorTextFlow monitorTextFlow = new MonitorTextFlow(verticalSplitPane, sensor);
                        monitorTextFlowList.add(monitorTextFlow);
                        sensorsMonitorVBox.getChildren().add(monitorTextFlow.getTextFlow());
                    }
                }
            }
        }

        actuatorsMonitorVBox.getChildren().clear();
        if (actuatorsInScheme != null) {
            for (Actuator actuator : actuatorsInScheme) {
                if (actuator.getIsUsedDefault()) {
                    if (actuator.getAttributeForControlling() != null) {
                        SetpointSpinner setpointSpinner = new SetpointSpinner(actuator.getAttributeForControlling());
                        setpointsVBox.getChildren().add(setpointSpinner.getSpinner());
                    }
                    if (actuator.getAttributeForMonitoring() != null) {
                        MonitorTextFlow monitorTextFlow = new MonitorTextFlow(verticalSplitPane, actuator);
                        monitorTextFlowList.add(monitorTextFlow);
                        actuatorsMonitorVBox.getChildren().add(monitorTextFlow.getTextFlow());
                    }
                }
            }
        }
    }

    private synchronized void updateStatusLabel() {
        List<String> statusList = mainParameters.getStatusAttribute().getValues();
        int statusNumber = Integer.parseInt(mainParameters.getStatusAttribute().readModbusParameter());
        statusLabel.setText(statusList.get(statusNumber));
    }

    private synchronized void startStop() {
        Attribute startStopAttribute = mainParameters.getStartStopAttribute();
        boolean startStopBoolean = Boolean.parseBoolean(startStopAttribute.readModbusParameter());
        if (startStopBoolean) {
            startStopAttribute.writeModbusParameter(startStopBoolean);
        } else {
            startStopAttribute.writeModbusParameter(!startStopBoolean);
        }
        startStopBoolean = Boolean.parseBoolean(startStopAttribute.readModbusParameter());
        startStopAttribute.writeModbusParameter(!startStopBoolean);
    }

    private boolean isAlarmActive() {
        boolean commonAlarm = Boolean.parseBoolean(mainParameters.getCommonAlarmAttribute().readModbusParameter());
        int warnings = Integer.parseInt(mainParameters.getWarningsAttribute().readModbusParameter());
        return commonAlarm || warnings > 0;
    }

    private ObservableList<String> getChoiceBoxStringItems(List<String> attributeValues) {
        return FXCollections.observableArrayList(attributeValues);
    }

    private ObservableList<String> getSeasonsChoiceBoxItems() {
        List<String> seasonsString = new ArrayList<>();
        List<Seasons> seasons = Arrays.asList(Seasons.values());
        for (int i = 0; i < seasons.size(); i++) {
            seasonsString.add(seasons.get(i).getDisplayValue());
        }
        return FXCollections.observableArrayList(seasonsString);
    }
}