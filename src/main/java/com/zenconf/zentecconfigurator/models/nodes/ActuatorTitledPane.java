package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Controls;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ActuatorTitledPane extends TitledPane {

    public ActuatorTitledPane() {

    }

    public ActuatorTitledPane(Actuator actuator) {

        VBox vBox = createMainVBox();
        vBox.setFillWidth(true);

        AnchorPane.setLeftAnchor(vBox, 0.0);
        AnchorPane.setRightAnchor(vBox, 0.0);
        AnchorPane.setTopAnchor(vBox, 0.0);
        AnchorPane.setBottomAnchor(vBox, 0.0);

        for (Attribute attribute : actuator.getAttributes()) {
            if (attribute.getControl() == Controls.SPINNER) {
                LabeledSpinner labeledSpinner = new LabeledSpinner(attribute);
                vBox.getChildren().add(labeledSpinner.getSpinner());
            } else if (attribute.getControl() == Controls.CHOICE_BOX) {
                LabeledChoiceBox labeledChoiceBox = new LabeledChoiceBox(attribute);
                vBox.getChildren().add(labeledChoiceBox.getChoiceBox());
            }
        }

        AnchorPane mainAnchor = new AnchorPane();
        mainAnchor.getChildren().add(vBox);

        this.setText(actuator.getName());
        this.setContent(mainAnchor);
    }

    private VBox createMainVBox() {
        VBox vBox = new VBox();
//        vBox.setAlignment(Pos.CENTER);
        vBox.setSpacing(10);
        vBox.setPadding(new Insets(10));
        return vBox;
    }
}
