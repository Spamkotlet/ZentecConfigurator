package com.zenconf.zentecconfigurator.controllers.configurator;

import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Actuator;
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
        mainParameters = MainController.mainParameters;
        actuatorsVBox.sceneProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                actuatorsInScheme = ChangeSchemeController.actuatorsInScheme;

                if (actuatorsInScheme != null) {
                    fillActuatorsSettingsPane();
                }
            }
        });
    }

    private void fillActuatorsSettingsPane() {
        Thread thread;
        Runnable task = () -> {
            transparentPane.setVisible(true);
            progressBar.setVisible(true);
            Platform.runLater(() -> actuatorsSettingsVbox.getChildren().clear());

            Parameter heatExchangerParameter = new Parameter();
            heatExchangerParameter.setName("Теплообменники");
            heatExchangerParameter.setAttributes(mainParameters.getHeatExchangerAttributes());
            ElementTitledPane heatExchangerTitledPane = new ElementTitledPane(heatExchangerParameter);
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
