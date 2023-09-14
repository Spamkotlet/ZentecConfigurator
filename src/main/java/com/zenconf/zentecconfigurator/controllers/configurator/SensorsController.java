package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class SensorsController extends CommonController implements Initializable {

    @FXML
    public VBox sensorsSettingsVbox;

    @FXML
    private VBox sensorsVBox;

    private int sensorsUsedNumber = 0;
    private int sensorsUsedNumberPrev = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sensorsVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (ChangeSchemeController.sensorsInScheme != null) {
                    for (Sensor sensor: ChangeSchemeController.sensorsInScheme) {
                        if (sensor.getIsUsedDefault()) {
                            sensorsUsedNumber++;
                        }
                    }
                    if (sensorsUsedNumber != sensorsUsedNumberPrev) {
                        sensorsUsedNumberPrev = sensorsUsedNumber;
                        fillSensorsSettingsPane();
                    }
                    sensorsUsedNumber = 0;
                }
            }
        });
    }

    private void fillSensorsSettingsPane() {
        Runnable task = () -> {
            Platform.runLater(this::showLoadWindow);
            Platform.runLater(() -> sensorsSettingsVbox.getChildren().clear());
            logger.info("Создание наполнения");

            for (Sensor sensorInScheme : ChangeSchemeController.sensorsInScheme) {
                if (sensorInScheme.getIsUsedDefault()) {
                    if (sensorInScheme.getAttributes() != null) {
                        ElementTitledPane sensorTitledPane;
                        try {
                            sensorTitledPane = new ElementTitledPane(sensorInScheme);
                            Platform.runLater(() -> sensorsSettingsVbox.getChildren().add(sensorTitledPane));
                        } catch (Exception e) {
                            Platform.runLater(this::closeLoadWindow);
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
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
            Platform.runLater(this::closeLoadWindow);
        };
        loadingThread = new Thread(task);
        loadingThread.start();
    }
}
