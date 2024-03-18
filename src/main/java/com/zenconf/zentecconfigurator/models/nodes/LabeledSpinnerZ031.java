package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;

import java.util.ArrayList;
import java.util.List;

public class LabeledSpinnerZ031 {

    private Spinner<Integer> spinner;
    private String errorText = "*";
    private Label errorLabel;
    private final Attribute attribute;
    private int labelWidth = 200;
    private boolean showDefaultValue = false;
    private boolean readingByInitialization = false;
    private boolean _enabled = true;

    public LabeledSpinnerZ031(Attribute attribute, int labelWidth, boolean showDefaultValue) {
        this.attribute = attribute;
        this.labelWidth = labelWidth;
        this.showDefaultValue = showDefaultValue;
    }

    public LabeledSpinnerZ031(Attribute attribute, int labelWidth, boolean showDefaultValue, boolean _enabled) {
        this.attribute = attribute;
        this.labelWidth = labelWidth;
        this.showDefaultValue = showDefaultValue;
        this._enabled = _enabled;
    }

    public Node getSpinner() throws Exception {
        String labelText = attribute.getName();
        int minValue = attribute.getMinValue();
        int maxValue = attribute.getMaxValue();

        List<Node> nodes = new ArrayList<>();
        // Label с названием атрибута
        nodes.add(createLabel(labelText, labelWidth));
        // Label с ошибкой
        errorLabel = createErrorLabel("(" + errorText + ")");
        // Спиннер
        nodes.add(createSpinner(minValue, maxValue));
        // Label c диапазоном
        nodes.add(createLabel("[" + minValue + " ... " + maxValue + "]", true));
        if (attribute.getDefaultValue() != null) {
            if (showDefaultValue) {
                // Label со значением по умолчанию
                nodes.add(createDefaultValueLabel(attribute.getDefaultValue()));
            }
        }

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

    private Label createLabel(String labelText) {
        Label label = new Label(labelText);
        label.setWrapText(true);
        if (attribute.getDescription() != null) {
            label.setTooltip(new Tooltip(attribute.getDescription()));
        }

        return label;
    }

    private Label createLabel(String labelText, boolean isDisabled) {
        Label label = createLabel(labelText);
        label.setDisable(isDisabled);

        return label;
    }

    private Label createLabel(String labelText, int labelWidth) {
        Label label = createLabel(labelText, false);
        label.setPrefWidth(labelWidth);

        return label;
    }

    private Label createErrorLabel(String labelText) {
        Label label = createLabel(labelText);
        label.setVisible(false);
        label.setTextFill(Color.RED);

        return label;
    }

    private Spinner<Integer> createSpinner(int minValue, int maxValue) throws Exception {
        int initValue = 0;

        if (showDefaultValue && !readingByInitialization && _enabled) {
            if (attribute.getDefaultValue() != null) {
                initValue = (int) attribute.getDefaultValue();
            }
        }

        if (readingByInitialization && _enabled) {
            try {
                errorLabel.setVisible(false);
                initValue = Integer.parseInt(attribute.readModbus());
            } catch (Exception e) {
                errorText = "Ошибка чтения";
                Platform.runLater(() -> errorLabel.setText(errorText));
                errorLabel.setVisible(true);
                throw e;
            }
        }

        spinner = new Spinner<>(minValue, maxValue, initValue);

        spinner.setEditable(!_enabled);
        spinner.setPrefWidth(200);
        if (!_enabled) {
            return spinner;
        }
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

        if (attribute.getDefaultValue() != null) {
            if (spinner.getValue() != Integer.parseInt(attribute.getDefaultValue().toString())) {
                Platform.runLater(() -> spinner.getEditor().setBackground(new Background(new BackgroundFill(Color.color(0.961, 0.545, 0, 0.35), CornerRadii.EMPTY, Insets.EMPTY))));
            }
        }

        spinner.getValueFactory().valueProperty().addListener(e -> {
            if (attribute.getDefaultValue() != null) {
                if (spinner.getValue() != Integer.parseInt(attribute.getDefaultValue().toString())) {
                    Platform.runLater(() -> spinner.getEditor().setBackground(new Background(new BackgroundFill(Color.color(0.961, 0.545, 0, 0.35), CornerRadii.EMPTY, Insets.EMPTY))));
                } else {
                    Platform.runLater(() -> spinner.getEditor().setBackground(Background.EMPTY));
                }
            }
        });

        spinner.setOnMouseReleased(e -> writeModbusValue());

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
        label.setTextFill(new Color(0.992, 0.4, 0.243, 1));

        return label;
    }

    public void readModbusValue() throws Exception {
        int value = 0;
        try {
            errorLabel.setVisible(false);
            value = Integer.parseInt(attribute.readModbus());
        } catch (Exception e) {
            errorText = "Ошибка чтения";
            Platform.runLater(() -> errorLabel.setText(errorText));
            errorLabel.setVisible(true);
            throw e;
        }
        spinner.getValueFactory().setValue(value);
    }

    public void writeModbusValue() {
        try {
            errorLabel.setVisible(false);
            attribute.writeModbus(spinner.getValue().toString());
        } catch (Exception e) {
            errorText = "Ошибка записи";
            Platform.runLater(() -> errorLabel.setText(errorText));
            errorLabel.setVisible(true);
            throw new RuntimeException(e);
        }
    }

    public void setSpinnerStyleLikeDefault() {
        if (attribute.getDefaultValue() != null) {
            if (spinner.getValue() == Integer.parseInt(attribute.getDefaultValue().toString())) {
                spinner.getEditor().setBackground(Background.EMPTY);
            }
        }
    }

    public void setDefaultValue() {
        spinner.getValueFactory().setValue((Integer) attribute.getDefaultValue());
        setSpinnerStyleLikeDefault();
    }

    public Attribute getAttribute() {
        return attribute;
    }
}