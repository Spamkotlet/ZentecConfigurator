package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Element;
import com.zenconf.zentecconfigurator.models.Scheme;
import com.zenconf.zentecconfigurator.models.enums.Controls;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class SchemeTitledPane extends TitledPane {

    private Element element;
    private CheckBox isUsedDefaultCheckBox;
    private ChoiceBox<String> isUsedDefaultChoiceBox;
    private Attribute inWorkAttribute = null;

    public SchemeTitledPane() {
    }

    public SchemeTitledPane(Element element) {
        this.element = element;
        List<Attribute> elementAttributes = element.getAttributes();
        if (elementAttributes != null) {
            for (Attribute attribute : elementAttributes) {
                if (attribute.getName().equals("В работе")) {
                    inWorkAttribute = attribute;
                }
            }
        }

        AnchorPane anchorPane = new AnchorPane();

        if (inWorkAttribute != null) {
            if (inWorkAttribute.getControl() != null) {
                if (inWorkAttribute.getControl().equals(Controls.CHECKBOX)) {
                    isUsedDefaultCheckBox = new CheckBox();
                    isUsedDefaultCheckBox.setSelected(element.getIsUsedDefault());
                    isUsedDefaultCheckBox.setOnAction(this::onSelectedCheckBox);
                    isUsedDefaultCheckBox.setText("Используется");
                    anchorPane.getChildren().add(isUsedDefaultCheckBox);
                } else if (inWorkAttribute.getControl().equals(Controls.CHOICE_BOX)) {
                    isUsedDefaultChoiceBox = new ChoiceBox<>();
                    isUsedDefaultChoiceBox.setItems(getElementTypes());
                    isUsedDefaultChoiceBox.setValue(getElementTypes().get(Integer.parseInt(inWorkAttribute.readModbusParameter())));
                    isUsedDefaultChoiceBox.setOnAction(this::onSelectedChoiceBox);
                    anchorPane.getChildren().add(isUsedDefaultChoiceBox);
                }
            }
        }

        VBox vBox = new VBox();
        vBox.getChildren().add(anchorPane);
        vBox.setAlignment(Pos.CENTER);

        this.setText(element.getName());
        this.setContent(vBox);
    }

    private void onSelectedCheckBox(ActionEvent actionEvent) {
        if (inWorkAttribute != null) {
            inWorkAttribute.writeModbusParameter(isUsedDefaultCheckBox.isSelected());
            element.setIsUsedDefault(isUsedDefaultCheckBox.isSelected());
        }
    }

    private void onSelectedChoiceBox(ActionEvent actionEvent) {
        if (inWorkAttribute != null) {
            inWorkAttribute.writeModbusParameter(isUsedDefaultChoiceBox.getSelectionModel().getSelectedIndex());
            element.setIsUsedDefault(isUsedDefaultChoiceBox.getSelectionModel().getSelectedIndex() > 0);
            System.out.println(element.getIsUsedDefault());
        }
    }

    private ObservableList<String> getElementTypes() {
        return FXCollections.observableArrayList(inWorkAttribute.getValues());
    }
}
