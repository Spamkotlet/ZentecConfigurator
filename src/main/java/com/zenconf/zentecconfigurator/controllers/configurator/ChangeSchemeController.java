package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.nodes.SchemeTitledPane;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
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
    public static List<Sensor> sensorsUsed = new ArrayList<>();
    public static List<Actuator> actuatorsUsed = new ArrayList<>();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        changeSchemeVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                try {
                    ObservableList<String> schemesItems = getSchemesForSchemeNumberChoiceBox(schemes);
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

        schemeNumberChoiceBox.setOnMouseClicked(e -> {
            schemeNumberChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (!Objects.equals(newVal, oldVal)) {
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
                }
            });
        });
    }

    // Что происходит при открытии экрана
    protected void onOpenedChangeSchemeVBox() throws Exception {
        schemes = MainController.schemes;

        ObservableList<String> schemesItems = getSchemesForSchemeNumberChoiceBox(schemes);

        schemeNumberChoiceBox.setItems(schemesItems);
        selectedScheme = schemes.get(readSchemeNumberFromModbus());
        schemeNumberChoiceBox.setValue(schemesItems.get(selectedScheme.getNumber()));
    }

    private void onActionSchemeNumberChoiceBox() throws Exception {
        selectedScheme = schemes.get(readSchemeNumberFromModbus());
        String selectedSchemeName = "";
        if (previousScheme != null) {
            selectedSchemeName = previousScheme.getNumber() + 1 + " - " + previousScheme.getName();
        }
        if (!schemeNumberChoiceBox.getValue().equals(selectedSchemeName)) {
            previousScheme = schemes.get(schemeNumberChoiceBox.getSelectionModel().getSelectedIndex());
            onSelectedSchemeNumber();
        }
    }

    // Что происходит при выборе схемы из списка
    protected void onSelectedSchemeNumber() throws Exception {

        selectedScheme = schemes.get(schemeNumberChoiceBox.getSelectionModel().getSelectedIndex());
        sensorsInScheme = selectedScheme.getSensors();
        actuatorsInScheme = selectedScheme.getActuators();
        sensorsUsed = new ArrayList<>();
        actuatorsUsed = new ArrayList<>();

        for (Actuator actuatorInScheme : actuatorsInScheme) {
            for (Actuator actuator : MainController.actuatorList) {
                if (actuatorInScheme.getName().equals(actuator.getName())) {
                    if (actuatorInScheme.getIsUsedDefault()) {
                        actuatorsUsed.add(actuatorInScheme);
                    }
                    actuatorInScheme.setAttributes(actuator.getAttributes());
                    actuatorInScheme.setAttributeForMonitoring(actuator.getAttributeForMonitoring());
                    actuatorInScheme.setAttributesForControlling(actuator.getAttributesForControlling());
                }
            }
        }

        for (Sensor sensorInScheme : sensorsInScheme) {
            for (Sensor sensor : MainController.sensorList) {
                if (sensorInScheme.getName().equals(sensor.getName())) {
                    if (sensorInScheme.getIsUsedDefault()) {
                        sensorsUsed.add(sensorInScheme);
                    }
                    sensorInScheme.setAttributes(sensor.getAttributes());
                    sensorInScheme.setAttributeForMonitoring(sensor.getAttributeForMonitoring());
                    sensorInScheme.setAttributesForControlling(sensor.getAttributesForControlling());
                }
            }
        }
        fillingPane();
        writeSchemeNumberByModbus(selectedScheme.getNumber());
    }


    // Заполнение панели устройствами и датчиками
    private void fillingPane() throws Exception {

        System.out.println("fillingPane");

        // Задача по установке всех чекбоксов в исходное положение
        Task<Void> setAttributeIsUsedOffTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> showLoadWindow(this));

                updateMessage("Загрузка...");
                // Установка всех чекбоксов "Используется" в положение false
                for (Iterator<Node> iterator = actuatorsVbox.getChildren().iterator(); iterator.hasNext(); ) {
                    Node node = iterator.next();
                    if (node instanceof SchemeTitledPane) {
                        ((SchemeTitledPane) node).setAttributeIsUsedOff();
                    }
                }

                for (Iterator<Node> iterator = sensorsVbox.getChildren().iterator(); iterator.hasNext(); ) {
                    Node node = iterator.next();
                    if (node instanceof SchemeTitledPane) {
                        ((SchemeTitledPane) node).setAttributeIsUsedOff();
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                System.out.println("Задача setAttributeIsUsedOffTask выполнена успешно");
                Platform.runLater(() -> closeLoadWindow(this));
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                System.out.println("Задача setAttributeIsUsedOffTask прервана");
            }

            @Override
            protected void failed() {
                super.failed();
                System.out.println("Задача setAttributeIsUsedOffTask завершилась ошибкой");
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Невозможно выполнить операцию");
                    alert.setContentText("- установите соединение с контроллером, или повторите ещё раз");
                    alert.show();
                    closeLoadWindow(this);
                });
            }
        };
        Thread setAttributeIsUsedOffThread = new Thread(setAttributeIsUsedOffTask);
        setAttributeIsUsedOffThread.start();

        // Задача по наполнению экрана
        Task<Void> loadActuatorsAndSensorsTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    showLoadWindow(this);

                    actuatorsVbox.getChildren().clear();
                    sensorsVbox.getChildren().clear();
                });

                List<Actuator> actuators = schemes.get(selectedScheme.getNumber()).getActuators();
                int actuatorsDone = 0;
                int actuatorsMax = actuators.size();
                for (Actuator actuator : schemes.get(selectedScheme.getNumber()).getActuators()) {
                    actuatorsDone++;
                    updateMessage("Загрузка устройств: " + actuatorsDone + "/" + actuatorsMax);
                    updateProgress(actuatorsDone, actuatorsMax);
                    SchemeTitledPane actuatorTitledPane = new SchemeTitledPane(actuator);
                    try {
                        Platform.runLater(() -> actuatorsVbox.getChildren().add(actuatorTitledPane));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw e;
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
                    SchemeTitledPane sensorTitledPane = new SchemeTitledPane(sensor);
                    try {
                        Platform.runLater(() -> sensorsVbox.getChildren().add(sensorTitledPane));
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
                System.out.println("Задача loadActuatorsAndSensorsTask выполнена успешно");
                Platform.runLater(() -> closeLoadWindow(this));
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                System.out.println("Задача loadActuatorsAndSensorsTask прервана");
            }

            @Override
            protected void failed() {
                super.failed();
                System.out.println("Задача loadActuatorsAndSensorsTask завершилась ошибкой");
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Невозможно выполнить операцию");
                    alert.setContentText("- установите соединение с контроллером, или повторите ещё раз");
                    alert.show();
                    closeLoadWindow(this);
                });
            }
        };
        Thread loadActuatorsAndSensorsThread = new Thread(loadActuatorsAndSensorsTask);
        setAttributeIsUsedOffTask.setOnSucceeded(e -> loadActuatorsAndSensorsThread.start());
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
    private ObservableList<String> getSchemesForSchemeNumberChoiceBox(List<Scheme> schemes) {
        List<String> schemeStrings = new ArrayList<>();
        for (Scheme scheme : schemes) {
            schemeStrings.add(scheme.getNumber() + 1 + " - " + scheme.getName());
        }
        return FXCollections.observableArrayList(schemeStrings);
    }
}
