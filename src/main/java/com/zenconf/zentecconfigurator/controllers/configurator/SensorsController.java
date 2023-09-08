package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.nodes.ElementPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class SensorsController implements Initializable {

    @FXML
    public VBox sensorsSettingsVbox;

    @FXML
    private VBox sensorsVBox;

    @FXML
    public AnchorPane transparentPane;
    @FXML
    public ProgressBar progressBar;

    private int sensorsUsedNumber = 0;
    private int sensorsUsedNumberPrev = 0;

    private static final Logger logger = LogManager.getLogger(SensorsController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Sensors initialize");
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
        Thread thread;
        Runnable task = () -> {
            transparentPane.setVisible(true);
            progressBar.setVisible(true);
            Platform.runLater(() -> sensorsSettingsVbox.getChildren().clear());
            logger.info("Создание наполнения");

            for (Sensor sensorInScheme : ChangeSchemeController.sensorsInScheme) {
                if (sensorInScheme.getIsUsedDefault()) {
                    if (sensorInScheme.getAttributes() != null) {
                        ElementPane elementPane;
                        try {
                            elementPane = new ElementPane(sensorInScheme);
                        } catch (Exception e) {
                            transparentPane.setVisible(false);
                            progressBar.setVisible(false);
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
                        ElementPane finalElementPane = elementPane;
                        Platform.runLater(() -> sensorsSettingsVbox.getChildren().add(finalElementPane));
                        try {
                            Thread.sleep(100);
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
