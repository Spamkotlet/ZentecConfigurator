package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Scheme;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class ActuatorsController implements Initializable {

    private Stage primaryStage;
    private List<Actuator> actuators;
    private Scheme selectedScheme;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectedScheme = ChangeSchemeController.selectedScheme;
        actuators = selectedScheme.getActuators();
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void onOpened() {

    }
}
