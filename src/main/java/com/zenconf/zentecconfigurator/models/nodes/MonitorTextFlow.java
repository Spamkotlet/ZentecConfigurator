package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Element;
import com.zenconf.zentecconfigurator.models.Sensor;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Spinner;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.StringConverter;

public class MonitorTextFlow {
    private final Element element;
    private Attribute correctionAttribute = null;
    private final MonitorValueText valueText = new MonitorValueText("***");

    public MonitorTextFlow(Element element) {
        this.element = element;
        valueText.setElement(element);
    }

    private Text createNameText() {
        Text nameText = new Text(element.getAttributeForMonitoring().getName());
        nameText.setFont(Font.font("Verdana", 14));
        nameText.setFill(Color.BLACK);
        nameText.setTextAlignment(TextAlignment.CENTER);

        return nameText;
    }

    private Text createValueText() {
        valueText.setFont(Font.font("Arial", 32));
        valueText.setFill(new Color(0.996, 0.4, 0.247, 1));
        valueText.setTextAlignment(TextAlignment.CENTER);
        return valueText;
    }

    private Node createVBoxForTextFlow() {
        VBox vBox = new VBox();
        vBox.getChildren().add(createValueText());
        vBox.getChildren().add(createNameText());
        vBox.setSpacing(10);
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox, Priority.ALWAYS);
        HBox.setHgrow(vBox, Priority.ALWAYS);
        return vBox;
    }

    private Spinner<Integer> createSpinner(Attribute attribute) {
        int initValue;

        try {
            initValue = (int) Double.parseDouble(attribute.readModbus());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Spinner<Integer> spinner = new Spinner<>(attribute.getMinValue(), attribute.getMaxValue(), initValue);

        spinner.setEditable(true);
        spinner.setPrefWidth(0);
        spinner.setPadding(new Insets(-10, -10, -10, 0));
        spinner.setPrefHeight(70);
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

        spinner.setOnMouseReleased(e -> writeModbusValue(spinner.getValue()));

        spinner.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (oldVal && !newVal) {
                writeModbusValue(spinner.getValue());
            }
        });

        spinner.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                writeModbusValue(spinner.getValue());
            }
        });

        return spinner;
    }

    public void writeModbusValue(Object value) {
        try {
            correctionAttribute.writeModbus(value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Node getTextFlow() {
        VBox monitorVBox = new VBox();
        monitorVBox.setBackground(new Background(new BackgroundFill(new Color(0.831, 0.831, 0.831, 1), CornerRadii.EMPTY, Insets.EMPTY)));
        monitorVBox.setAlignment(Pos.CENTER);
        monitorVBox.setPadding(new Insets(10));

        VBox vBox = (VBox) createVBoxForTextFlow();

        HBox hBox = null;
        if (element.getClass().equals(Sensor.class)) {
            for (Attribute attribute : element.getAttributes()) {
                if (attribute.getName().equals("Коррекция")) {
                    correctionAttribute = attribute;
                    break;
                }
            }
            if (correctionAttribute != null) {
                hBox = new HBox();
                hBox.setAlignment(Pos.CENTER);
                hBox.getChildren().add(vBox);
                hBox.getChildren().add(createSpinner(correctionAttribute));
            }
        }

        monitorVBox.getChildren().add(correctionAttribute == null ? vBox : hBox);

        return monitorVBox;
    }

    public MonitorValueText getValueText() {
        return valueText;
    }
}
