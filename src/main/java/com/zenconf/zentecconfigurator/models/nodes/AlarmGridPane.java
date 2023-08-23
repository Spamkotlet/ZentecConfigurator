package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.MainParameters;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class AlarmGridPane {

    private GridPane alarmsGridPane;
    private List<String> alarmList;
    private Attribute alarmsAttribute0;
    private Attribute alarmsAttribute1;
    private Attribute warningsAttribute;
    private MainParameters mainParameters;

    public AlarmGridPane() {
        mainParameters = MainController.mainParameters;
    }

    private VBox createAlarmGridPane() {
        VBox vBox = new VBox();
        vBox.getChildren().add(createLabel());
        return vBox;
    }

    private Label createLabel() {
        Label label = new Label();
        label.setText("Журнал событий");
        return label;
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();
        return gridPane;
    }
}
