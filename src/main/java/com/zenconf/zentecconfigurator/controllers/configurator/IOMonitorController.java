package com.zenconf.zentecconfigurator.controllers.configurator;

import com.intelligt.modbus.jlibmodbus.exception.ModbusIOException;
import com.intelligt.modbus.jlibmodbus.master.ModbusMaster;
import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.enums.Seasons;
import com.zenconf.zentecconfigurator.models.nodes.AlarmTableView;
import com.zenconf.zentecconfigurator.models.nodes.MonitorTextFlow;
import com.zenconf.zentecconfigurator.models.nodes.MonitorValueText;
import com.zenconf.zentecconfigurator.models.nodes.SetpointSpinner;
import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class IOMonitorController extends CommonController implements Initializable {

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
    List<MonitorValueText> monitorValueTextList = new ArrayList<>();
    public AlarmTableView alarmsTableView;

    public static ScheduledExecutorService executor;
    private boolean pollingPreviousState = false;

    private long alarmsNumber0 = 0;
    private long alarmsNumber1 = 0;
    private long warningsNumber = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();

        try {
            initializationPollingElements();
            initializationPLCControlElements();
        } catch (Exception e) {
            logger.error(e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText("Невозможно выполнить операцию");
            alert.setContentText("- установите соединение с контроллером, или повторите ещё раз");
            alert.show();
            throw new RuntimeException(e);
        }
        ioMonitorVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            // Событие на открытие окна
            if (newVal != null) {

                if (ChangeSchemeController.sensorsUsed != null || ChangeSchemeController.actuatorsUsed != null) {
                    if (pollingPreviousState) {
                        startPolling();
                    } else {
                        stopPolling();
                    }
                }

                try {
                    initializationPLCMonitoringElements();
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    Platform.runLater(() -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Ошибка");
                        alert.setHeaderText("Невозможно выполнить операцию");
                        alert.setContentText("- установите соединение с контроллером, или повторите ещё раз");
                        alert.show();
                    });
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
    }

    private void startPolling() {
        ModbusMaster master = modbusUtilSingleton.getMaster();
        stopPolling();
        if (master != null) {
            logger.info("Старт опроса");
            final int[] errorCounter = {0};
            final int[] cycleCounter = {0};
            int cycleNumber = 10;
            int errorNumber = 5;
            pollingStatusIndicatorCircle.setFill(Color.GREEN);

            executor = Executors.newSingleThreadScheduledExecutor();
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        try {
                            for (MonitorValueText monitorValueText : monitorValueTextList) {
                                monitorValueText.update();

                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }

                            updateCurrentSeason();

                            boolean isAlarmActive = isAlarmActive();
                            if (isAlarmActive) {
                                alarmsTableView.updateJournal();
                            }

                            updateStatusLabel();

                            cycleCounter[0]++;
                            if (cycleCounter[0] >= cycleNumber) {
                                errorCounter[0] = 0;
                                cycleCounter[0] = 0;
                            }
                        } catch (Exception e) {
                            errorCounter[0]++;
                            if (errorCounter[0] >= errorNumber) {
                                cycleCounter[0] = 0;
                                errorCounter[0] = 0;
                                logger.error("Достигнут лимит ошибок во время опроса");
                                logger.error(e.getMessage());
                                throw e;
                            }
                            logger.warn("Количество ошибок во время опроса: " + errorCounter[0] + "/" + errorNumber);
                        }
                    } catch (Exception e) {
                        System.out.println("Stop polling timer 1");
                        Platform.runLater(() -> {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Ошибка");
                            alert.setHeaderText("Опрос контроллера завершился ошибкой");
                            alert.setContentText("- установите соединение с контроллером");
                            alert.show();
                        });
                        logger.error(e.getMessage());
                        pollingPreviousState = false;
                        stopPolling();
                        try {
                            modbusUtilSingleton.disconnect();
                        } catch (ModbusIOException ex) {
                            throw new RuntimeException(ex);
                        }
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
            logger.error("Невозможно запустить опрос контроллера - установите соединение с контроллером");
        }
    }

    private void stopPolling() {
        logger.info("Стоп опроса");
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
        logger.info("Инициализация элементов управления контроллером");

        startStopButton.setOnAction(e -> {
            try {
                startStop();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new RuntimeException(ex);
            }
        });
        resetAlarmsButton.setOnAction(e -> {
            try {
                onResetAlarmsButton();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new RuntimeException(ex);
            }
        });
        clearJournalButton.setOnAction(e -> {
            try {
                onClearJournalButton();
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new RuntimeException(ex);
            }
        });

        Attribute controlModeAttribute = MainController.mainParameters.getControlModeAttribute();
        List<String> controlModeValues = MainController.mainParameters.getControlModeAttribute().getValues();
        controlModeChoiceBox.setItems(getChoiceBoxStringItems(controlModeValues));
        controlModeChoiceBox.setValue(
                getChoiceBoxStringItems(controlModeValues)
                        .get(Integer.parseInt(controlModeAttribute.readModbus()))
        );
        controlModeChoiceBox.setOnAction(e -> {
            try {
                controlModeAttribute.writeModbus(controlModeValues.indexOf(controlModeChoiceBox.getValue()));
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new RuntimeException(ex);
            }
            System.out.println("Index: " + controlModeValues.indexOf(controlModeChoiceBox.getValue()) + " Value: " + controlModeChoiceBox.getValue());
        });

        Attribute seasonAttribute = MainController.mainParameters.getSeasonAttribute();
        seasonChoiceBox.setItems(getSeasonsChoiceBoxItems());
        seasonChoiceBox.setValue(getSeasonsChoiceBoxItems()
                .get(getCurrentSeasonNumber(seasonAttribute))
        );
        seasonChoiceBox.setOnAction(e -> {
            try {
                int seasonNumber = Math.max(Seasons.values()[seasonChoiceBox.getSelectionModel().getSelectedIndex()].getNumber(), 0);
                seasonAttribute.writeModbus(seasonNumber);
                System.out.println("Index: " + seasonChoiceBox.getValue() + " Value: " + seasonChoiceBox.getValue());
            } catch (Exception ex) {
                logger.error(ex.getMessage());
                throw new RuntimeException(ex);
            }
        });
    }

    // Инициализация элементов мониторинга состояния контроллера
    private void initializationPLCMonitoringElements() throws Exception {
        logger.info("Инициализация элементов мониторинга состояния контроллера");
        monitorValueTextList.clear();
        sensorsMonitorVBox.getChildren().clear();
        setpointsVBox.getChildren().clear();
        if (ChangeSchemeController.sensorsUsed != null) {
            for (Sensor sensor : ChangeSchemeController.sensorsUsed) {
                if (sensor.getAttributesForControlling() != null) {
                    List<Attribute> attributesForControlling = sensor.getAttributesForControlling();
                    for (Attribute attribute : attributesForControlling) {
                        SetpointSpinner setpointSpinner = new SetpointSpinner(attribute);
                        setpointsVBox.getChildren().add(setpointSpinner.getSpinner());
                    }
                }
                if (sensor.getAttributeForMonitoring() != null) {
                    MonitorTextFlow monitorTextFlow = new MonitorTextFlow(verticalSplitPane, sensor);
                    monitorValueTextList.add(monitorTextFlow.getValueText());
                    sensorsMonitorVBox.getChildren().add(monitorTextFlow.getTextFlow());
                }
            }
        }

        actuatorsMonitorVBox.getChildren().clear();
        if (ChangeSchemeController.actuatorsUsed != null) {
            for (Actuator actuator : ChangeSchemeController.actuatorsUsed) {
                if (actuator.getAttributesForControlling() != null) {
                    List<Attribute> attributesForControlling = actuator.getAttributesForControlling();
                    for (Attribute attribute : attributesForControlling) {
                        SetpointSpinner setpointSpinner = new SetpointSpinner(attribute);
                        setpointsVBox.getChildren().add(setpointSpinner.getSpinner());
                    }
                }
                if (actuator.getAttributeForMonitoring() != null) {
                    MonitorTextFlow monitorTextFlow = new MonitorTextFlow(verticalSplitPane, actuator);
                    monitorValueTextList.add(monitorTextFlow.getValueText());
                    actuatorsMonitorVBox.getChildren().add(monitorTextFlow.getTextFlow());
                }
            }
        }
    }

    private synchronized void updateCurrentSeason() throws Exception {
        Attribute seasonAttribute = MainController.mainParameters.getSeasonAttribute();
        String binaryString = Integer.toBinaryString(Integer.parseInt(seasonAttribute.readModbus()));
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
        int number = Integer.parseInt(seasonAttribute.readModbus());
        int currentSeasonNumber = 0;
        if (number < 8) {
            switch (number) {
                case 2:
                    currentSeasonNumber = 1;
                    break;
                case 4:
                    currentSeasonNumber = 2;
                    break;
            }
        } else {
            currentSeasonNumber = 3;
        }
        return currentSeasonNumber;
    }

    private synchronized void updateStatusLabel() throws Exception {
        List<String> statusList = MainController.mainParameters.getStatusAttribute().getValues();
        int statusNumber = Integer.parseInt(MainController.mainParameters.getStatusAttribute().readModbus());
        Platform.runLater(() -> statusLabel.setText(statusList.get(statusNumber)));
    }

    private synchronized void startStop() throws Exception {
        logger.info("Нажата кнопка <ПУСК/СТОП>");
        Attribute startStopAttribute = MainController.mainParameters.getStartStopAttribute();
        boolean startStopBoolean = Boolean.parseBoolean(startStopAttribute.readModbus());
        if (startStopBoolean) {
            startStopAttribute.writeModbus(startStopBoolean);
        } else {
            startStopAttribute.writeModbus(!startStopBoolean);
        }
        startStopBoolean = Boolean.parseBoolean(startStopAttribute.readModbus());
        startStopAttribute.writeModbus(!startStopBoolean);
    }

    private boolean isAlarmActive() throws Exception {
        long alarms0 = Long.parseLong(MainController.mainParameters.getAlarmsAttribute0().readModbus());
        long alarms1 = Long.parseLong(MainController.mainParameters.getAlarmsAttribute1().readModbus());
        long warnings = Long.parseLong(MainController.mainParameters.getWarningsAttribute().readModbus());

        boolean isAlarmActive = warnings != warningsNumber || alarms0 != alarmsNumber0 || alarms1 != alarmsNumber1;

        alarmsNumber0 = alarms0;
        alarmsNumber1 = alarms1;
        warningsNumber = warnings;

        return isAlarmActive;
    }

    private void onClearJournalButton() throws Exception {
        logger.info("Очистка журнала");
        Attribute clearJournalAttribute = MainController.mainParameters.getClearJournalAttribute();
        clearJournalAttribute.writeModbus(true);
        alarmsTableView.clearJournal();
        onResetAlarmsButton();
    }

    private void onResetAlarmsButton() throws Exception {
        logger.info("Сброс аварий");
        Attribute resetAlarmsAttribute = MainController.mainParameters.getResetAlarmsAttribute();
        resetAlarmsAttribute.writeModbus(true);
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
