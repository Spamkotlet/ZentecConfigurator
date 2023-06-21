package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class LabeledSpinner {

    private final Attribute attribute;

    public LabeledSpinner(Attribute attribute) {
        this.attribute = attribute;
    }

    public Node getSpinner() {
        String labelText = attribute.getName();
        int minValue = attribute.getMinValue();
        int maxValue = attribute.getMaxValue();

        AnchorPane labelAnchor = createLabelAnchor(labelText);
        AnchorPane spinnerAnchor = createSpinnerAnchor(minValue, maxValue);

        return createHBoxForLabeledSpinner(labelAnchor, spinnerAnchor);
    }

    private HBox createHBoxForLabeledSpinner(Node... nodes) {
        HBox hBox = new HBox();
        hBox.getChildren().addAll(nodes);
        hBox.setFillHeight(true);
        hBox.setSpacing(10);
        HBox.setHgrow(hBox, Priority.ALWAYS);

        AnchorPane.setLeftAnchor(hBox, 0.0);
        AnchorPane.setRightAnchor(hBox, 0.0);
        AnchorPane.setTopAnchor(hBox, 0.0);
        AnchorPane.setBottomAnchor(hBox, 0.0);

        return hBox;
    }

    private AnchorPane createLabelAnchor(String labelText) {
        Label label = new Label(labelText);
        label.setPrefWidth(200);

        AnchorPane labelAnchor = new AnchorPane();

        labelAnchor.getChildren().add(label);
//        HBox.setHgrow(labelAnchor, Priority.ALWAYS);
        AnchorPane.setLeftAnchor(labelAnchor, 0.0);
        AnchorPane.setRightAnchor(labelAnchor, 0.0);
        AnchorPane.setTopAnchor(labelAnchor, 0.0);
        AnchorPane.setBottomAnchor(labelAnchor, 0.0);

        return labelAnchor;
    }

    private AnchorPane createSpinnerAnchor(int minValue, int maxValue) {
        int initValue = 0;
        Spinner<Double> spinner = new Spinner<>(minValue, maxValue, initValue);
        spinner.setEditable(true);
        spinner.setPrefWidth(200);

        AnchorPane spinnerAnchor = new AnchorPane();

        spinnerAnchor.getChildren().add(spinner);
//        HBox.setHgrow(spinnerAnchor, Priority.ALWAYS);
        AnchorPane.setLeftAnchor(spinnerAnchor, 0.0);
        AnchorPane.setRightAnchor(spinnerAnchor, 0.0);
        AnchorPane.setTopAnchor(spinnerAnchor, 0.0);
        AnchorPane.setBottomAnchor(spinnerAnchor, 0.0);

        return spinnerAnchor;
    }
}
