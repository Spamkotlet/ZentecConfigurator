package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.modbus.ModbusParameter;
import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.Objects;

public class LabeledSpinner {

    private final Attribute attribute;
    private ModbusUtilSingleton modbusUtilSingleton;
    private final ModbusParameter modbusParameter;

    public LabeledSpinner(Attribute attribute) {
        this.attribute = attribute;
        modbusParameter = attribute.getModbusParameters();
    }

    public Node getSpinner() {
        String labelText = attribute.getName();
        double minValue = attribute.getMinValue();
        double maxValue = attribute.getMaxValue();

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
                        attribute.getMinValue(), attribute.getMaxValue(), readAttributeValueFromModbus());
        spinner.setValueFactory(spinnerFactory);
        spinner.setOnMouseReleased(e -> {
            writeAttributeValueByModbus((int)((double) spinner.getValue()));
            System.out.println("Value: " + spinner.getValue());
        });

        spinner.getEditor().addEventHandler(KeyEvent.KEY_RELEASED, event -> {
            if (event.getCode() == KeyCode.ENTER) {
                writeAttributeValueByModbus((int)((double) spinner.getValue()));
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

    // Запись значения атрибута в контроллер по Modbus
    private void writeAttributeValueByModbus(int value) {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        if (modbusUtilSingleton.getMaster() != null) {
            modbusUtilSingleton.writeModbusRegister(modbusParameter.getAddress(), value);
        }
    }

    // Чтение значения атрибута из контроллера по Modbus
    private int readAttributeValueFromModbus() {
        int attributeValue = 0;
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        if (modbusUtilSingleton.getMaster() != null) {
            attributeValue = modbusUtilSingleton.readModbusRegister(modbusParameter.getAddress());
        }
        return attributeValue;
    }
}
