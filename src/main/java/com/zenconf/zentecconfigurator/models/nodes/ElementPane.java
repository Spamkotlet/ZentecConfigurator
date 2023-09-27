package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Element;
import com.zenconf.zentecconfigurator.models.enums.Controls;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class ElementPane extends VBox {

    public ElementPane() {

    }

    public ElementPane(Element element) throws Exception {

        VBox mainVBox = createVBox();
        mainVBox.setFillWidth(true);
        mainVBox.getChildren().add(createLabelAnchorPane(element.getName()));

        AnchorPane.setLeftAnchor(mainVBox, 0.0);
        AnchorPane.setRightAnchor(mainVBox, 0.0);
        AnchorPane.setTopAnchor(mainVBox, 0.0);
        AnchorPane.setBottomAnchor(mainVBox, 0.0);

        VBox secondVBox = createVBox();
        secondVBox.setPadding(new Insets(5));

        for (Attribute attribute : element.getAttributes()) {
            if (attribute.getControl() == Controls.SPINNER) {
                LabeledSpinner labeledSpinner = new LabeledSpinner(attribute, true, true);
                secondVBox.getChildren().add(labeledSpinner.getSpinner());
            } else if (attribute.getControl() == Controls.CHOICE_BOX) {
                LabeledChoiceBox labeledChoiceBox = new LabeledChoiceBox(attribute);
                secondVBox.getChildren().add(labeledChoiceBox.getChoiceBox());
            }
        }

        mainVBox.getChildren().add(secondVBox);
        mainVBox.getStyleClass().add("element-pane-border");

        this.getChildren().add(mainVBox);
    }

    private VBox createVBox() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        return vBox;
    }

    private AnchorPane createLabelAnchorPane(String elementName) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPadding(new Insets(5));

        VBox.setVgrow(anchorPane, Priority.ALWAYS);

        Label label = new Label(elementName);

        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);

        anchorPane.getStyleClass().add("element-pane-header");
        anchorPane.getChildren().add(label);
        return anchorPane;
    }
}
