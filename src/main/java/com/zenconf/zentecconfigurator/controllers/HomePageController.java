package com.zenconf.zentecconfigurator.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.*;

public class HomePageController extends CommonController implements Initializable {

    @FXML
    public Label currentVersionLabel;
    @FXML
    public Button goToConfiguratorButton;
    @FXML
    public Button goToZ031Button;
    @FXML
    public VBox mainViewVBox;
    public static VBox mainViewVBox1;
    private AnchorPane mainAnchorPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentVersionLabel.setText(MainController.configuratorProperties.get("Configurator.currentVersion"));

        mainViewVBox1 = mainViewVBox;
        mainAnchorPane = MainController.mainAnchorPane1;

        goToConfiguratorButton.setOnAction(e -> onCreateChildNode(mainAnchorPane, "Конфигуратор", "/com/zenconf/zentecconfigurator/fxml/homepage/configurator-view.fxml"));
        goToZ031Button.setOnAction(e -> onCreateChildNode(mainAnchorPane, "ПУ Z031", "/com/zenconf/zentecconfigurator/fxml/homepage/z031-settings.fxml"));

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

//    private void onClickButton(String panelName, String resourcePath) {
//        Node node = panels.get(panelName);
//        if (node == null) {
//            try {
//                node = createChildNode(resourcePath);
//            } catch (IOException e) {
//                logger.error(e.getStackTrace());
//                throw new RuntimeException(e);
//            }
//            panels.put(panelName, node);
//        }
//        logger.info("Открыть окно <" + panelName + ">");
//        showNode(node);
//    }
//
//    private Node createChildNode(String resourcePath) throws IOException {
//        FXMLLoader loader = new FXMLLoader(Application.class.getResource(resourcePath));
//        Node node = loader.load();
//        CommonController controller = loader.getController();
//        controller.setPrimaryStage(this.getPrimaryStage());
//        return node;
//    }
//
//    private void showNode(Node node) {
//        AnchorPane mainAnchorPane = (AnchorPane) mainViewVBox.getParent();
//        mainAnchorPane.getChildren().clear();
//        mainAnchorPane.getChildren().add(node);
//    }
}
