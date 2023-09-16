package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Parameter;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ActuatorsController extends CommonController implements Initializable {
    @FXML
    public VBox actuatorsSettingsVbox;
    @FXML
    public VBox actuatorsVBox;

    private final List<Actuator> actuatorsUsed = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(ActuatorsController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actuatorsVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (ChangeSchemeController.actuatorsUsed != null) {
                    if (!ChangeSchemeController.actuatorsUsed.equals(this.actuatorsUsed)) {
                        fillActuatorsSettingsPane();
                    }
                }
            }
        });
    }

    private void fillActuatorsSettingsPane() {
        Task<Void> fillingPaneTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    showLoadWindow(this);

                    actuatorsSettingsVbox.getChildren().clear();
                });

                List<Actuator> actuators = ChangeSchemeController.actuatorsUsed;
                int actuatorsDone = 0;
                int actuatorsMax = actuators.size() + 3;
                if (MainController.mainParameters != null) {
                    Parameter valveParameter = new Parameter();
                    valveParameter.setName("Воздушный клапан");
                    valveParameter.setAttributes(MainController.mainParameters.getValveAttributes());
                    ElementTitledPane valveTitledPane = new ElementTitledPane(valveParameter);
                    try {
                        Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(valveTitledPane));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw e;
                    }
                    actuatorsDone++;

                    Parameter valveHeatersParameter = new Parameter();
                    valveHeatersParameter.setName("Обогрев клапанов");
                    valveHeatersParameter.setAttributes(MainController.mainParameters.getValveHeatersAttributes());
                    ElementTitledPane valveHeatersTitledPane = new ElementTitledPane(valveHeatersParameter);
                    try {
                        Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(valveHeatersTitledPane));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw e;
                    }
                    actuatorsDone++;

                    Parameter heatExchangerParameter = new Parameter();
                    heatExchangerParameter.setName("Теплообменники");
                    heatExchangerParameter.setAttributes(MainController.mainParameters.getHeatExchangerAttributes());
                    ElementTitledPane heatExchangerTitledPane = new ElementTitledPane(heatExchangerParameter);
                    try {
                        Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(heatExchangerTitledPane));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw e;
                    }
                    actuatorsDone++;
                }

                for (Actuator actuatorInScheme : actuators) {
                    if (actuatorInScheme.getIsUsedDefault()) {
                        actuatorsDone++;
                        updateMessage("Загрузка...: " + actuatorsDone + "/" + actuatorsMax);
                        updateProgress(actuatorsDone, actuatorsMax);
                        if (actuatorInScheme.getAttributes() != null) {
                            ElementTitledPane actuatorTitledPane = new ElementTitledPane(actuatorInScheme);
                            try {
                                Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(actuatorTitledPane));
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
            actuatorsUsed.clear();
            actuatorsUsed.addAll(ChangeSchemeController.actuatorsUsed);
        });
    }
}
