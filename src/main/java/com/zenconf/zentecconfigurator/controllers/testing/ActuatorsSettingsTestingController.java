package com.zenconf.zentecconfigurator.controllers.testing;

import com.zenconf.zentecconfigurator.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ActuatorsSettingsTestingController implements Initializable {

    @FXML
    public Button forwardButton;
    @FXML
    public Button backButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        forwardButton.setOnAction(e -> {
            Scene currentScene = forwardButton.getScene();
            Parent root = currentScene.getRoot();
            try {
                Node testingPanel = FXMLLoader.load(Application.class.getResource("fxml/testing/inouts-settings-testing.fxml"));
                AnchorPane splitPaneRight = (AnchorPane) root.lookup("#splitPaneRight");
                splitPaneRight.getChildren().clear();
                splitPaneRight.getChildren().add(testingPanel);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        backButton.setOnAction(e -> {
            Scene currentScene = forwardButton.getScene();
            Parent root = currentScene.getRoot();
            try {
                Node testingPanel = FXMLLoader.load(Application.class.getResource("fxml/testing/sensors-settings-testing.fxml"));
                AnchorPane splitPaneRight = (AnchorPane) root.lookup("#splitPaneRight");
                splitPaneRight.getChildren().clear();
                splitPaneRight.getChildren().add(testingPanel);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

}
