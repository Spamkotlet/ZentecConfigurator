package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.enums.VarFunctions;
import com.zenconf.zentecconfigurator.models.nodes.LabeledSpinnerZ031;
import com.zenconf.zentecconfigurator.models.z031.ElectricParameters;
import com.zenconf.zentecconfigurator.models.z031.WaterParameters;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

// TODO: Добавить сравнение параметров после записи с прочитанными из пульта. Помечать поля где расхождение
public class Z031Controller extends CommonController implements Initializable {

    @FXML
    public VBox electricParametersVBox;
    @FXML
    public VBox waterParametersVBox;
    @FXML
    public VBox z031ParametersVBox;
    @FXML
    public Button readZ031ParametersButton;
    @FXML
    public Button writeElectricParametersButton;
    @FXML
    public Button writeWaterParametersButton;
    @FXML
    public Button resetDefaultElectricButton;
    @FXML
    public Button resetDefaultWaterButton;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public AnchorPane transparentPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ElectricParameters electricParameters = MainController.electricParameters;
        WaterParameters waterParameters = MainController.waterParameters;

        List<LabeledSpinnerZ031> electricLabeledSpinnerList = new ArrayList<>();
        electricParametersVBox.getChildren().clear();
        if (electricParameters != null) {
            for (Attribute attribute : electricParameters.getAttributes()) {
                LabeledSpinnerZ031 labeledSpinner = new LabeledSpinnerZ031(attribute, 60, true);
                try {
                    electricParametersVBox.getChildren().add(labeledSpinner.getSpinner());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                electricLabeledSpinnerList.add(labeledSpinner);
            }
        }
        writeElectricParametersButton.setOnAction(e -> writeParameters(electricLabeledSpinnerList));
        resetDefaultElectricButton.setOnAction(e -> setDefaultParameters(electricLabeledSpinnerList));

        List<LabeledSpinnerZ031> waterLabeledSpinnerList = new ArrayList<>();
        waterParametersVBox.getChildren().clear();
        if (waterParameters != null) {
            for (Attribute attribute : waterParameters.getAttributes()) {
                LabeledSpinnerZ031 labeledSpinner = new LabeledSpinnerZ031(attribute, 60, true);
                try {
                    waterParametersVBox.getChildren().add(labeledSpinner.getSpinner());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                waterLabeledSpinnerList.add(labeledSpinner);
            }
        }
        writeWaterParametersButton.setOnAction(e -> writeParameters(waterLabeledSpinnerList));
        resetDefaultWaterButton.setOnAction(e -> setDefaultParameters(waterLabeledSpinnerList));

        List<LabeledSpinnerZ031> z031LabeledSpinnerList = new ArrayList<>();
        z031ParametersVBox.getChildren().clear();
        if (waterParameters != null) {
            for (int i = 0; i < waterParameters.getAttributes().size(); i++) {
                LabeledSpinnerZ031 labeledSpinner = new LabeledSpinnerZ031(waterParameters.getAttributes().get(i), 60, false, false);
                try {
                    z031ParametersVBox.getChildren().add(labeledSpinner.getSpinner());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                z031LabeledSpinnerList.add(labeledSpinner);
            }
        }
        readZ031ParametersButton.setOnAction(e -> readParameters(z031LabeledSpinnerList));
        readParameters(z031LabeledSpinnerList);
    }

    private void writeParameters(List<LabeledSpinnerZ031> labeledSpinnerList) {
        try {
            logger.info("Запись параметров в пульт");
            Thread thread = getThread(labeledSpinnerList, VarFunctions.WRITE);
            thread.start();
        } catch (Exception exception) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка при записи в ПУ\n" + exception.getMessage());
                alert.setHeaderText("Ошибка Modbus");
                alert.show();
            });
            logger.error(exception.getMessage());
            throw exception;
        }

    }

    private void readParameters(List<LabeledSpinnerZ031> labeledSpinnerList) {
        try {
            logger.info("Чтение параметров из пульта");
            Thread thread = getThread(labeledSpinnerList, VarFunctions.READ);
            thread.start();
        } catch (Exception exception) {
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Ошибка при чтении из ПУ\n" + exception.getMessage());
                alert.setHeaderText("Ошибка Modbus");
                alert.show();
            });
            logger.error(exception.getMessage());
            throw exception;
        }
    }

    private void setDefaultParameters(List<LabeledSpinnerZ031> labeledSpinnerList) {
        logger.info("Вернуть параметры по умолчанию");
        for (LabeledSpinnerZ031 spinner : labeledSpinnerList) {
            spinner.setDefaultValue();
        }
    }

    private Thread getThread(List<LabeledSpinnerZ031> labeledSpinnerList, VarFunctions varFunctions) {
        Thread thread;
        Task<Void> fillingPaneTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> showLoadWindow(this));

                int parametersMax = labeledSpinnerList.size();
                LabeledSpinnerZ031 spinner = null;
                boolean isSuccessfulAction = true;
                int successfulActionAttempt = 0;
                for (int i = 0; i < labeledSpinnerList.size(); ) {
                    if (isSuccessfulAction) {
                        spinner = labeledSpinnerList.get(i);
                        updateMessage("Загрузка " + spinner.getAttribute().getName() + "\n[" + (i + 1) + "/" + parametersMax + "]");
                        updateProgress(i + 1, parametersMax);
                    }
                    try {
                        if (varFunctions.equals(VarFunctions.WRITE)) {
                            spinner.writeModbusValue();
                            isSuccessfulAction = true;
                            i++;
                        } else if (varFunctions.equals(VarFunctions.READ)) {
                            spinner.readModbusValue();
                            i++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        isSuccessfulAction = false;
                        successfulActionAttempt++;
                        updateMessage("Попытка загрузки " + spinner.getAttribute().getName() + "\n[" + successfulActionAttempt + "/3]");
                        if (successfulActionAttempt >= 3) {
                            isSuccessfulAction = true;
                            successfulActionAttempt = 0;
                            i++;
                            updateMessage("Ошибка загрузки");
                            Thread.sleep(1000);
                        }
                        Thread.sleep(1000);
                    }

                    Thread.sleep(100);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                logger.info("Задача fillingZ031PaneTask выполнена успешно");
                Platform.runLater(() -> closeLoadWindow(this));
                Thread.currentThread().interrupt();
            }

            @Override
            protected void cancelled() {
                super.cancelled();
                logger.info("Задача fillingZ031PaneTask прервана");
                Thread.currentThread().interrupt();
            }

            @Override
            protected void failed() {
                super.failed();
                logger.info("Задача fillingZ031PaneTask завершилась ошибкой");
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
        thread = new Thread(fillingPaneTask);
        return thread;
    }
}
