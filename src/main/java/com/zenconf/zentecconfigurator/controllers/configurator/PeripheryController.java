package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.CommonController;
import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Parameter;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.List;
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

                List<Parameter> parameters = MainController.mainParameters.getPeripheryParameters();
                int parametersDone = 0;
                int parametersMax = parameters.size();
                for (Parameter parameter : parameters) {
                    if (parameter.getAttributes() != null) {
                        parametersDone++;
                        updateMessage("Загрузка...: " + parametersDone + "/" + parametersMax);
                        updateProgress(parametersDone, parametersMax);
                        ElementTitledPane elementTitledPane = new ElementTitledPane(parameter);
                        try {
                            Platform.runLater(() -> peripherySettingsVbox.getChildren().add(elementTitledPane));
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
    }
}
