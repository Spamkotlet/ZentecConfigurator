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
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class SetpointChoiceBox extends SetpointControl {
    private final Attribute attribute;
    private List<String> attributeValues = new ArrayList<>();
    private ChoiceBox<String> choiceBox;
    public SetpointChoiceBox(Attribute attribute) {
        this.attribute = attribute;
    }

    public Node getChoiceBox() throws Exception {
        String labelText = attribute.getName();

        AnchorPane choiceBoxAnchor = createChoiceBoxAnchor();

        return createVBoxForSetpointSpinner(createLabelAnchor(labelText), choiceBoxAnchor);
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
        label.setPrefWidth(200);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font("Verdana", 14));

        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);

        return label;
    }

    private AnchorPane createChoiceBoxAnchor() throws Exception {
        choiceBox = new ChoiceBox<>();
        choiceBox.setPrefWidth(200);
        choiceBox.setPrefHeight(55);
        attributeValues = attribute.getValues();
        choiceBox.setItems(getChoiceBoxItems(attributeValues));
        readModbusValue();
        if (attribute.getModbusParameters().getVarType().equals(VarTypes.BOOL)) {
            choiceBox.setOnAction(e -> {
                Boolean value = attributeValues.indexOf(choiceBox.getValue()) > 0;
                try {
                    attribute.writeModbus(value);
                } catch (Exception ex) {
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
                    attribute.writeModbus(attributeValues.indexOf(choiceBox.getValue()));
                } catch (Exception ex) {
                    try {
                        throw ex;
                    } catch (Exception exc) {
                        throw new RuntimeException(exc);
                    }
                }
            });
        }

        AnchorPane choiceBoxAnchor = new AnchorPane();

        choiceBoxAnchor.getChildren().add(choiceBox);

        AnchorPane.setLeftAnchor(choiceBox, 0.0);
        AnchorPane.setRightAnchor(choiceBox, 0.0);
        AnchorPane.setTopAnchor(choiceBox, 0.0);
        AnchorPane.setBottomAnchor(choiceBox, 0.0);

        AnchorPane.setLeftAnchor(choiceBoxAnchor, 0.0);
        AnchorPane.setRightAnchor(choiceBoxAnchor, 0.0);
        AnchorPane.setTopAnchor(choiceBoxAnchor, 0.0);
        AnchorPane.setBottomAnchor(choiceBoxAnchor, 0.0);

        return choiceBoxAnchor;
    }

    public void readModbusValue() throws Exception {
        if (attribute.getModbusParameters().getVarType().equals(VarTypes.BOOL)) {

            try {
                choiceBox.setValue(
                        getChoiceBoxItems(attributeValues)
                                .get(attribute.readModbus().equals("true") ? 1 : 0)
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        } else {
            try {
                choiceBox.setValue(
                        getChoiceBoxItems(attributeValues)
                                .get(Integer.parseInt(attribute.readModbus()))
                );
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    private ObservableList<String> getChoiceBoxItems(List<String> attributeValues) {
        return FXCollections.observableArrayList(attributeValues);
    }
}
