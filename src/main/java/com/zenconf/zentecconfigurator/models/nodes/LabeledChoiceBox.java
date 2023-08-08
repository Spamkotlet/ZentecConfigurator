package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.enums.VarTypes;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.util.List;

public class LabeledChoiceBox {

    private final Attribute attribute;

    public LabeledChoiceBox(Attribute attribute) {
        this.attribute = attribute;
    }

    public Node getChoiceBox() {
        String labelText = attribute.getName();
        List<String> attributeValues = attribute.getValues();

        AnchorPane labelAnchor = createLabelAnchor(labelText);
        AnchorPane choiceBoxAnchor = createChoiceBoxAnchor(attributeValues);

        return createHBoxForLabeledChoiceBox(labelAnchor, choiceBoxAnchor);
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

    private AnchorPane createChoiceBoxAnchor(List<String> attributeValues) {
        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        choiceBox.setPrefWidth(200);
        choiceBox.setItems(getChoiceBoxItems(attributeValues));

        if (attribute.getModbusParameters().getVarType().equals(VarTypes.BOOL)) {
            choiceBox.setValue(
                    getChoiceBoxItems(attributeValues)
                            .get(attribute.readModbusParameter().equals("true") ? 1 : 0)
            );
            choiceBox.setOnAction(e -> {
                Boolean value = attributeValues.indexOf(choiceBox.getValue()) > 0;
                attribute.writeModbusParameter(value);
                System.out.println("Index: " + attributeValues.indexOf(choiceBox.getValue()) + " Value: " + value);
            });
        } else {
            choiceBox.setValue(
                    getChoiceBoxItems(attributeValues)
                            .get(Integer.parseInt(attribute.readModbusParameter()))
            );
            choiceBox.setOnAction(e -> {
                attribute.writeModbusParameter(attributeValues.indexOf(choiceBox.getValue()));
                System.out.println("Index: " + attributeValues.indexOf(choiceBox.getValue()) + " Value: " + choiceBox.getValue());
            });
        }

        AnchorPane choiceBoxAnchor = new AnchorPane();
        choiceBoxAnchor.getChildren().add(choiceBox);
        AnchorPane.setLeftAnchor(choiceBoxAnchor, 0.0);
        AnchorPane.setRightAnchor(choiceBoxAnchor, 0.0);
        AnchorPane.setTopAnchor(choiceBoxAnchor, 0.0);
        AnchorPane.setBottomAnchor(choiceBoxAnchor, 0.0);

        return choiceBoxAnchor;
    }

    private ObservableList<String> getChoiceBoxItems(List<String> attributeValues) {
        return FXCollections.observableArrayList(attributeValues);
    }

    private HBox createHBoxForLabeledChoiceBox(Node... nodes) {
        HBox hBox = new HBox();
        hBox.getChildren().addAll(nodes);
        hBox.setSpacing(10);

        return hBox;
    }
}
