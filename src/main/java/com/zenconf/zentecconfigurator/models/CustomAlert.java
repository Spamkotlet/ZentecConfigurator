package com.zenconf.zentecconfigurator.models;

import javafx.scene.control.Alert;

public class CustomAlert extends Alert {
    public CustomAlert(AlertType alertType) {
        super(alertType);
    }
}