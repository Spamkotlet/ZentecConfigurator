package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SensorsController implements Initializable {

    private List<Sensor> sensorsInScheme;

    @FXML
    public VBox sensorsSettingsVbox;

    @FXML
    private VBox sensorsVBox;

    @FXML
    public AnchorPane transparentPane;
    @FXML
    public ProgressBar progressBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sensorsVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sensorsInScheme = ChangeSchemeController.sensorsInScheme;
                if (sensorsInScheme != null) {
                    fillSensorsSettingsPane();
                }
            }
        });
    }

    private void fillSensorsSettingsPane() {
        Thread thread;
        Runnable task = () -> {
            transparentPane.setVisible(true);
            progressBar.setVisible(true);
            Platform.runLater(() -> sensorsSettingsVbox.getChildren().clear());
            for (Sensor sensorInScheme : sensorsInScheme) {
                if (sensorInScheme.getIsUsedDefault()) {
                    if (sensorInScheme.getAttributes() != null) {
                        ElementTitledPane elementTitledPane = new ElementTitledPane(sensorInScheme);
                        Platform.runLater(() -> sensorsSettingsVbox.getChildren().add(elementTitledPane));
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }

            transparentPane.setVisible(false);
            progressBar.setVisible(false);
        };
        thread = new Thread(task);
        thread.start();
    }
}
