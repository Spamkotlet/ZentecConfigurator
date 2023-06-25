package com.zenconf.zentecconfigurator;

import com.zenconf.zentecconfigurator.controllers.MainController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BackgroundImage;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Application extends javafx.application.Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("main-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setMinHeight(800);
        stage.setMinWidth(1280);
        stage.setTitle("Zentec Configurator");
        stage.setScene(scene);
        stage.show();

        MainController mainController = fxmlLoader.getController();
        mainController.setPrimaryStage(stage);

//        ImageView schemeImageView = (ImageView) scene.lookup("#schemeButton");
//        ImageView sensorsImageView = (ImageView) scene.lookup("#sensorsButton");
//        ImageView actuatorsImageView = (ImageView) scene.lookup("#actuatorsButton");
//        ImageView inOutsImageView = (ImageView) scene.lookup("#inOutsButton");
//        ImageView settingsImageView = (ImageView) scene.lookup("#settingsButton");
//        Image schemeImage = new Image(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/scheme_button.png"));
//        Image sensorsImage = new Image(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/sensors_button.png"));
//        Image actuatorsImage = new Image(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/actuators_button.png"));
//        Image inOutsImage = new Image(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/io_button.png"));
//        Image settingsImage = new Image(getClass().getResourceAsStream("/com/zenconf/zentecconfigurator/graphics/settings_button.png"));
//        schemeImageView.setImage(schemeImage);
//        sensorsImageView.setImage(sensorsImage);
//        actuatorsImageView.setImage(actuatorsImage);
//        inOutsImageView.setImage(inOutsImage);
//        settingsImageView.setImage(settingsImage);
    }

    public static void main(String[] args) {
        launch();
    }
}