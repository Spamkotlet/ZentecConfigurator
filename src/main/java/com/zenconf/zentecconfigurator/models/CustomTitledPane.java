package com.zenconf.zentecconfigurator.models;

import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;

public class CustomTitledPane extends TitledPane {

//    private String header;
    private Label name;
    private CheckBox checkBox;
    private Spinner spinner;

    public CustomTitledPane() {

    }

    public CustomTitledPane(String header) {
        this.setText(header);
    }

    public CustomTitledPane(String header, String parameterName) {
        this(header);
        Label parameterLabel = new Label();
        parameterLabel.setText(parameterName);
        this.setContent(parameterLabel);
    }
}
