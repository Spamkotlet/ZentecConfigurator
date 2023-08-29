package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.MainParameters;
import com.zenconf.zentecconfigurator.models.Parameter;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

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
    private MainParameters mainParameters;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainParameters = MainController.mainParameters;
        peripheryVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                fillPeripherySettingsPane();
            }
        });
    }

    private void fillPeripherySettingsPane() {
        Thread thread;
        Runnable task = () -> {
            transparentPane.setVisible(true);
            progressBar.setVisible(true);
            Platform.runLater(() -> peripherySettingsVbox.getChildren().clear());

            for (Parameter parameter : mainParameters.getPeripheryParameters()) {
                if (parameter.getAttributes() != null) {
                    ElementTitledPane elementTitledPane = null;
                    try {
                        elementTitledPane = new ElementTitledPane(parameter);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    ElementTitledPane finalElementTitledPane = elementTitledPane;
                    Platform.runLater(() -> peripherySettingsVbox.getChildren().add(finalElementTitledPane));
                    try {
                        Thread.sleep(20);
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
