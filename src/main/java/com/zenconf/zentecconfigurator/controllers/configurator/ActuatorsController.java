package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Parameter;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class ActuatorsController extends CommonController implements Initializable {
    @FXML
    public VBox actuatorsSettingsVbox;
    @FXML
    public VBox actuatorsVBox;

    private int devicesUsedNumber = 0;
    private int devicesUsedNumberPrev = 0;

    private static final Logger logger = LogManager.getLogger(ActuatorsController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actuatorsVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (ChangeSchemeController.actuatorsInScheme != null) {
                    for (Actuator actuator: ChangeSchemeController.actuatorsInScheme) {
                        if (actuator.getIsUsedDefault()) {
                            devicesUsedNumber++;
                        }
                    }
                    if (devicesUsedNumber != devicesUsedNumberPrev) {
                        devicesUsedNumberPrev = devicesUsedNumber;
                        fillActuatorsSettingsPane();
                    }
                    devicesUsedNumber = 0;
                }
            }
        });
    }

    private void fillActuatorsSettingsPane() {
        Runnable task = () -> {
            Platform.runLater(this::showLoadWindow);
            Platform.runLater(() -> actuatorsSettingsVbox.getChildren().clear());
            logger.info("Создание наполнения");

            try {
                if (MainController.mainParameters != null) {
                    Parameter valveParameter = new Parameter();
                    valveParameter.setName("Воздушный клапан");
                    valveParameter.setAttributes(MainController.mainParameters.getValveAttributes());
                    ElementTitledPane valveTitledPane;
                    try {
                        valveTitledPane = new ElementTitledPane(valveParameter);
                        Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(valveTitledPane));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }

                    Parameter valveHeatersParameter = new Parameter();
                    valveHeatersParameter.setName("Обогрев клапанов");
                    valveHeatersParameter.setAttributes(MainController.mainParameters.getValveHeatersAttributes());
                    ElementTitledPane valveHeatersTitledPane;
                    try {
                        valveHeatersTitledPane = new ElementTitledPane(valveHeatersParameter);
                        Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(valveHeatersTitledPane));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }

                    Parameter heatExchangerParameter = new Parameter();
                    heatExchangerParameter.setName("Теплообменники");
                    heatExchangerParameter.setAttributes(MainController.mainParameters.getHeatExchangerAttributes());
                    ElementTitledPane heatExchangerTitledPane;
                    try {
                        heatExchangerTitledPane = new ElementTitledPane(heatExchangerParameter);
                        Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(heatExchangerTitledPane));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }

                for (Actuator actuatorInScheme : ChangeSchemeController.actuatorsInScheme) {
                    if (actuatorInScheme.getIsUsedDefault()) {
                        if (actuatorInScheme.getAttributes() != null) {
                            ElementTitledPane actuatorTitledPane;
                            try {
                                actuatorTitledPane = new ElementTitledPane(actuatorInScheme);
                                Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(actuatorTitledPane));
                            } catch (Exception e) {
                                Platform.runLater(this::closeLoadWindow);
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
                }
            } catch (Exception e) {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Ошибка");
                    alert.setHeaderText("Невозможно выполнить операцию");
                    alert.setContentText("- установите соединение с контроллером, или повторите ещё раз");
                    alert.show();
                });
            }

            Platform.runLater(this::closeLoadWindow);
        };
        loadingThread = new Thread(task);
        loadingThread.start();
    }
}
