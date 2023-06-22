package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Element;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class SchemeTitledPane extends TitledPane {

    public SchemeTitledPane() {

    }

    public SchemeTitledPane(Element element) {
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(element.getIsUsedDefault());
        checkBox.setText("Используется");

        AnchorPane anchorPane = new AnchorPane();
        anchorPane.getChildren().add(checkBox);

        VBox vBox = new VBox();
        vBox.getChildren().add(anchorPane);
        vBox.setAlignment(Pos.CENTER);

        this.setText(element.getName());
        this.setContent(vBox);
    }
}
