package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class LabeledSpinner {

    private final Attribute attribute;
    private final int labelWidth;

    public LabeledSpinner(Attribute attribute, int labelWidth) {
        this.attribute = attribute;
        this.labelWidth = labelWidth;
    }

    public Node getSpinner() {
        String labelText = attribute.getName();
        double minValue = attribute.getMinValue();
        double maxValue = attribute.getMaxValue();

        List<Node> nodes = new ArrayList<>();
        nodes.add(createLabelAnchor(labelText));
        nodes.add(createSpinnerAnchor(minValue, maxValue));
        if (attribute.getDefaultValue() != null) {
            nodes.add(createDefaultValueLabelAnchor(attribute.getDefaultValue()));
        }

        return createHBoxForLabeledSpinner(nodes);
    }

    private HBox createHBoxForLabeledSpinner(List<Node> nodes) {
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
        label.setPrefWidth(labelWidth);
        if (attribute.getDescription() != null) {
            label.setTooltip(new Tooltip(attribute.getDescription()));
        }

        AnchorPane labelAnchor = new AnchorPane();

        labelAnchor.getChildren().add(label);
        AnchorPane.setLeftAnchor(labelAnchor, 0.0);
        AnchorPane.setRightAnchor(labelAnchor, 0.0);
        AnchorPane.setTopAnchor(labelAnchor, 0.0);
        AnchorPane.setBottomAnchor(labelAnchor, 0.0);

        return labelAnchor;
    }

    private AnchorPane createSpinnerAnchor(double minValue, double maxValue) {
        int initValue = 0;
        Spinner<Double> spinner = new Spinner<>(minValue, maxValue, initValue, 0.1);
        spinner.setEditable(true);
        spinner.setPrefWidth(200);
        SpinnerValueFactory<Double> spinnerFactory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(
                        attribute.getMinValue(), attribute.getMaxValue(), Double.parseDouble(attribute.readModbusParameter()));
        spinner.setValueFactory(spinnerFactory);
        spinner.setOnMouseReleased(e -> {
            attribute.writeModbusParameter(spinner.getValue().toString());
            System.out.println("Value: " + spinner.getValue());
        });

        spinner.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                attribute.writeModbusParameter(Float.parseFloat(spinner.getValue().toString()));
                System.out.println("Value: " + spinner.getValue());
            }
        });

        AnchorPane spinnerAnchor = new AnchorPane();

        spinnerAnchor.getChildren().add(spinner);
        AnchorPane.setLeftAnchor(spinnerAnchor, 0.0);
        AnchorPane.setRightAnchor(spinnerAnchor, 0.0);
        AnchorPane.setTopAnchor(spinnerAnchor, 0.0);
        AnchorPane.setBottomAnchor(spinnerAnchor, 0.0);

        return spinnerAnchor;
    }

    private AnchorPane createDefaultValueLabelAnchor(Object labelText) {
        Label label = new Label("(" + labelText + ")");
        label.setPrefWidth(labelWidth);
        label.setTextFill(Color.GRAY);

        AnchorPane labelAnchor = new AnchorPane();

        labelAnchor.getChildren().add(label);
        AnchorPane.setLeftAnchor(labelAnchor, 0.0);
        AnchorPane.setRightAnchor(labelAnchor, 0.0);
        AnchorPane.setTopAnchor(labelAnchor, 0.0);
        AnchorPane.setBottomAnchor(labelAnchor, 0.0);

        return labelAnchor;
    }
}
