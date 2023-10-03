package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.ConfiguratorController;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class SensorsController extends CommonController implements Initializable {

    @FXML
    public VBox sensorsSettingsVbox;

    @FXML
    private VBox sensorsVBox;

    private final List<Sensor> sensorsUsed = new ArrayList<>();
    private final HashMap<String, ElementTitledPane> sensorsTitledPaneHashMap = new HashMap<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sensorsVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (ConfiguratorController.sensorsUsed != null) {
                    if (!ConfiguratorController.sensorsUsed.equals(this.sensorsUsed)) {
                        fillSensorsSettingsPane();
                    }
                }
            }
        });
    }

    private void fillSensorsSettingsPane() {
        Task<Void> fillingPaneTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    showLoadWindow(this);

                    sensorsSettingsVbox.getChildren().clear();
                    System.out.println("sensorsSettingsVBox очищен");
                });

                List<Sensor> sensors = ConfiguratorController.sensorsUsed;
                Sensor sensor = null;
                boolean isSuccessfulAction = true;
                int successfulActionAttempt = 0;
                for (int i = 0; i < sensors.size(); ) {
                    if (isSuccessfulAction) {
                        sensor = sensors.get(i);
                        updateMessage("Загрузка...: " + (i + 1) + "/" + sensors.size());
                        updateProgress(i + 1, sensors.size());
                    }

                    ElementTitledPane sensorTitledPane = null;
                    if (sensor.getAttributes() != null) {
                        if (sensorsTitledPaneHashMap.get(sensor.getName()) == null) {
                            try {
                                sensorTitledPane = new ElementTitledPane(sensor);
                                sensorsTitledPaneHashMap.put(sensor.getName(), sensorTitledPane);
                                isSuccessfulAction = true;
                                i++;
                            } catch (Exception e) {
                                e.printStackTrace();
                                logger.error(e.getMessage());
                                isSuccessfulAction = false;
                                successfulActionAttempt++;
                                updateMessage("Попытка загрузки [" + successfulActionAttempt + "/3]\n" + sensor.getName());
                                if (successfulActionAttempt >= 3) {
                                    isSuccessfulAction = true;
                                    successfulActionAttempt = 0;
                                    i++;
                                    updateMessage("Ошибка загрузки\n" + sensor.getName());
                                    Thread.sleep(1000);
                                }
                                Thread.sleep(1000);
                            }
                        } else {
                            sensorTitledPane = sensorsTitledPaneHashMap.get(sensor.getName());
                            sensorTitledPane.setElement(sensor);
                            isSuccessfulAction = true;
                            i++;
                        }
                    }
                    try {
                        ElementTitledPane finalSensorTitledPane = sensorTitledPane;
                        Platform.runLater(() -> sensorsSettingsVbox.getChildren().add(finalSensorTitledPane));
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
                logger.info("Задача fillingSensorsPaneTask выполнена успешно");
                Platform.runLater(() -> closeLoadWindow(this));
                Thread.currentThread().interrupt();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                logger.info("Задача fillingSensorsPaneTask прервана");
                Thread.currentThread().interrupt();
            }

            @Override
            protected void failed() {
                super.failed();
                getException().printStackTrace();
                logger.info("Задача fillingSensorsPaneTask завершилась ошибкой");
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
        Thread fillingPaneThread = new Thread(fillingPaneTask);
        fillingPaneThread.start();
        fillingPaneTask.setOnSucceeded(e -> {
            if (sensorsSettingsVbox.getChildren().size() == ConfiguratorController.sensorsUsed.size()) {
                sensorsUsed.clear();
                sensorsUsed.addAll(ConfiguratorController.sensorsUsed);
            }
        });
    }
}
