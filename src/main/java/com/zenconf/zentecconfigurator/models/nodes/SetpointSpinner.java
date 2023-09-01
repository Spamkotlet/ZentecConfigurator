package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.StringConverter;

public class SetpointSpinner {

    private final Attribute attribute;

    public SetpointSpinner(Attribute attribute) {
        this.attribute = attribute;
    }

    public Node getSpinner() throws Exception {
        String labelText = attribute.getName();
        int minValue = attribute.getMinValue();
        int maxValue = attribute.getMaxValue();

        AnchorPane labelAnchor = createLabelAnchor(labelText);
        AnchorPane spinnerAnchor = createSpinnerAnchor(minValue, maxValue);

        return createVBoxForSetpointSpinner(labelAnchor, spinnerAnchor);
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

    private AnchorPane createLabelAnchor(String labelText) {
        Label label = new Label(labelText);
        label.setPrefWidth(200);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font("Verdana", 14));

        AnchorPane labelAnchor = new AnchorPane();

        labelAnchor.getChildren().add(label);

        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);

        AnchorPane.setLeftAnchor(labelAnchor, 0.0);
        AnchorPane.setRightAnchor(labelAnchor, 0.0);
        AnchorPane.setTopAnchor(labelAnchor, 0.0);
        AnchorPane.setBottomAnchor(labelAnchor, 0.0);

        return labelAnchor;
    }

    private AnchorPane createSpinnerAnchor(int minValue, int maxValue) throws Exception {
        int initValue = 0;
        Spinner<Integer> spinner = new Spinner<>(minValue, maxValue, initValue, 0.1);
        spinner.setEditable(true);
        spinner.setPrefWidth(200);
        spinner.setPrefHeight(55);
        SpinnerValueFactory<Integer> spinnerFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        attribute.getMinValue(), attribute.getMaxValue(), Integer.parseInt(attribute.readModbusParameter()));
        spinner.setValueFactory(spinnerFactory);

        spinner.setOnMouseReleased(e -> {
            try {
                attribute.writeModbusParameter(spinner.getValue().toString());
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });

        spinner.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    attribute.writeModbusParameter(Integer.parseInt(spinner.getValue().toString()));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        spinner.editorProperty().get().setAlignment(Pos.CENTER);
        spinner.editorProperty().get().setFont(Font.font("Verdana", 26));

        AnchorPane spinnerAnchor = new AnchorPane();

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
}