package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.ConfiguratorController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Parameter;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import com.zenconf.zentecconfigurator.models.nodes.SchemeTitledPane;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.ref.WeakReference;
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
                int actuatorsDone = 0;
                int actuatorsMax = actuators.size() + 3;
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
                        valveTitledPane.setElement(valveParameter);
                    }

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
                    ElementTitledPane valveHeatersTitledPane;

                    if (actuatorsTitledPaneHashMap.get(valveHeatersParameter.getName()) == null) {
                        valveHeatersTitledPane = new ElementTitledPane(valveHeatersParameter);
                        actuatorsTitledPaneHashMap.put(valveHeatersParameter.getName(), valveHeatersTitledPane);
                    } else {
                        valveHeatersTitledPane = actuatorsTitledPaneHashMap.get(valveHeatersParameter.getName());
                        valveHeatersTitledPane.setElement(valveHeatersParameter);
                    }

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
                    ElementTitledPane heatExchangerTitledPane;

                    if (actuatorsTitledPaneHashMap.get(heatExchangerParameter.getName()) == null) {
                        heatExchangerTitledPane = new ElementTitledPane(heatExchangerParameter);
                        actuatorsTitledPaneHashMap.put(heatExchangerParameter.getName(), heatExchangerTitledPane);
                    } else {
                        heatExchangerTitledPane = actuatorsTitledPaneHashMap.get(heatExchangerParameter.getName());
                        heatExchangerTitledPane.setElement(heatExchangerParameter);
                    }

                    try {
                        Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(heatExchangerTitledPane));
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw e;
                    }
                    actuatorsDone++;
                }

                for (Actuator actuatorInScheme : actuators) {
                    actuatorsDone++;
                    updateMessage("Загрузка...: " + actuatorsDone + "/" + actuatorsMax);
                    updateProgress(actuatorsDone, actuatorsMax);
                    if (actuatorInScheme.getAttributes() != null) {
                        ElementTitledPane actuatorTitledPane;
                        if (actuatorsTitledPaneHashMap.get(actuatorInScheme.getName()) == null) {
                            actuatorTitledPane = new ElementTitledPane(actuatorInScheme);
                            actuatorsTitledPaneHashMap.put(actuatorInScheme.getName(), actuatorTitledPane);
                        } else {
                            actuatorTitledPane = actuatorsTitledPaneHashMap.get(actuatorInScheme.getName());
                            actuatorTitledPane.setElement(actuatorInScheme);
                        }

                        try {
                            Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(actuatorTitledPane));
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                            throw e;
                        }
                        Thread.sleep(100);
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                System.out.println("Задача fillingPaneTask выполнена успешно");
                Platform.runLater(() -> closeLoadWindow(this));
                Thread.currentThread().interrupt();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                System.out.println("Задача fillingPaneTask прервана");
                Thread.currentThread().interrupt();
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
                Thread.currentThread().interrupt();
            }
        };
        Thread fillingPaneThread = new Thread(fillingPaneTask);
        fillingPaneThread.start();
        fillingPaneTask.setOnSucceeded(e -> {
            actuatorsUsed.clear();
            actuatorsUsed.addAll(ConfiguratorController.actuatorsUsed);
        });
    }
}
