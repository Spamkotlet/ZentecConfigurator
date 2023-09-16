package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sensorsVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (!Objects.equals(newVal, oldVal)) {
                if (ChangeSchemeController.sensorsUsed != null) {
                    if (!ChangeSchemeController.sensorsUsed.equals(this.sensorsUsed)) {
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
                });

                List<Sensor> sensors = ChangeSchemeController.sensorsUsed;
                int sensorsDone = 0;
                int sensorsMax = sensors.size();
                for (Sensor sensorInScheme : ChangeSchemeController.sensorsUsed) {
                    if (sensorInScheme.getIsUsedDefault()) {
                        sensorsDone++;
                        updateMessage("Загрузка...: " + sensorsDone + "/" + sensorsMax);
                        updateProgress(sensorsDone, sensorsMax);
                        if (sensorInScheme.getAttributes() != null) {
                            ElementTitledPane sensorTitledPane = new ElementTitledPane(sensorInScheme);
                            try {
                                Platform.runLater(() -> sensorsSettingsVbox.getChildren().add(sensorTitledPane));
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                                throw e;
                            }
                            Thread.sleep(100);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                System.out.println("Задача fillingPaneTask выполнена успешно");
                Platform.runLater(() -> closeLoadWindow(this));
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                System.out.println("Задача fillingPaneTask прервана");
            }

            @Override
            protected void failed() {
                super.failed();
                System.out.println("Задача fillingPaneTask завершилась ошибкой");
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
        Thread fillingPaneThread = new Thread(fillingPaneTask);
        fillingPaneThread.start();
        fillingPaneTask.setOnSucceeded(e -> {
            sensorsUsed.clear();
            sensorsUsed.addAll(ChangeSchemeController.sensorsUsed);
        });
    }
}
