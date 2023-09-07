package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.MainParameters;
import com.zenconf.zentecconfigurator.models.Parameter;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
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
import java.util.List;
import java.util.ResourceBundle;

public class ActuatorsController implements Initializable {
    private List<Actuator> actuatorsInScheme;
    private MainParameters mainParameters;

    @FXML
    public VBox actuatorsSettingsVbox;
    @FXML
    public VBox actuatorsVBox;

    @FXML
    public AnchorPane transparentPane;
    @FXML
    public ProgressBar progressBar;

    private static final Logger logger = LogManager.getLogger(ActuatorsController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainParameters = MainController.mainParameters;
        actuatorsVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                actuatorsInScheme = ChangeSchemeController.actuatorsInScheme;

                if (actuatorsInScheme != null) {
                    fillActuatorsSettingsPane();
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
                if (mainParameters != null) {
                    Parameter valveParameter = new Parameter();
                    valveParameter.setName("Воздушный клапан");
                    valveParameter.setAttributes(mainParameters.getValveAttributes());
                    ElementTitledPane valveTitledPane = null;
                    try {
                        valveTitledPane = new ElementTitledPane(valveParameter);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    ElementTitledPane finalValveTitledPane = valveTitledPane;
                    Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(finalValveTitledPane));

                    Parameter valveHeatersParameter = new Parameter();
                    valveHeatersParameter.setName("Обогрев клапанов");
                    valveHeatersParameter.setAttributes(mainParameters.getValveHeatersAttributes());
                    ElementTitledPane valveHeatersTitledPane = null;
                    try {
                        valveHeatersTitledPane = new ElementTitledPane(valveHeatersParameter);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    ElementTitledPane finalValveHeatersTitledPane = valveHeatersTitledPane;
                    Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(finalValveHeatersTitledPane));

                    Parameter heatExchangerParameter = new Parameter();
                    heatExchangerParameter.setName("Теплообменники");
                    heatExchangerParameter.setAttributes(mainParameters.getHeatExchangerAttributes());
                    ElementTitledPane heatExchangerTitledPane = null;
                    try {
                        heatExchangerTitledPane = new ElementTitledPane(heatExchangerParameter);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                        throw new RuntimeException(e);
                    }
                    ElementTitledPane finalHeatExchangerTitledPane = heatExchangerTitledPane;
                    Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(finalHeatExchangerTitledPane));
                }

                for (Actuator actuatorInScheme : actuatorsInScheme) {
                    if (actuatorInScheme.getIsUsedDefault()) {
                        if (actuatorInScheme.getAttributes() != null) {
                            ElementTitledPane elementTitledPane = null;
                            try {
                                elementTitledPane = new ElementTitledPane(actuatorInScheme);
                            } catch (Exception e) {
                                transparentPane.setVisible(false);
                                progressBar.setVisible(false);
                                logger.error(e.getMessage());
                                throw new RuntimeException(e);
                            }
                            ElementTitledPane finalElementTitledPane = elementTitledPane;
                            Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(finalElementTitledPane));
                            try {
                                Thread.sleep(20);
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
