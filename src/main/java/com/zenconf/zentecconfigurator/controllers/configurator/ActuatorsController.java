package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Parameter;
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

public class ActuatorsController implements Initializable {
    @FXML
    public VBox actuatorsSettingsVbox;
    @FXML
    public VBox actuatorsVBox;

    @FXML
    public AnchorPane transparentPane;
    @FXML
    public ProgressBar progressBar;

    private int devicesUsedNumber = 0;
    private int devicesUsedNumberPrev = 0;

    private static final Logger logger = LogManager.getLogger(ActuatorsController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Actuators initialize");
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
        Thread thread;
        Runnable task = () -> {
            transparentPane.setVisible(true);
            progressBar.setVisible(true);
            Platform.runLater(() -> actuatorsSettingsVbox.getChildren().clear());
            logger.info("Создание наполнения");

            try {
                if (MainController.mainParameters != null) {
                    Parameter valveParameter = new Parameter();
                    valveParameter.setName("Воздушный клапан");
                    valveParameter.setAttributes(MainController.mainParameters.getValveAttributes());
                    ElementPane valvePane;
                    try {
                        valvePane = new ElementPane(valveParameter);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    ElementPane finalValvePane = valvePane;
                    Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(finalValvePane));

                    Parameter valveHeatersParameter = new Parameter();
                    valveHeatersParameter.setName("Обогрев клапанов");
                    valveHeatersParameter.setAttributes(MainController.mainParameters.getValveHeatersAttributes());
                    ElementPane valveHeatersPane;
                    try {
                        valveHeatersPane = new ElementPane(valveHeatersParameter);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    ElementPane finalValveHeatersPane = valveHeatersPane;
                    Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(finalValveHeatersPane));

                    Parameter heatExchangerParameter = new Parameter();
                    heatExchangerParameter.setName("Теплообменники");
                    heatExchangerParameter.setAttributes(MainController.mainParameters.getHeatExchangerAttributes());
                    ElementPane heatExchangerPane;
                    try {
                        heatExchangerPane = new ElementPane(heatExchangerParameter);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    ElementPane finalHeatExchangerPane = heatExchangerPane;
                    Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(finalHeatExchangerPane));
                }

                for (Actuator actuatorInScheme : ChangeSchemeController.actuatorsInScheme) {
                    if (actuatorInScheme.getIsUsedDefault()) {
                        if (actuatorInScheme.getAttributes() != null) {
                            ElementPane elementPane;
                            try {
                                elementPane = new ElementPane(actuatorInScheme);
                            } catch (Exception e) {
                                transparentPane.setVisible(false);
                                progressBar.setVisible(false);
                                logger.error(e.getMessage());
                                throw new RuntimeException(e);
                            }
                            ElementPane finalElementPane = elementPane;
                            Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(finalElementPane));
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

            transparentPane.setVisible(false);
            progressBar.setVisible(false);
        };
        thread = new Thread(task);
        thread.start();
    }
}
