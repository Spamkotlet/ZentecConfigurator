package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.ConfiguratorController;
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
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class ActuatorsController extends CommonController implements Initializable {
    @FXML
    public VBox actuatorsSettingsVbox;
    @FXML
    public VBox actuatorsVBox;

    private final List<Actuator> actuatorsUsed = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(ActuatorsController.class);

    private final HashMap<String, ElementTitledPane> actuatorsTitledPaneHashMap = new HashMap<>();

    private int standardElementsCount = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actuatorsVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (ConfiguratorController.actuatorsUsed != null) {
                    if (!ConfiguratorController.actuatorsUsed.equals(this.actuatorsUsed)) {
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

                List<Actuator> actuators = ConfiguratorController.actuatorsUsed;
                if (MainController.mainParameters != null) {
                    Parameter valveParameter = new Parameter();
                    valveParameter.setName("Воздушный клапан");
                    valveParameter.setAttributes(MainController.mainParameters.getValveAttributes());
                    ElementTitledPane valveTitledPane;
                    if (actuatorsTitledPaneHashMap.get(valveParameter.getName()) == null) {
                        valveTitledPane = new ElementTitledPane(valveParameter);
                        actuatorsTitledPaneHashMap.put(valveParameter.getName(), valveTitledPane);
                    } else {
                        valveTitledPane = actuatorsTitledPaneHashMap.get(valveParameter.getName());
                    }

                    try {
                        Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(valveTitledPane));
                        standardElementsCount++;
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw e;
                    }

                    Parameter valveHeatersParameter = new Parameter();
                    valveHeatersParameter.setName("Обогрев клапанов");
                    valveHeatersParameter.setAttributes(MainController.mainParameters.getValveHeatersAttributes());
                    ElementTitledPane valveHeatersTitledPane;

                    if (actuatorsTitledPaneHashMap.get(valveHeatersParameter.getName()) == null) {
                        valveHeatersTitledPane = new ElementTitledPane(valveHeatersParameter);
                        actuatorsTitledPaneHashMap.put(valveHeatersParameter.getName(), valveHeatersTitledPane);
                    } else {
                        valveHeatersTitledPane = actuatorsTitledPaneHashMap.get(valveHeatersParameter.getName());
                    }

                    try {
                        Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(valveHeatersTitledPane));
                        standardElementsCount++;
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw e;
                    }

                    Parameter heatExchangerParameter = new Parameter();
                    heatExchangerParameter.setName("Теплообменники");
                    heatExchangerParameter.setAttributes(MainController.mainParameters.getHeatExchangerAttributes());
                    ElementTitledPane heatExchangerTitledPane;

                    if (actuatorsTitledPaneHashMap.get(heatExchangerParameter.getName()) == null) {
                        heatExchangerTitledPane = new ElementTitledPane(heatExchangerParameter);
                        actuatorsTitledPaneHashMap.put(heatExchangerParameter.getName(), heatExchangerTitledPane);
                    } else {
                        heatExchangerTitledPane = actuatorsTitledPaneHashMap.get(heatExchangerParameter.getName());
                    }

                    try {
                        Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(heatExchangerTitledPane));
                        standardElementsCount++;
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw e;
                    }
                }

                Actuator actuator = null;
                boolean isSuccessfulAction = true;
                int successfulActionAttempt = 0;
                for (int i = 0; i < actuators.size(); ) {
                    if (isSuccessfulAction) {
                        actuator = actuators.get(i);
                        updateMessage("Загрузка...: " + (i + 1) + "/" + actuators.size());
                        updateProgress(i + 1, actuators.size());
                    }

                    ElementTitledPane actuatorTitledPane = null;
                    if (actuator.getAttributes() != null) {
                        if (actuatorsTitledPaneHashMap.get(actuator.getName()) == null) {
                            try {
                                actuatorTitledPane = new ElementTitledPane(actuator);
                                actuatorsTitledPaneHashMap.put(actuator.getName(), actuatorTitledPane);
                                isSuccessfulAction = true;
                                i++;
                            } catch (Exception e) {
                                e.printStackTrace();
                                logger.error(e.getMessage());
                                isSuccessfulAction = false;
                                successfulActionAttempt++;
                                updateMessage("Попытка загрузки [" + successfulActionAttempt + "/3]\n" + actuator.getName());
                                if (successfulActionAttempt >= 3) {
                                    isSuccessfulAction = true;
                                    successfulActionAttempt = 0;
                                    i++;
                                    updateMessage("Ошибка загрузки\n" + actuator.getName());
                                    Thread.sleep(1000);
                                }
                                logger.error(e.getMessage());
                                Thread.sleep(1000);
                            }
                        } else {
                            actuatorTitledPane = actuatorsTitledPaneHashMap.get(actuator.getName());
                            isSuccessfulAction = true;
                            i++;
                        }
                    } else {
                        isSuccessfulAction = true;
                        i++;
                    }
                    try {
                        ElementTitledPane finalActuatorTitledPane = actuatorTitledPane;
                        Platform.runLater(() -> {
                            if (finalActuatorTitledPane != null) {
                                actuatorsSettingsVbox.getChildren().add(finalActuatorTitledPane);
                            }
                        });
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
                logger.info("Задача fillingActuatorsPaneTask выполнена успешно");
                Platform.runLater(() -> closeLoadWindow(this));
                Thread.currentThread().interrupt();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                logger.info("Задача fillingActuatorsPaneTask прервана");
                Thread.currentThread().interrupt();
            }

            @Override
            protected void failed() {
                super.failed();
                logger.info("Задача fillingActuatorsPaneTask завершилась ошибкой");
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
            if (actuatorsSettingsVbox.getChildren().size() - standardElementsCount == ConfiguratorController.actuatorsUsed.size()) {
                actuatorsUsed.clear();
                actuatorsUsed.addAll(ConfiguratorController.actuatorsUsed);
            }
            standardElementsCount = 0;
        });
    }
}
