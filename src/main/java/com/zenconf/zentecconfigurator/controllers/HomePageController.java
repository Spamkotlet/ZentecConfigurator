package com.zenconf.zentecconfigurator.controllers;

import com.zenconf.zentecconfigurator.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class HomePageController implements Initializable {

    public static final Map<String, Node> panels = new HashMap<>();

    @FXML
    public Button goToConfiguratorButton;
    @FXML
    public Button goToZ031Button;
    @FXML
    public VBox mainViewVBox;
    public static VBox mainViewVBox1;

    private static final Logger logger = LogManager.getLogger(HomePageController.class);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mainViewVBox1 = mainViewVBox;

        goToConfiguratorButton.setOnAction(e -> onClickButton("Конфигуратор", "/com/zenconf/zentecconfigurator/fxml/homepage/configurator-view.fxml"));
        goToZ031Button.setOnAction(e -> onClickButton("ПУ Z031", "/com/zenconf/zentecconfigurator/fxml/homepage/z031-settings.fxml"));

        Image configuratorImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/configurator_button.png")));
        ImageView configuratorImageView = new ImageView(configuratorImage);
        configuratorImageView.setFitHeight(120);
        configuratorImageView.setFitWidth(120);
        goToConfiguratorButton.graphicProperty().setValue(configuratorImageView);
        goToConfiguratorButton.getStyleClass().add("button-home-page");
        goToConfiguratorButton.setText("");

        Image Z031Image = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/z031_button.png")));
        ImageView Z031ImageView = new ImageView(Z031Image);
        Z031ImageView.setFitHeight(120);
        Z031ImageView.setFitWidth(120);
        goToZ031Button.graphicProperty().setValue(Z031ImageView);
        goToZ031Button.getStyleClass().add("button-home-page");
        goToZ031Button.setText("");
    }

    private void onClickButton(String panelName, String resourcePath) {
        Node node = panels.get(panelName);
        if (node == null) {
            try {
                node = createNewNode(resourcePath);
            } catch (IOException e) {
                logger.error(e.getStackTrace());
                throw new RuntimeException(e);
            }
            panels.put(panelName, node);
        }
        logger.info("Открыть окно <" + panelName + ">");
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
