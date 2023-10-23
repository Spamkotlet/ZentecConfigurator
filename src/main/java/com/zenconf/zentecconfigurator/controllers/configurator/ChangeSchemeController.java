package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.ConfiguratorController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.elements.Actuator;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.elements.Sensor;
import com.zenconf.zentecconfigurator.models.enums.Controls;
import com.zenconf.zentecconfigurator.models.nodes.SchemeTitledPane;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class ChangeSchemeController extends CommonController implements Initializable {

    @FXML
    public ChoiceBox<String> schemeNumberChoiceBox;
    @FXML
    public VBox actuatorsVbox;
    @FXML
    public VBox sensorsVbox;
    @FXML
    public VBox changeSchemeVBox;

    private List<Scheme> schemes;
    public static Scheme selectedScheme = null;
    private Scheme previousScheme = null;
    public static List<Sensor> sensorsInScheme;
    public static List<Actuator> actuatorsInScheme;
    private final HashMap<String, SchemeTitledPane> actuatorsTitledPaneHashMap = new HashMap<>();
    private final HashMap<String, SchemeTitledPane> sensorsTitledPaneHashMap = new HashMap<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        changeSchemeVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    ObservableList<String> schemesItems = getSchemeItems(schemes);
                    onActionSchemeNumberChoiceBox();
                    schemeNumberChoiceBox.setValue(schemesItems.get(selectedScheme.getNumber()));
                    logger.info("Выбрана схема " + selectedScheme.getNumber() + " / " + selectedScheme.getName());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        });

        try {
            onOpenedChangeSchemeVBox();
        } catch (Exception e) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Ошибка");
                alert.setHeaderText("Невозможно выполнить операцию");
                alert.setContentText("- установите соединение с контроллером, или повторите ещё раз");
                alert.show();
            });
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

//        schemeNumberChoiceBox.setOnMouseClicked(e -> {
            schemeNumberChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (!Objects.equals(newVal, oldVal)) {
                    ConfiguratorController.resetAttributesToDefault();
                    if (ConfiguratorController.resetAttributesToDefaultTask != null) {
                        ConfiguratorController.resetAttributesToDefaultTask.setOnSucceeded(event -> {
                            try {
                                onActionSchemeNumberChoiceBox();
                            } catch (Exception ex) {
                                Platform.runLater(() -> {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setTitle("Ошибка");
                                    alert.setHeaderText("Невозможно выполнить операцию");
                                    alert.setContentText("- установите соединение с контроллером, или повторите ещё раз");
                                    alert.show();
                                });
                                logger.error(ex.getMessage());
                                throw new RuntimeException(ex);
                            }
                        });
                    }
                }
            });
