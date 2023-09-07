package com.zenconf.zentecconfigurator.models;

import javafx.scene.control.Alert;

public class CustomAlert extends Alert {
    public CustomAlert(AlertType alertType, String title, String header, String content) {
        super(alertType);
        super.setTitle(title);
        super.setHeaderText(header);
        super.setContentText(content);
        super.show();
    }
}
