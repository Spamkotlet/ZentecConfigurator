package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.enums.VarTypes;
import com.zenconf.zentecconfigurator.models.nodes.SchemeTitledPane;

import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChangeSchemeController implements Initializable {

    @FXML
    public ChoiceBox<String> schemeNumberChoiceBox;
    @FXML
    public TitledPane schemeChoiceTitledPane;
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

    private List<Scheme> schemes;
    protected static Scheme selectedScheme = null;
    private Scheme previousScheme = null;
    protected static List<Sensor> sensorsInScheme;
    protected static List<Actuator> actuatorsInScheme;
    private List<Actuator> actuatorList;
    private List<Sensor> sensorList;
    private ModbusUtilSingleton modbusUtilSingleton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        changeSchemeVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // Потом надо запихать в метод
                ObservableList<String> schemesItems = getSchemesForSchemeNumberChoiceBox(schemes);
                try {
                    selectedScheme = readSchemeNumberFromModbus();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                schemeNumberChoiceBox.setValue(schemesItems.get(selectedScheme.getNumber()));
            }
        });

        try {
            onOpenedChoiceSchemePane();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        schemeNumberChoiceBox.setOnAction(e -> {
            try {
                onActionSchemeNumberChoiceBox();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    // Что происходит при открытии экрана
    protected void onOpenedChoiceSchemePane() throws Exception {
        schemes = MainController.schemes;
        actuatorList = MainController.actuatorList;
        sensorList = MainController.sensorList;

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
            for (Actuator actuator : actuatorList) {
                if (actuatorInScheme.getName().equals(actuator.getName())) {
                    actuatorInScheme.setAttributes(actuator.getAttributes());
                    actuatorInScheme.setAttributeForMonitoring(actuator.getAttributeForMonitoring());
                    actuatorInScheme.setAttributesForControlling(actuator.getAttributesForControlling());
                }
            }
        }

        for (Sensor sensorInScheme : sensorsInScheme) {
            for (Sensor sensor : sensorList) {
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

        schemeChoiceTitledPane.getContent();
        ObservableList<Node> actuatorSchemeTitledPaneNodes = actuatorsVbox.getChildren();
        // Установка всех чекбоксов "Используется" в положение false
        if (actuatorSchemeTitledPaneNodes != null) {
            for (Node schemeTitledNode : actuatorSchemeTitledPaneNodes) {
                ((SchemeTitledPane) schemeTitledNode).setAttributeIsUsedOff();
            }
        }

        Thread thread;
        Runnable task = () -> {
            transparentPane.setVisible(true);
            progressBar.setVisible(true);
            Platform.runLater(() -> actuatorsVbox.getChildren().clear());
            for (Actuator actuator : schemes.get(selectedScheme.getNumber()).getActuators()) {
                SchemeTitledPane schemeTitledPane = null;
                try {
                    schemeTitledPane = new SchemeTitledPane(actuator);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                SchemeTitledPane finalSchemeTitledPane = schemeTitledPane;
                Platform.runLater(() -> actuatorsVbox.getChildren().add(finalSchemeTitledPane));
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            ObservableList<Node> sensorSchemeTitledPaneNodes = sensorsVbox.getChildren();
            if (sensorSchemeTitledPaneNodes != null) {
                for (Node schemeTitledNode : sensorSchemeTitledPaneNodes) {
                    try {
                        ((SchemeTitledPane) schemeTitledNode).setAttributeIsUsedOff();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            Platform.runLater(() -> sensorsVbox.getChildren().clear());
            for (Sensor sensor : schemes.get(selectedScheme.getNumber()).getSensors()) {
                SchemeTitledPane schemeTitledPane = null;
                try {
                    schemeTitledPane = new SchemeTitledPane(sensor);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                SchemeTitledPane finalSchemeTitledPane = schemeTitledPane;
                Platform.runLater(() -> sensorsVbox.getChildren().add(finalSchemeTitledPane));
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            transparentPane.setVisible(false);
            progressBar.setVisible(false);
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
