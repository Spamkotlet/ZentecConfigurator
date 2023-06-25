package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Element;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class SchemeTitledPane extends TitledPane {

    private Element element;
    private CheckBox isUsedDefaultCheckBox;
    public SchemeTitledPane() {
    }

    public SchemeTitledPane(Element element) {
        this.element = element;

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
        element.setIsUsedDefault(isUsedDefaultCheckBox.isSelected());
    }
}
