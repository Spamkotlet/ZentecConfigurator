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

import java.util.ArrayList;
import java.util.List;

public class LabeledSpinner {

    private Spinner<Integer> spinner;
    private final Attribute attribute;
    private int labelWidth = 200;
    private boolean showDefaultValue = false;
    private boolean readingByInitialization = false;


    public LabeledSpinner(Attribute attribute) {
        this.attribute = attribute;
    }

    public LabeledSpinner(Attribute attribute, int labelWidth) {
        this.attribute = attribute;
        this.labelWidth = labelWidth;
    }

    public LabeledSpinner(Attribute attribute, int labelWidth, boolean showDefaultValue) {
        this.attribute = attribute;
        this.labelWidth = labelWidth;
        this.showDefaultValue = showDefaultValue;
    }

    public LabeledSpinner(Attribute attribute, boolean readingByInitialization) {
        this.attribute = attribute;
        this.readingByInitialization = readingByInitialization;
    }

    public Node getSpinner() {
        String labelText = attribute.getName();
        int minValue = attribute.getMinValue();
        int maxValue = attribute.getMaxValue();

        List<Node> nodes = new ArrayList<>();
        nodes.add(createLabelAnchor(labelText));
        nodes.add(createSpinnerAnchor(minValue, maxValue));
        if (attribute.getDefaultValue() != null) {
            if (showDefaultValue) {
                nodes.add(createDefaultValueLabelAnchor(attribute.getDefaultValue()));
            }
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

    private AnchorPane createSpinnerAnchor(int minValue, int maxValue) {
        int initValue = 0;

        if (showDefaultValue) {
            if (attribute.getDefaultValue() != null) {
                initValue = (int) attribute.getDefaultValue();
            }
        }

        if (readingByInitialization) {
            initValue = (int) Double.parseDouble(attribute.readModbusParameter());
        }

        spinner = new Spinner<>(minValue, maxValue, initValue);

        spinner.setEditable(true);
        spinner.setPrefWidth(200);

        spinner.setOnMouseReleased(e -> {
            writeModbusValue();
        });

        spinner.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                writeModbusValue();
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

    public void readModbusValue() {
        SpinnerValueFactory<Integer> spinnerFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        attribute.getMinValue(), attribute.getMaxValue(), Integer.parseInt(attribute.readModbusParameter()));
        spinner.setValueFactory(spinnerFactory);
    }

    public void writeModbusValue() {
        attribute.writeModbusParameter(spinner.getValue().toString());
        System.out.println("Value: " + spinner.getValue());
    }

    public void setDefaultValue() {
        SpinnerValueFactory<Integer> spinnerFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        attribute.getMinValue(), attribute.getMaxValue(), (Integer) attribute.getDefaultValue());
        spinner.setValueFactory(spinnerFactory);
    }
}
