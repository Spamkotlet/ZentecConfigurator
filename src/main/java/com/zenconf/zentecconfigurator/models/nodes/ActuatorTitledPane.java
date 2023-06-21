package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Controls;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ActuatorTitledPane extends TitledPane {

    public ActuatorTitledPane() {

    }

    public ActuatorTitledPane(Actuator actuator) {

        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));

        for (Attribute attribute : actuator.getAttributes()) {
            if (attribute.getControl() == Controls.SPINNER) {
                vBox.getChildren().add(newSpinner(attribute));
            }
        }

        AnchorPane mainAnchor = new AnchorPane();
        mainAnchor.getChildren().add(vBox);

        this.setText(actuator.getName());
        this.setContent(mainAnchor);
    }

    private Node newSpinner(Attribute attribute) {
        Label label = new Label(attribute.getName());
        Spinner<Double> spinner = new Spinner<>(attribute.getMinValue(), attribute.getMaxValue(), 0);

        AnchorPane labelAnchor = new AnchorPane();
        labelAnchor.getChildren().add(label);
        HBox.setHgrow(labelAnchor, Priority.ALWAYS);
        AnchorPane.setLeftAnchor(labelAnchor, 0.0);
        AnchorPane.setRightAnchor(labelAnchor, 0.0);
        AnchorPane.setTopAnchor(labelAnchor, 0.0);
        AnchorPane.setBottomAnchor(labelAnchor, 0.0);

        AnchorPane spinnerAnchor = new AnchorPane();
        spinnerAnchor.getChildren().add(spinner);
        AnchorPane.setLeftAnchor(spinnerAnchor, 0.0);
        AnchorPane.setRightAnchor(spinnerAnchor, 0.0);
        AnchorPane.setTopAnchor(spinnerAnchor, 0.0);
        AnchorPane.setBottomAnchor(spinnerAnchor, 0.0);

        HBox hBox = new HBox();
        hBox.getChildren().addAll(labelAnchor, spinnerAnchor);
        hBox.setSpacing(10);

        return hBox;
    }
}
