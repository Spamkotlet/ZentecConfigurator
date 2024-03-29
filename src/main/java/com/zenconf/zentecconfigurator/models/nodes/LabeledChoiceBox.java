package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.enums.VarTypes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class LabeledChoiceBox {

    private final ChoiceBox<String> choiceBox = new ChoiceBox<>();
    private final Attribute attribute;
    private String errorText = "*";
    private Label errorLabel;
    List<String> attributeValues;

    public LabeledChoiceBox(Attribute attribute) {
        this.attribute = attribute;
    }

    public Node getChoiceBox() throws Exception {
        String labelText = attribute.getName();
        attributeValues = attribute.getValues();
        List<Node> nodes = new ArrayList<>();

        nodes.add(createLabel(labelText));
        errorLabel = createErrorLabel("(" + errorText + ")", 100, false);
        nodes.add(createChoiceBox());
        nodes.add(errorLabel);

        return createHBoxForLabeledChoiceBox(nodes);
    }

    private Label createLabel(String labelText) {
        Label label = new Label(labelText);
        label.setPrefWidth(200);

        return label;
    }

    private Label createErrorLabel(String labelText, int labelWidth, boolean isVisible) {
        Label label = createLabel(labelText);
        label.setPrefWidth(labelWidth);
        label.setVisible(isVisible);
        label.setTextFill(Color.RED);

        return label;
    }

    private ChoiceBox<String> createChoiceBox() throws Exception {
        choiceBox.setPrefWidth(200);
        choiceBox.setItems(getChoiceBoxItems(attributeValues));
        readModbusValue();
            if (attribute.getModbusParameters().getVarType().equals(VarTypes.BOOL)) {
                choiceBox.setOnAction(e -> {
                    Boolean value = attributeValues.indexOf(choiceBox.getValue()) > 0;
                    try {
                        errorLabel.setVisible(false);
                        attribute.writeModbus(value);
                    } catch (Exception ex) {
                        errorText = "Ошибка записи";
                        errorLabel.setText(errorText);
                        errorLabel.setVisible(true);
                        try {
                            throw ex;
                        } catch (Exception exc) {
                            throw new RuntimeException(exc);
                        }
                    }
                });
            } else {
                choiceBox.setOnAction(e -> {
                    try {
                        errorLabel.setVisible(false);
                        attribute.writeModbus(attributeValues.indexOf(choiceBox.getValue()));
                    } catch (Exception ex) {
                        errorText = "Ошибка записи";
                        errorLabel.setText(errorText);
                        errorLabel.setVisible(true);
                        try {
                            throw ex;
                        } catch (Exception exc) {
                            throw new RuntimeException(exc);
                        }
                    }
                });
            }
        return choiceBox;
    }

    private ObservableList<String> getChoiceBoxItems(List<String> attributeValues) {
        return FXCollections.observableArrayList(attributeValues);
    }

    private HBox createHBoxForLabeledChoiceBox(List<Node> nodes) {
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

    public void readModbusValue() throws Exception {
        if (attribute.getModbusParameters().getVarType().equals(VarTypes.BOOL)) {

            try {
                errorLabel.setVisible(false);
                choiceBox.setValue(
                        getChoiceBoxItems(attributeValues)
                                .get(attribute.readModbus().equals("true") ? 1 : 0)
                );
            } catch (Exception ex) {
                errorText = "Ошибка чтения";
                errorLabel.setText(errorText);
                errorLabel.setVisible(true);
                ex.printStackTrace();
                throw ex;
            }
        } else {
            try {
                errorLabel.setVisible(false);
                choiceBox.setValue(
                        getChoiceBoxItems(attributeValues)
                                .get(Integer.parseInt(attribute.readModbus()))
                );
            } catch (Exception ex) {
                errorText = "Ошибка чтения";
                errorLabel.setText(errorText);
                errorLabel.setVisible(true);
                ex.printStackTrace();
                throw ex;
            }
        }
    }
}
