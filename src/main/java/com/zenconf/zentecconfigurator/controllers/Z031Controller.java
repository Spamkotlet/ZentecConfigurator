package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.enums.VarFunctions;
import com.zenconf.zentecconfigurator.models.nodes.LabeledSpinner;
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

        List<LabeledSpinner> electricLabeledSpinnerList = new ArrayList<>();
        electricParametersVBox.getChildren().clear();
        if (electricParameters != null) {
            for (Attribute attribute : electricParameters.getAttributes()) {
                LabeledSpinner labeledSpinner = new LabeledSpinner(attribute, 60, true);
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

        List<LabeledSpinner> waterLabeledSpinnerList = new ArrayList<>();
        waterParametersVBox.getChildren().clear();
        if (waterParameters != null) {
            for (Attribute attribute : waterParameters.getAttributes()) {
                LabeledSpinner labeledSpinner = new LabeledSpinner(attribute, 60, true);
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

        List<LabeledSpinner> z031LabeledSpinnerList = new ArrayList<>();
        z031ParametersVBox.getChildren().clear();
        if (waterParameters != null) {
            for (Attribute attribute : waterParameters.getAttributes()) {
                LabeledSpinner labeledSpinner = new LabeledSpinner(attribute, 60);
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

    private void writeParameters(List<LabeledSpinner> labeledSpinnerList) {
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

    private void readParameters(List<LabeledSpinner> labeledSpinnerList) {
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

    private void setDefaultParameters(List<LabeledSpinner> labeledSpinnerList) {
        logger.info("Вернуть параметры по умолчанию");
        for (LabeledSpinner spinner : labeledSpinnerList) {
            spinner.setDefaultValue();
        }
    }

    private Thread getThread(List<LabeledSpinner> labeledSpinnerList, VarFunctions varFunctions) {
        Thread thread;
        Task<Void> fillingPaneTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> showLoadWindow(this));

                int parametersDone = 0;
                int parametersMax = labeledSpinnerList.size();
                for (LabeledSpinner spinner: labeledSpinnerList) {
                    parametersDone++;
                    updateMessage("Загрузка...: " + parametersDone + "/" + parametersMax);
                    updateProgress(parametersDone, parametersMax);
                    if (varFunctions.equals(VarFunctions.WRITE)) {
                        try {
                            spinner.writeModbusValue();
                        } catch (Exception e) {
                            logger.error(e.getMessage());
                            throw new RuntimeException(e);
                        }
                    } else if (varFunctions.equals(VarFunctions.READ)) {
                        spinner.readModbusValue();
                    }
                    Thread.sleep(50);
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
        thread = new Thread(fillingPaneTask);
        return thread;
    }
}
