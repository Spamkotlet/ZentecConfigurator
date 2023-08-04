package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Element;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class SchemeTitledPane extends TitledPane {

    private Element element;
    private CheckBox isUsedDefaultCheckBox;
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

        isUsedDefaultCheckBox = new CheckBox();
        isUsedDefaultCheckBox.setSelected(element.getIsUsedDefault());
        isUsedDefaultCheckBox.setOnAction(this::onSelectedCheckBox);
        isUsedDefaultCheckBox.setText("Используется");

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(isUsedDefaultCheckBox);

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
}
