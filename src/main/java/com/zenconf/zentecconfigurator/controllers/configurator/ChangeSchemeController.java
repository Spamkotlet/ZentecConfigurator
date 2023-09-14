package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.ConfiguratorController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.enums.VarTypes;
import com.zenconf.zentecconfigurator.models.nodes.SchemeTitledPane;

import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChangeSchemeController extends CommonController implements Initializable {

    @FXML
    public ChoiceBox<String> schemeNumberChoiceBox;
    @FXML
    public VBox actuatorsVbox;
    @FXML
    public VBox sensorsVbox;
    @FXML
    public VBox changeSchemeVBox;
    @FXML
    public AnchorPane transparentPane;
    @FXML
    public ProgressBar progressBar;
    private Thread thread;

    private List<Scheme> schemes;
    public static Scheme selectedScheme = null;
    private Scheme previousScheme = null;
    public static List<Sensor> sensorsInScheme;
    public static List<Actuator> actuatorsInScheme;
    private ModbusUtilSingleton modbusUtilSingleton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        changeSchemeVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ObservableList<String> schemesItems = getSchemesForSchemeNumberChoiceBox(schemes);
                try {
                    selectedScheme = readSchemeNumberFromModbus();
                    ConfiguratorController.selectScheme();
                    logger.info("Выбрана схема " + selectedScheme.getNumber() + " / " + selectedScheme.getName());
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
                schemeNumberChoiceBox.setValue(schemesItems.get(selectedScheme.getNumber()));
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

        schemeNumberChoiceBox.setOnAction(e -> {
            try {
                ConfiguratorController.selectScheme();
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

    // Что происходит при открытии экрана
    protected void onOpenedChangeSchemeVBox() throws Exception {
        schemes = MainController.schemes;

        ObservableList<String> schemesItems = getSchemesForSchemeNumberChoiceBox(schemes);

        schemeNumberChoiceBox.setItems(schemesItems);
        selectedScheme = readSchemeNumberFromModbus();
        schemeNumberChoiceBox.setValue(schemesItems.get(selectedScheme.getNumber()));
    }

    private void onActionSchemeNumberChoiceBox() throws Exception {
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

        for (Actuator actuatorInScheme : actuatorsInScheme) {
            for (Actuator actuator : MainController.actuatorList) {
                if (actuatorInScheme.getName().equals(actuator.getName())) {
                    actuatorInScheme.setAttributes(actuator.getAttributes());
                    actuatorInScheme.setAttributeForMonitoring(actuator.getAttributeForMonitoring());
                    actuatorInScheme.setAttributesForControlling(actuator.getAttributesForControlling());
                }
            }
        }

        for (Sensor sensorInScheme : sensorsInScheme) {
            for (Sensor sensor : MainController.sensorList) {
                if (sensorInScheme.getName().equals(sensor.getName())) {
                    sensorInScheme.setAttributes(sensor.getAttributes());
                    sensorInScheme.setAttributeForMonitoring(sensor.getAttributeForMonitoring());
                    sensorInScheme.setAttributesForControlling(sensor.getAttributesForControlling());
                }
            }
        }
        fillingPane();
        writeSchemeNumberByModbus();
    }


    // Заполнение панели устройствами и датчиками
    private void fillingPane() throws Exception {

        ObservableList<Node> actuatorSchemeTitledPaneNodes = actuatorsVbox.getChildren();
        // Установка всех чекбоксов "Используется" в положение false
        if (actuatorSchemeTitledPaneNodes != null) {
            for (Node schemeNode : actuatorSchemeTitledPaneNodes) {
                ((SchemeTitledPane) schemeNode).setAttributeIsUsedOff();
            }
        }

        Runnable task = () -> {
            progressBar.setVisible(true);
            transparentPane.setVisible(true);
            Platform.runLater(() -> actuatorsVbox.getChildren().clear());
            logger.info("Создание наполнения");
            for (Actuator actuator : schemes.get(selectedScheme.getNumber()).getActuators()) {
                SchemeTitledPane actuatorTitledPane;
                try {
                    actuatorTitledPane = new SchemeTitledPane(actuator);
                    Platform.runLater(() -> actuatorsVbox.getChildren().add(actuatorTitledPane));
                } catch (Exception e) {
                    logger.error(e.getMessage());
                    progressBar.setVisible(false);
                    transparentPane.setVisible(false);
                    throw new RuntimeException(e);
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            ObservableList<Node> sensorSchemeTitledPaneNodes = sensorsVbox.getChildren();
            if (sensorSchemeTitledPaneNodes != null) {
                for (Node schemeNode : sensorSchemeTitledPaneNodes) {
                    try {
                        ((SchemeTitledPane) schemeNode).setAttributeIsUsedOff();
                    } catch (Exception e) {
                        progressBar.setVisible(false);
                        transparentPane.setVisible(false);
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            Platform.runLater(() -> sensorsVbox.getChildren().clear());
            for (Sensor sensor : schemes.get(selectedScheme.getNumber()).getSensors()) {
                SchemeTitledPane sensorTitledPane;
                try {
                    sensorTitledPane = new SchemeTitledPane(sensor);
                    Platform.runLater(() -> sensorsVbox.getChildren().add(sensorTitledPane));
                } catch (Exception e) {
                    progressBar.setVisible(false);
                    transparentPane.setVisible(false);
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            progressBar.setVisible(false);
            transparentPane.setVisible(false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
        thread = new Thread(task);
        thread.start();
    }

    // Запись номера схемы в контроллер по Modbus
    private void writeSchemeNumberByModbus() throws Exception {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        if (modbusUtilSingleton.getMaster() != null) {
            modbusUtilSingleton.writeModbus(1436, VarTypes.UINT8, selectedScheme.getNumber());
        }
    }

    // Чтение номера схемы из контроллера по Modbus
    private Scheme readSchemeNumberFromModbus() throws Exception {
        selectedScheme = schemes.get(0);
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        if (modbusUtilSingleton.getMaster() != null) {
            selectedScheme = schemes.get(Integer.parseInt(modbusUtilSingleton.readModbus(1436, VarTypes.UINT8).toString()));
        }
        return selectedScheme;
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
