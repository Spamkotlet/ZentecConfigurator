package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Element;
import com.zenconf.zentecconfigurator.models.MainParameters;
import com.zenconf.zentecconfigurator.models.nodes.ElementTitledPane;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actuatorsVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                actuatorsInScheme = ChangeSchemeController.actuatorsInScheme;

                if (actuatorsInScheme != null) {
                    fillActuatorsSettingsPane();
                }

            }
        });
        mainParameters = MainParameters.getMainParametersFromJson();
    }

    private void fillActuatorsSettingsPane() {
        Thread thread;
        Runnable task = () -> {
            transparentPane.setVisible(true);
            progressBar.setVisible(true);
            Platform.runLater(() -> actuatorsSettingsVbox.getChildren().clear());

            Actuator heatExchangerElement = new Actuator();
            heatExchangerElement.setName("Теплообменники");
            heatExchangerElement.setAttributes(mainParameters.getHeatExchangerAttributes());
            ElementTitledPane heatExchangerTitledPane = new ElementTitledPane(heatExchangerElement);
            Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(heatExchangerTitledPane));

            for (Actuator actuatorInScheme : actuatorsInScheme) {
                if (actuatorInScheme.getIsUsedDefault()) {
                    if (actuatorInScheme.getAttributes() != null) {
                        ElementTitledPane elementTitledPane = new ElementTitledPane(actuatorInScheme);
                        Platform.runLater(() -> actuatorsSettingsVbox.getChildren().add(elementTitledPane));
                        try {
                            Thread.sleep(20);
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
