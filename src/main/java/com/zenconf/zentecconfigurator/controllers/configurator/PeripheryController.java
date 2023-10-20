package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.elements.Parameter;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

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
        Task<Void> fillingPaneTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> {
                    showLoadWindow(this);
                    peripherySettingsVbox.getChildren().clear();
                });

                boolean isSuccessfulAction = true;
                int successfulActionAttempt = 0;
                Parameter parameter = null;
                for (int i = 0; i < MainController.mainParameters.getPeripheryParameters().size(); ) {
                    if (isSuccessfulAction) {
                        updateMessage("Загрузка...: " + (i + 1) + "/" + MainController.mainParameters.getPeripheryParameters().size());
                        updateProgress(i + 1, MainController.mainParameters.getPeripheryParameters().size());
                        parameter = MainController.mainParameters.getPeripheryParameters().get(i);
                    }

                    if (parameter.getAttributes() != null) {
                        ElementTitledPane elementTitledPane = new ElementTitledPane(parameter);
                        try {
                            Platform.runLater(() -> peripherySettingsVbox.getChildren().add(elementTitledPane));
                            isSuccessfulAction = true;
                            i++;
                        } catch (Exception e) {
                            e.printStackTrace();
                            logger.error(e.getMessage());
                            isSuccessfulAction = false;
                            successfulActionAttempt++;
                            updateMessage("Попытка загрузки [" + successfulActionAttempt + "/3]\n" + parameter.getName());
                            if (successfulActionAttempt >= 3) {
                                isSuccessfulAction = true;
                                successfulActionAttempt = 0;
                                i++;
                                updateMessage("Ошибка загрузки\n" + parameter.getName());
                                Thread.sleep(1000);
                            }
                            Thread.sleep(1000);
                        }

                        Thread.sleep(100);
                    }
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                logger.info("Задача fillingPeripheryPaneTask выполнена успешно");
                Platform.runLater(() -> closeLoadWindow(this));
                Thread.currentThread().interrupt();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                logger.info("Задача fillingPeripheryPaneTask прервана");
                Thread.currentThread().interrupt();
            }

            @Override
            protected void failed() {
                super.failed();
                logger.info("Задача fillingPeripheryPaneTask завершилась ошибкой");
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
    }
}
