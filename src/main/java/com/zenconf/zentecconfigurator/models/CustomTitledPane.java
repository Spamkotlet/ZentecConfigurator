package com.zenconf.zentecconfigurator.models;

import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;

public class CustomTitledPane extends TitledPane {
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
