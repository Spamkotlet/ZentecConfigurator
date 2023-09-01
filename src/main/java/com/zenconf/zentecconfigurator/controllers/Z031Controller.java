package com.zenconf.zentecconfigurator.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.enums.VarFunctions;
import com.zenconf.zentecconfigurator.models.nodes.LabeledSpinner;
import com.zenconf.zentecconfigurator.models.z031.ElectricParameters;
import com.zenconf.zentecconfigurator.models.z031.WaterParameters;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

// TODO: Добавить сравнение параметров после записи с прочитанными из пульта. Помечать поля где расхождение
public class Z031Controller implements Initializable {

    private ElectricParameters electricParameters;
    private WaterParameters waterParameters;

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

    private final static Logger logger = LogManager.getLogger(Z031Controller.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        electricParameters = MainController.electricParameters;
        waterParameters = MainController.waterParameters;

        List<LabeledSpinner> electricLabeledSpinnerList = new ArrayList<>();
        electricParametersVBox.getChildren().clear();
        if (electricParameters != null) {
            for (Attribute attribute : electricParameters.getAttributes()) {
                LabeledSpinner labeledSpinner = new LabeledSpinner(attribute, 60, true);
                electricParametersVBox.getChildren().add(labeledSpinner.getSpinner());
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
                waterParametersVBox.getChildren().add(labeledSpinner.getSpinner());
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
                z031ParametersVBox.getChildren().add(labeledSpinner.getSpinner());
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
        } catch (RuntimeException exception) {
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
            logger.info("Запись параметров из пульта");
            Thread thread = getThread(labeledSpinnerList, VarFunctions.READ);
            thread.start();
        } catch (RuntimeException exception) {
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
        Runnable task = () -> {
            transparentPane.setVisible(true);
            progressBar.setVisible(true);
            double progress = 0;
            double progressStep = 1d / labeledSpinnerList.size();
            for (LabeledSpinner spinner: labeledSpinnerList) {
                progress += progressStep;
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
                progressBar.setProgress(progress);
            }
            progressBar.setProgress(0.0);
            progressBar.setVisible(false);
            transparentPane.setVisible(false);
        };
        thread = new Thread(task);
        return thread;
    }
}
