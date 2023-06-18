package com.zenconf.zentecconfigurator.models;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ActuatorTitledPane extends TitledPane {

    public ActuatorTitledPane() {

    }

    public ActuatorTitledPane(Actuator actuator) {

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        for (Attribute attribute : actuator.getAttributes()) {
            if (attribute.getControl() == Controls.SPINNER) {
                vBox.getChildren().add(newSpinner(attribute));
            }
        }

        this.setText(actuator.getName());
        this.setContent(vBox);
    }

    private Node newSpinner(Attribute attribute) {
        Label label = new Label(attribute.getName());
        Spinner<Double> spinner = new Spinner<>(attribute.getMinValue(), attribute.getMaxValue(), 0);

        AnchorPane labelAnchor = new AnchorPane();
        labelAnchor.getChildren().add(label);

        AnchorPane spinnerAnchor = new AnchorPane();
        spinnerAnchor.getChildren().add(spinner);

        HBox hBox = new HBox();
        hBox.setAlignment(Pos.CENTER);
        hBox.getChildren().addAll(labelAnchor, spinnerAnchor);

        return hBox;
    }
}
