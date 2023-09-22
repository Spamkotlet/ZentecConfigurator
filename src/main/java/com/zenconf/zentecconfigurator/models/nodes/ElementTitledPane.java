package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.enums.Controls;
import com.zenconf.zentecconfigurator.models.Element;
import javafx.geometry.Insets;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class ElementTitledPane extends TitledPane {

    List<LabeledSpinner> labeledSpinnerList;
    List<LabeledChoiceBox> labeledChoiceBoxList;
    private Element element;

    public ElementTitledPane() {

    }

    public ElementTitledPane(Element element) throws Exception {
        this.element = element;
        VBox vBox = createMainVBox();
        vBox.setFillWidth(true);

        AnchorPane.setLeftAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);

        labeledSpinnerList = new ArrayList<>();
        labeledChoiceBoxList = new ArrayList<>();
        for (Attribute attribute : element.getAttributes()) {
            if (attribute.getControl() == Controls.SPINNER) {
                LabeledSpinner labeledSpinner = new LabeledSpinner(attribute, true);
                labeledSpinnerList.add(labeledSpinner);
                vBox.getChildren().add(labeledSpinner.getSpinner());
            } else if (attribute.getControl() == Controls.CHOICE_BOX) {
                LabeledChoiceBox labeledChoiceBox = new LabeledChoiceBox(attribute);
                labeledChoiceBoxList.add(labeledChoiceBox);
                vBox.getChildren().add(labeledChoiceBox.getChoiceBox());
            }
        }

        AnchorPane mainAnchor = new AnchorPane();
        mainAnchor.getChildren().add(vBox);

        this.setText(element.getName());
        this.setContent(mainAnchor);
    }

    private VBox createMainVBox() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        return vBox;
    }

    public void setElement(Element element) throws Exception {
        this.element = element;
        for (LabeledSpinner labeledSpinner : labeledSpinnerList) {
            labeledSpinner.readModbusValue();
        }
        for (LabeledChoiceBox labeledChoiceBox : labeledChoiceBoxList) {
            labeledChoiceBox.readModbusValue();
        }
    }
}