//        });
    }

    // Что происходит при открытии экрана
    protected void onOpenedChangeSchemeVBox() throws Exception {
        // Получаем все возможные схемы
        schemes = MainController.schemes;

        // Получаем список схем для Choice Box
        ObservableList<String> schemesItems = getSchemeItems(schemes);
        schemeNumberChoiceBox.setItems(schemesItems);

        // Получаем текущую выбранную схему и пихаем в Choice Box
        selectedScheme = schemes.get(readSchemeNumberFromModbus());
        schemeNumberChoiceBox.setValue(schemesItems.get(selectedScheme.getNumber()));
    }

    // Что происходит при выборе Choice Box
    private void onActionSchemeNumberChoiceBox() throws Exception {
        System.out.println("onActionSchemeNumberChoiceBox");

        // Получаем имя схемы для дальнейшего сравнения
        String selectedSchemeName = "";
        if (previousScheme != null) {
            selectedSchemeName = previousScheme.getNumber() + 1 + " - " + previousScheme.getName();
        }

        // Если выбранная схема не совпадает текущей
        if (!schemeNumberChoiceBox.getValue().equals(selectedSchemeName)) {
            System.out.println("Выбрана другая схема");
            if (!ActuatorsController.actuatorsUsed.isEmpty()) {
                ActuatorsController.actuatorsUsed.clear();
            }
            if (!SensorsController.sensorsUsed.isEmpty()) {
                SensorsController.sensorsUsed.clear();
            }
            previousScheme = schemes.get(schemeNumberChoiceBox.getSelectionModel().getSelectedIndex());

            onSelectedSchemeNumber();
        }
    }

    // Что происходит при выборе схемы из списка
    protected void onSelectedSchemeNumber() throws Exception {
        // Получить схему, которая была выбрана в Choice Box
        selectedScheme = schemes.get(schemeNumberChoiceBox.getSelectionModel().getSelectedIndex());

        // Получить все датчики и исполнительные устройства, которые есть в схеме
        sensorsInScheme = selectedScheme.getSensors();
        actuatorsInScheme = selectedScheme.getActuators();

        // Создаем списки для устройств, которые используются
        ConfiguratorController.sensorsUsed = new ArrayList<>();
        ConfiguratorController.actuatorsUsed = new ArrayList<>();

        for (Actuator actuatorInScheme : actuatorsInScheme) {
            for (Actuator actuator : ConfiguratorController.actuatorsList) {
                // Копирование свойств элементов схемы
                if (actuatorInScheme.getName().equals(actuator.getName())) {
                    actuatorInScheme.setFunctionalities(actuator.getFunctionalities());
                    actuatorInScheme.setIsInWorkAttribute(actuator.getIsInWorkAttribute());
                    actuatorInScheme.setAttributes(actuator.getAttributes());
                    actuatorInScheme.setAttributeForMonitoring(actuator.getAttributeForMonitoring());
                    actuatorInScheme.setAttributesForControlling(actuator.getAttributesForControlling());
                    if (actuatorInScheme.getIsUsedDefault()) {
                        ConfiguratorController.actuatorsUsed.add(actuatorInScheme);
                    }
                }
            }
        }

        for (Sensor sensorInScheme : sensorsInScheme) {
            for (Sensor sensor : ConfiguratorController.sensorsList) {
                // Копирование свойств элементов схемы
                if (sensorInScheme.getName().equals(sensor.getName())) {
                    sensorInScheme.setIsInWorkAttribute(sensor.getIsInWorkAttribute());
                    sensorInScheme.setAttributes(sensor.getAttributes());
                    sensorInScheme.setAttributeForMonitoring(sensor.getAttributeForMonitoring());
                    sensorInScheme.setAttributesForControlling(sensor.getAttributesForControlling());
                    if (sensorInScheme.getIsUsedDefault()) {
                        ConfiguratorController.sensorsUsed.add(sensorInScheme);
                    }
                }
            }
        }
        fillingPane();

        // Запись номера схемы по Modbus
        writeSchemeNumberByModbus(selectedScheme.getNumber());
    }


    // Заполнение панели устройствами и датчиками
    private void fillingPane() throws Exception {
        // Задача по наполнению экрана
        Task<Void> loadActuatorsAndSensorsTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    showLoadWindow(this);

                    actuatorsVbox.getChildren().clear();
                    sensorsVbox.getChildren().clear();
                });

                List<Actuator> actuators = actuatorsInScheme;
                int actuatorsDone = 0;
                int actuatorsMax = actuators.size();
                for (Actuator actuator : actuators) {
                    actuatorsDone++;
                    updateMessage("Загрузка устройств: " + actuatorsDone + "/" + actuatorsMax);
                    updateProgress(actuatorsDone, actuatorsMax);

                    SchemeTitledPane actuatorTitledPane;
                    if (actuatorsTitledPaneHashMap.get(actuator.getName()) == null) {
                        actuatorTitledPane = new SchemeTitledPane(actuator);
                        actuatorsTitledPaneHashMap.put(actuator.getName(), actuatorTitledPane);
                    } else {
                        actuatorTitledPane = actuatorsTitledPaneHashMap.get(actuator.getName());
                        actuatorTitledPane.setElement(actuator);
                    }

                    try {
                        Platform.runLater(() -> actuatorsVbox.getChildren().add(actuatorTitledPane));
                        actuatorTitledPane.setAttributeIsUsedDefault();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }

                    Thread.sleep(100);
                }

                List<Sensor> sensors = schemes.get(selectedScheme.getNumber()).getSensors();
                int sensorsDone = 0;
                int sensorsMax = sensors.size();
                for (Sensor sensor : schemes.get(selectedScheme.getNumber()).getSensors()) {
                    sensorsDone++;
                    updateMessage("Загрузка датчиков: " + sensorsDone + "/" + sensorsMax);
                    updateProgress(sensorsDone, sensorsMax);

                    SchemeTitledPane sensorTitledPane;
                    if (sensorsTitledPaneHashMap.get(sensor.getName()) == null) {
                        sensorTitledPane = new SchemeTitledPane(sensor);
                        sensorsTitledPaneHashMap.put(sensor.getName(), sensorTitledPane);
                    } else {
                        sensorTitledPane = sensorsTitledPaneHashMap.get(sensor.getName());
                        sensorTitledPane.setElement(sensor);
                    }

                    try {
                        Platform.runLater(() -> sensorsVbox.getChildren().add(sensorTitledPane));
                        sensorTitledPane.setAttributeIsUsedDefault();
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw e;
                    }

                    Thread.sleep(100);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                logger.info("Задача loadActuatorsAndSensorsTask выполнена успешно");
                Platform.runLater(() -> closeLoadWindow(this));
                Thread.currentThread().interrupt();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                logger.info("Задача loadActuatorsAndSensorsTask прервана");
                Thread.currentThread().interrupt();
            }

            @Override
            protected void failed() {
                super.failed();
                this.getException().printStackTrace();
                logger.info("Задача loadActuatorsAndSensorsTask завершилась ошибкой");
                logger.error(this.getException().getMessage());
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Невозможно выполнить операцию");
                    alert.setContentText("- установите соединение с контроллером, или повторите ещё раз");
                    alert.show();
                    closeLoadWindow(this);
                });
                Thread.currentThread().interrupt();
            }
        };
        Thread loadActuatorsAndSensorsThread = new Thread(loadActuatorsAndSensorsTask);

        // Задача по установке всех чекбоксов в исходное положение
        Task<Void> setAttributeIsUsedDefaultTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> showLoadWindow(this));

                // Установка всех чекбоксов "Используется" в положение false
                boolean isSuccessfulAction = true;
                int successfulActionAttempt = 0;
                Actuator actuator = null;
                for (int i = 0; i < ConfiguratorController.actuatorsList.size(); ) {
                    if (isSuccessfulAction) {
                        updateMessage("Загрузка...");
                        actuator = ConfiguratorController.actuatorsList.get(i);
                    }

                    try {
                        if (actuator != null) {
                            if (actuator.getIsInWorkAttribute() != null) {
                                if (actuator.getIsInWorkAttribute().getControl() == Controls.CHECKBOX) {
                                    actuator.getIsInWorkAttribute().writeModbus(false);
                                } else if (actuator.getIsInWorkAttribute().getControl() == Controls.CHOICE_BOX) {
                                    actuator.getIsInWorkAttribute().writeModbus(0);
                                }
                            }
                            isSuccessfulAction = true;
                            i++;
                        }
                    } catch (Exception e) {
                        isSuccessfulAction = false;
                        successfulActionAttempt++;
                        updateMessage("Попытка загрузки [" + successfulActionAttempt + "/3]\n" + actuator.getName());
                        if (successfulActionAttempt >= 3) {
                            isSuccessfulAction = true;
                            successfulActionAttempt = 0;
                            i++;
                        }
                        Thread.sleep(1000);
                    }
                }

                isSuccessfulAction = true;
                successfulActionAttempt = 0;
                Sensor sensor = null;
                for (int i = 0; i < ConfiguratorController.sensorsList.size(); ) {
                    if (isSuccessfulAction) {
                        updateMessage("Загрузка...");
                        sensor = ConfiguratorController.sensorsList.get(i);
                    }

                    try {
                        if (sensor != null) {
                            if (sensor.getIsInWorkAttribute() != null) {
                                if (sensor.getIsInWorkAttribute().getControl() == Controls.CHECKBOX) {
                                    sensor.getIsInWorkAttribute().writeModbus(false);
                                } else if (sensor.getIsInWorkAttribute().getControl() == Controls.CHOICE_BOX) {
                                    sensor.getIsInWorkAttribute().writeModbus(0);
                                }
                            }
                            isSuccessfulAction = true;
                            i++;
                        }
                    } catch (Exception e) {
                        isSuccessfulAction = false;
                        successfulActionAttempt++;
                        updateMessage("Попытка загрузки [" + successfulActionAttempt + "/3]\n" + sensor.getName());
                        if (successfulActionAttempt >= 3) {
                            isSuccessfulAction = true;
                            successfulActionAttempt = 0;
                            i++;
                        }
                        Thread.sleep(1000);
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                logger.info("Задача setAttributeIsUsedOffTask выполнена успешно");
                closeLoadWindow(this);
                Thread.currentThread().interrupt();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                logger.info("Задача setAttributeIsUsedOffTask прервана");
                Thread.currentThread().interrupt();
            }

            @Override
            protected void failed() {
                this.getException().printStackTrace();
                super.failed();
                logger.info("Задача setAttributeIsUsedOffTask завершилась ошибкой");
                logger.error(this.getException().getMessage());
                closeLoadWindow(this);
                Thread.currentThread().interrupt();
            }
        };
        Thread setAttributeIsUsedDefaultThread = new Thread(setAttributeIsUsedDefaultTask);
        setAttributeIsUsedDefaultThread.start();
        setAttributeIsUsedDefaultTask.setOnSucceeded(e -> loadActuatorsAndSensorsThread.start());
    }

    // Запись номера схемы в контроллер по Modbus
    private void writeSchemeNumberByModbus(int schemeNumber) throws Exception {
        MainController.mainParameters.getSchemeNumberAttribute().writeModbus(schemeNumber);
    }

    // Чтение номера схемы из контроллера по Modbus
    private int readSchemeNumberFromModbus() throws Exception {
        return Integer.parseInt(MainController.mainParameters.getSchemeNumberAttribute().readModbus());
    }

    // Получить список схем для помещения в schemeNumberChoiceBox
    private ObservableList<String> getSchemeItems(List<Scheme> schemes) {
        List<String> schemeStrings = new ArrayList<>();
        for (Scheme scheme : schemes) {
            schemeStrings.add(scheme.getNumber() + 1 + " - " + scheme.getName());
        }
        return FXCollections.observableArrayList(schemeStrings);
    }
}
