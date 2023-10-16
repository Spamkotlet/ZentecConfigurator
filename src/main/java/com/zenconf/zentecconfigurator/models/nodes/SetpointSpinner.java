package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

public class SetpointSpinner extends SetpointControl {

    private final Attribute attribute;
    private Spinner<Integer> spinner;

    public SetpointSpinner(Attribute attribute) {
        this.attribute = attribute;
    }

    public Node getSpinner() throws Exception {
        String labelText = attribute.getName();

        AnchorPane spinnerAnchor = createSpinnerAnchor();

        return createVBoxForSetpointSpinner(createLabelAnchor(labelText), spinnerAnchor);
    }

    private VBox createVBoxForSetpointSpinner(Node... nodes) {
        VBox vBox = new VBox();
        vBox.getChildren().addAll(nodes);
        vBox.setFillWidth(true);
        vBox.setSpacing(10);
        vBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox, Priority.NEVER);

        AnchorPane.setLeftAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);

        return vBox;
    }

    private Label createLabelAnchor(String labelText) {
        Label label = new Label(labelText);
        label.setPrefWidth(250);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font("Verdana", 14));

        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);

        return label;
    }

    private AnchorPane createSpinnerAnchor() throws Exception {
        AnchorPane spinnerAnchor = new AnchorPane();

        spinner = createSpinner(attribute);

        spinnerAnchor.getChildren().add(spinner);

        AnchorPane.setLeftAnchor(spinner, 0.0);
        AnchorPane.setRightAnchor(spinner, 0.0);
        AnchorPane.setTopAnchor(spinner, 0.0);
        AnchorPane.setBottomAnchor(spinner, 0.0);

        AnchorPane.setLeftAnchor(spinnerAnchor, 0.0);
        AnchorPane.setRightAnchor(spinnerAnchor, 0.0);
        AnchorPane.setTopAnchor(spinnerAnchor, 0.0);
        AnchorPane.setBottomAnchor(spinnerAnchor, 0.0);

        return spinnerAnchor;
    }

    private Spinner<Integer> createSpinner(Attribute attribute) throws Exception {
        int initValue;

        try {
            initValue = (int) Double.parseDouble(attribute.readModbus());
        } catch (Exception e) {
            throw e;
        }

        spinner = new Spinner<>(attribute.getMinValue(), attribute.getMaxValue(), initValue);
        spinner.setEditable(true);
        spinner.setPrefWidth(200);
        spinner.setPrefHeight(55);
        spinner.editorProperty().get().setAlignment(Pos.CENTER);
        spinner.editorProperty().get().setFont(Font.font("Verdana", 26));
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

        spinner.getValueFactory().valueProperty().bindBidirectional(attribute.currentValueProperty);

        spinner.getValueFactory().valueProperty().addListener(e -> {
            if (attribute.getDefaultValue() != null) {
                if (attribute.currentValueProperty.getValue() != Integer.parseInt(attribute.getDefaultValue().toString())) {
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

    public void writeModbusValue() {
        try {
            attribute.writeModbus(spinner.getValue().toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}