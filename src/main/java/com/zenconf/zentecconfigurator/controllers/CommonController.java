package com.zenconf.zentecconfigurator.controllers;

import javafx.stage.Stage;

public abstract class CommonController {

    private Stage primaryStage;

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
