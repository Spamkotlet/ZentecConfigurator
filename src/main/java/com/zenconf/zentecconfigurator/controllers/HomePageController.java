package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {

    private final Map<Button, Node> panels = new HashMap<>();

    @FXML
    public Button goToTestButton;
    @FXML
    public Button goToConfiguratorButton;
    @FXML
    public Button goToZ031Button;
    @FXML
    public VBox mainViewVBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        goToConfiguratorButton.setOnAction(this::onClickConfiguratorButton);
        goToZ031Button.setOnAction(this::onClickZ031Button);

        Image testImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/test_button.png")));
        ImageView testImageView = new ImageView(testImage);
        testImageView.setFitHeight(80);
        testImageView.setFitWidth(80);
        goToTestButton.graphicProperty().setValue(testImageView);
        goToTestButton.setStyle("-fx-background-color: #BFBFBF;");
        goToTestButton.setText("");

        Image configuratorImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/configurator_button.png")));
        ImageView configuratorImageView = new ImageView(configuratorImage);
        configuratorImageView.setFitHeight(80);
        configuratorImageView.setFitWidth(80);
        goToConfiguratorButton.graphicProperty().setValue(configuratorImageView);
        goToConfiguratorButton.setStyle("-fx-background-color: #BFBFBF;");
        goToConfiguratorButton.setText("");

        Image Z031Image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/z031_button.png")));
        ImageView Z031ImageView = new ImageView(Z031Image);
        Z031ImageView.setFitHeight(80);
        Z031ImageView.setFitWidth(80);
        goToZ031Button.graphicProperty().setValue(Z031ImageView);
        goToZ031Button.setStyle("-fx-background-color: #BFBFBF;");
        goToZ031Button.setText("");
    }

    private void onClickConfiguratorButton(ActionEvent event) {
        Button button = (Button) event.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("/com/zenconf/zentecconfigurator/fxml/homepage/configurator-view.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    private void onClickZ031Button(ActionEvent event) {
        Button button = (Button) event.getSource();
        Node node = panels.get(button);
        if (node == null) {
            try {
                node = createNewNode("com/zenconf/zentecconfigurator/fxml/homepage/z031-settings.fxml");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            panels.put(button, node);
        }
        showNode(node);
    }

    private Node createNewNode(String resourcePath) throws IOException {
        FXMLLoader loader = new FXMLLoader(Application.class.getResource(resourcePath));
        return loader.load();
    }

    private void showNode(Node node) {
        AnchorPane mainAnchorPane = (AnchorPane) mainViewVBox.getParent();
        mainAnchorPane.getChildren().clear();
        mainAnchorPane.getChildren().add(node);
    }
}
