package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.MainParameters;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

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

    public VBox createAlarmGridPane() {
        VBox vBox = new VBox();
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(createLabel(), createGridPane());

        VBox.setVgrow(vBox, Priority.ALWAYS);

        return vBox;
    }

    private Label createLabel() {
        Label label = new Label();
        label.setText("Журнал событий");
        return label;
    }

    private GridPane createGridPane() {
        GridPane gridPane = new GridPane();

        ColumnConstraints column0 = new ColumnConstraints();
        column0.setHgrow(Priority.SOMETIMES);
        column0.setFillWidth(true);
        column0.setPercentWidth(-1);
        column0.setMinWidth(10);
        column0.setPrefWidth(100);
        gridPane.getColumnConstraints().add(column0);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.SOMETIMES);
        column1.setFillWidth(true);
        column1.setPercentWidth(-1);
        column1.setMinWidth(10);
        column1.setPrefWidth(100);
        gridPane.getColumnConstraints().add(column1);

        gridPane.setGridLinesVisible(true);
        gridPane.add(new Button(), 1, 0);
        gridPane.add(new Button(), 0, 1);

        VBox.setVgrow(gridPane, Priority.ALWAYS);
        return gridPane;
    }

    private void update() {

    }
}
