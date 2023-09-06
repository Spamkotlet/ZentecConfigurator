package com.zenconf.zentecconfigurator.models.nodes;

import com.intelligt.modbus.jlibmodbus.serial.SerialPortException;
import com.zenconf.zentecconfigurator.models.Attribute;
import javafx.geometry.Pos;
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
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LabeledSpinner {

    private Spinner<Integer> spinner;
    private String errorText = "*";
    private Label errorLabel;
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
        nodes.add(createLabel(labelText, labelWidth, false));
        try {
            nodes.add(createSpinner(minValue, maxValue));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        nodes.add(createLabel("[" + minValue + " ... " + maxValue + "]", 70, true));
        if (attribute.getDefaultValue() != null) {
            if (showDefaultValue) {
                nodes.add(createDefaultValueLabel(attribute.getDefaultValue()));
            }
        }

        errorLabel = createErrorLabel("(" + errorText + ")", 100, false);
        nodes.add(errorLabel);

        return createHBoxForLabeledSpinner(nodes);
    }

    private HBox createHBoxForLabeledSpinner(List<Node> nodes) {
        HBox hBox = new HBox();
        hBox.getChildren().addAll(nodes);
        hBox.setFillHeight(true);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(hBox, Priority.ALWAYS);

        AnchorPane.setLeftAnchor(hBox, 0.0);
        AnchorPane.setRightAnchor(hBox, 0.0);
        AnchorPane.setTopAnchor(hBox, 0.0);
        AnchorPane.setBottomAnchor(hBox, 0.0);

        return hBox;
    }

    private Label createLabel(String labelText, int labelWidth, boolean isDisabled) {
        Label label = new Label(labelText);
        label.setDisable(isDisabled);
        label.setPrefWidth(labelWidth);
        if (attribute.getDescription() != null) {
            label.setTooltip(new Tooltip(attribute.getDescription()));
        }

        return label;
    }

    private Label createErrorLabel(String labelText, int labelWidth, boolean isVisible) {
        Label label = createLabel(labelText, labelWidth, false);
        label.setVisible(isVisible);
        label.setTextFill(Color.RED);

        return label;
    }

    private Spinner<Integer> createSpinner(int minValue, int maxValue) throws Exception {
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
        spinner.getValueFactory().setWrapAround(true);
        spinner.getValueFactory().setConverter(
                new StringConverter<>() {
                    @Override
                    public String toString(Integer integer) {
                        return (integer == null) ? "0" : integer.toString();
                    }

                    @Override
                    public Integer fromString(String s) {
                        if (s.matches("^-?[0-9]+$")) {
                            try {
                                return Integer.valueOf(s);
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        }
                        return 0;
                    }
                }
        );

        spinner.setOnMouseReleased(e -> {
            writeModbusValue();
        });

        spinner.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal && !newVal) {
                writeModbusValue();
            }
        });

        spinner.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                writeModbusValue();
            }
        });

        return spinner;
    }

    private Label createDefaultValueLabel(Object labelText) {
        Label label = new Label("(" + labelText + ")");
        label.setPrefWidth(labelWidth);
        label.setTextFill(Color.GRAY);

        return label;
    }

    public void readModbusValue() {
        SpinnerValueFactory<Integer> spinnerFactory =
                null;
        try {
            errorLabel.setVisible(false);
            spinnerFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    attribute.getMinValue(), attribute.getMaxValue(), Integer.parseInt(attribute.readModbusParameter()));
        } catch (Exception e) {
            errorText = "Ошибка чтения";
            errorLabel.setText(errorText);
            errorLabel.setVisible(true);
            throw new RuntimeException(e);
        }
        spinner.setValueFactory(spinnerFactory);
    }

    public void writeModbusValue() {
        try {
            errorLabel.setVisible(false);
            attribute.writeModbusParameter(spinner.getValue().toString());
        } catch (Exception e) {
            errorText = "Ошибка записи";
            errorLabel.setText(errorText);
            errorLabel.setVisible(true);
            throw new RuntimeException(e);
        }
    }

    public void setDefaultValue() {
        SpinnerValueFactory<Integer> spinnerFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        attribute.getMinValue(), attribute.getMaxValue(), (Integer) attribute.getDefaultValue());
        spinner.setValueFactory(spinnerFactory);
    }
}
