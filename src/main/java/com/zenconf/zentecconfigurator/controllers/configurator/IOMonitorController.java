package com.zenconf.zentecconfigurator.controllers.configurator;

import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.MainParameters;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.enums.Seasons;
import com.zenconf.zentecconfigurator.models.nodes.AlarmTableView;
import com.zenconf.zentecconfigurator.models.nodes.MonitorTextFlow;
import com.zenconf.zentecconfigurator.models.nodes.MonitorValueText;
import com.zenconf.zentecconfigurator.models.nodes.SetpointSpinner;
import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.animation.FillTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
    public Button clearJournalButton;
    @FXML
    public ChoiceBox<String> controlModeChoiceBox;
    @FXML
    public ChoiceBox<String> seasonChoiceBox;
    @FXML
    public Label statusLabel;
    @FXML
    public Label seasonLabel;
    @FXML
    public AnchorPane statusAnchorPane;
    @FXML
    public SplitPane verticalSplitPane;
    @FXML
    public VBox alarmsVBox;
    @FXML
    public Circle pollingStatusIndicatorCircle;

    ModbusUtilSingleton modbusUtilSingleton;
    List<Sensor> sensorsInScheme = new ArrayList<>();
    List<Actuator> actuatorsInScheme = new ArrayList<>();
    List<MonitorValueText> monitorTextFlowList = new ArrayList<>();
    private MainParameters mainParameters;
    public AlarmTableView alarmsTableView;

    public static ScheduledExecutorService executor;
    private boolean pollingPreviousState = false;

    private long alarmsNumber0 = 0;
    private long alarmsNumber1 = 0;
    private long warningsNumber = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();

        initializationPollingElements();
        try {
            initializationPLCControlElements();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

                try {
                    initializationPLCMonitoringElements();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            // Событие на закрытие окна
            if (oldVal != null) {
                stopPolling();
            }
        });

        alarmsTableView = new AlarmTableView();
        alarmsVBox.getChildren().clear();
        alarmsVBox.getChildren().add(alarmsTableView.createAlarmGridPane());

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
        stopPolling();
        if (master != null) {

            pollingStatusIndicatorCircle.setFill(Color.GREEN);

            executor = Executors.newSingleThreadScheduledExecutor();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        for (MonitorValueText monitorTextFlow : monitorTextFlowList) {
                            monitorTextFlow.update();

                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        updateCurrentSeason();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                        boolean isAlarmActive = isAlarmActive();
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        if (isAlarmActive) {
                            alarmsTableView.updateJournal();
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        updateStatusLabel();
                    } catch (Exception e) {
                        System.out.println("Stop polling timer 1");
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Опрос контроллера завершился ошибкой");
                            alert.setContentText("- установите соединение с контроллером");
                            alert.show();
                        });
                        pollingPreviousState = false;
                        stopPolling();
                    }
                }
            };

            executor.scheduleWithFixedDelay(timerTask, 1, 500, TimeUnit.MILLISECONDS);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
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
        pollingStatusIndicatorCircle.setFill(new Color(0.831, 0.831, 0.831, 1f));
    }

    private void initializationPollingElements() {
        Image startPollingImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/iomonitor/play.png")));
        ImageView startPollingImageView = new ImageView(startPollingImage);
        startPollingImageView.setFitHeight(35);
        startPollingImageView.setFitWidth(35);
        startPollingButton.graphicProperty().setValue(startPollingImageView);
        startPollingButton.setText("");
        startPollingButton.getStyleClass().add("button-iomonitor-toolbar");
        startPollingButton.setOnAction(e -> {
            startPolling();
            pollingPreviousState = true;
        });

        Image stopPollingImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/iomonitor/stop.png")));
        ImageView stopPollingImageView = new ImageView(stopPollingImage);
        stopPollingImageView.setFitHeight(35);
        stopPollingImageView.setFitWidth(35);
        stopPollingButton.graphicProperty().setValue(stopPollingImageView);
        stopPollingButton.setText("");
        stopPollingButton.getStyleClass().add("button-iomonitor-toolbar");
        stopPollingButton.setOnAction(e -> {
            stopPolling();
            pollingPreviousState = false;
        });
    }

    // Инициализация элементов управления контроллером
    private void initializationPLCControlElements() throws Exception {
        mainParameters = MainController.mainParameters;

        startStopButton.setOnAction(e -> {
            try {
                startStop();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
        resetAlarmsButton.setOnAction(this::onResetAlarmsButton);
        clearJournalButton.setOnAction(this::onClearJournalButton);

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
                .get(getCurrentSeasonNumber(seasonAttribute))
        );
        seasonChoiceBox.setOnAction(e -> {
            try {
                int seasonNumber = Math.max(Seasons.values()[seasonChoiceBox.getSelectionModel().getSelectedIndex()].getNumber(), 0);
                seasonAttribute.writeModbusParameter(seasonNumber);
                System.out.println("Index: " + seasonChoiceBox.getValue() + " Value: " + seasonChoiceBox.getValue());
            } catch (ArrayIndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
        });
    }

    // Инициализация элементов мониторинга состояния контроллера
    private void initializationPLCMonitoringElements() throws Exception {
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
                        monitorTextFlowList.add(monitorTextFlow.getValueText());
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
                        monitorTextFlowList.add(monitorTextFlow.getValueText());
                        actuatorsMonitorVBox.getChildren().add(monitorTextFlow.getTextFlow());
                    }
                }
            }
        }
    }

    private synchronized void updateCurrentSeason() throws Exception {
        Attribute seasonAttribute = mainParameters.getSeasonAttribute();
        String binaryString = Integer.toBinaryString(Integer.parseInt(seasonAttribute.readModbusParameter()));
        char[] seasonBitsCharArray = String.format("%4s", binaryString).replace(' ', '0').toCharArray();
        String seasonText;
        if (seasonBitsCharArray[0] == '1') {
            if (seasonBitsCharArray[3] == '1') {
                seasonText = "В (А)";
            } else if (seasonBitsCharArray[2] == '1') {
                seasonText = "Н (А)";
            } else if (seasonBitsCharArray[1] == '1') {
                seasonText = "О (А)";
            } else {
                seasonText = "";
            }
        } else {
            if (seasonBitsCharArray[3] == '1') {
                seasonText = "В";
            } else if (seasonBitsCharArray[2] == '1') {
                seasonText = "Н";
            } else if (seasonBitsCharArray[1] == '1') {
                seasonText = "О";
            } else {
                seasonText = "";
            }
        }
        Platform.runLater(() -> seasonLabel.setText(seasonText));
    }

    private int getCurrentSeasonNumber(Attribute seasonAttribute) throws Exception {
        int number = Integer.parseInt(seasonAttribute.readModbusParameter());
        int currentSeasonNumber = 0;
        if (number < 8) {
            switch (number) {
                case 2 -> currentSeasonNumber = 1;
                case 4 -> currentSeasonNumber = 2;
            }
        } else {
            currentSeasonNumber = 3;
        }
        return currentSeasonNumber;
    }

    private synchronized void updateStatusLabel() throws Exception {
        List<String> statusList = mainParameters.getStatusAttribute().getValues();
        int statusNumber = Integer.parseInt(mainParameters.getStatusAttribute().readModbusParameter());
        Platform.runLater(() -> statusLabel.setText(statusList.get(statusNumber)));
    }

    private synchronized void startStop() throws Exception {
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

    private boolean isAlarmActive() throws Exception {
        long alarms0 = Long.parseLong(mainParameters.getAlarmsAttribute0().readModbusParameter());
        long alarms1 = Long.parseLong(mainParameters.getAlarmsAttribute1().readModbusParameter());
        long warnings = Long.parseLong(mainParameters.getWarningsAttribute().readModbusParameter());

        boolean isAlarmActive = warnings != warningsNumber || alarms0 != alarmsNumber0 || alarms1 != alarmsNumber1;

        alarmsNumber0 = alarms0;
        alarmsNumber1 = alarms1;
        warningsNumber = warnings;

        return isAlarmActive;
    }

    private void onClearJournalButton(ActionEvent event) {
        Attribute clearJournalAttribute = mainParameters.getClearJournalAttribute();
        clearJournalAttribute.writeModbusParameter(true);
        alarmsTableView.clearJournal();
        onResetAlarmsButton(event);
    }

    private void onResetAlarmsButton(ActionEvent event) {
        Attribute resetAlarmsAttribute = mainParameters.getResetAlarmsAttribute();
        resetAlarmsAttribute.writeModbusParameter(true);
        alarmsTableView.resetAlarms();
    }

    private ObservableList<String> getChoiceBoxStringItems(List<String> attributeValues) {
        return FXCollections.observableArrayList(attributeValues);
    }

    private ObservableList<String> getSeasonsChoiceBoxItems() {
        List<String> seasonsString = new ArrayList<>();
        Seasons[] seasons = Seasons.values();
        for (int i = 0; i < 4; i++) {
            seasonsString.add(seasons[i].getDisplayValue());
        }
        return FXCollections.observableArrayList(seasonsString);
    }
}
