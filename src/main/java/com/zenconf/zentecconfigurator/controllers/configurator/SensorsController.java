package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.models.Sensor;
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

    private static final Logger logger = LogManager.getLogger(SensorsController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sensorsVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if (ChangeSchemeController.sensorsInScheme != null) {
                    fillSensorsSettingsPane();
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
                        ElementTitledPane elementTitledPane;
                        try {
                            elementTitledPane = new ElementTitledPane(sensorInScheme);
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
                        ElementTitledPane finalElementTitledPane = elementTitledPane;
                        Platform.runLater(() -> sensorsSettingsVbox.getChildren().add(finalElementTitledPane));
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
