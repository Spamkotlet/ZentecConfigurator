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

    public ElementTitledPane() {

    }

    public ElementTitledPane(Element element) throws Exception {
        VBox vBox = createMainVBox();
        vBox.setFillWidth(true);

        AnchorPane.setLeftAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);

        labeledSpinnerList = new ArrayList<>();
        labeledChoiceBoxList = new ArrayList<>();
        if (element.getAttributes() != null) {
            for (Attribute attribute : element.getAttributes()) {
                if (attribute.getControl() == Controls.SPINNER) {
                    LabeledSpinner labeledSpinner = new LabeledSpinner(attribute, true, true);
                    labeledSpinnerList.add(labeledSpinner);
                    vBox.getChildren().add(labeledSpinner.getSpinner());
                } else if (attribute.getControl() == Controls.CHOICE_BOX) {
                    LabeledChoiceBox labeledChoiceBox = new LabeledChoiceBox(attribute);
                    labeledChoiceBoxList.add(labeledChoiceBox);
                    vBox.getChildren().add(labeledChoiceBox.getChoiceBox());
                }
            }
        }

        this.setAnimated(false);
        this.setText(element.getName());
        this.setContent(vBox);
    }

    private VBox createMainVBox() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        return vBox;
    }
}