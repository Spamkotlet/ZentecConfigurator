package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Parameter;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PeripheryController extends CommonController implements Initializable {

    @FXML
    public VBox peripherySettingsVbox;
    @FXML
    public VBox peripheryVBox;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillPeripherySettingsPane();
    }

    private void fillPeripherySettingsPane() {
        Runnable task = () -> {
            Platform.runLater(this::showLoadWindow);
            Platform.runLater(() -> peripherySettingsVbox.getChildren().clear());
            logger.info("Создание наполнения");

            for (Parameter parameter : MainController.mainParameters.getPeripheryParameters()) {
                if (parameter.getAttributes() != null) {
                    ElementTitledPane ElementTitledPane;
                    try {
                        ElementTitledPane = new ElementTitledPane(parameter);
                        ElementTitledPane finalElementTitledPane1 = ElementTitledPane;
                        Platform.runLater(() -> peripherySettingsVbox.getChildren().add(finalElementTitledPane1));
                    } catch (Exception e) {
                        Platform.runLater(this::closeLoadWindow);
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
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            Platform.runLater(this::closeLoadWindow);
        };
        loadingThread = new Thread(task);
        loadingThread.start();
    }
}
