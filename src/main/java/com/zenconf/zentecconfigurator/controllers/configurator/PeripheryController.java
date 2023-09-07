package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.MainController;
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
import java.util.ResourceBundle;

public class PeripheryController implements Initializable {

    @FXML
    public VBox peripherySettingsVbox;
    @FXML
    public VBox peripheryVBox;
    @FXML
    public AnchorPane transparentPane;
    @FXML
    public ProgressBar progressBar;

    private static final Logger logger = LogManager.getLogger(PeripheryController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        fillPeripherySettingsPane();
    }

    private void fillPeripherySettingsPane() {
        Thread thread;
        Runnable task = () -> {
            transparentPane.setVisible(true);
            progressBar.setVisible(true);
            Platform.runLater(() -> peripherySettingsVbox.getChildren().clear());
            logger.info("Создание наполнения");

            for (Parameter parameter : MainController.mainParameters.getPeripheryParameters()) {
                if (parameter.getAttributes() != null) {
                    ElementTitledPane elementTitledPane;
                    try {
                        elementTitledPane = new ElementTitledPane(parameter);
                        ElementTitledPane finalElementTitledPane1 = elementTitledPane;
                        Platform.runLater(() -> peripherySettingsVbox.getChildren().add(finalElementTitledPane1));
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
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
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
