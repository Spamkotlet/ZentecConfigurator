package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.MainParameters;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

import java.util.Collections;
import java.util.List;

public class AlarmGridPane {

    private GridPane alarmsGridPane;
    private List<String> alarmList;
    private Attribute alarmsAttribute0;
    private Attribute alarmsAttribute1;
    private Attribute warningsAttribute;
    private MainParameters mainParameters;
    private List<String> alarmsList0;
    private List<String> alarmsList1;
    private List<String> warningsList;

    public AlarmGridPane() {
        mainParameters = MainController.mainParameters;
        alarmsAttribute0 = mainParameters.getAlarmsAttribute0();
        alarmsAttribute1 = mainParameters.getAlarmsAttribute1();
        warningsAttribute = mainParameters.getWarningsAttribute();
        alarmsList0 = MainController.alarms0;
        alarmsList1 = MainController.alarms1;
        warningsList = MainController.warnings;
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
        alarmsGridPane = new GridPane();

        ColumnConstraints column0 = new ColumnConstraints();
        column0.setHgrow(Priority.SOMETIMES);
        column0.setFillWidth(true);
        column0.setPercentWidth(-1);
        column0.setMinWidth(10);
        column0.setPrefWidth(100);
        alarmsGridPane.getColumnConstraints().add(column0);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.SOMETIMES);
        column1.setFillWidth(true);
        column1.setPercentWidth(-1);
        column1.setMinWidth(10);
        column1.setPrefWidth(100);
        alarmsGridPane.getColumnConstraints().add(column1);

        alarmsGridPane.setGridLinesVisible(true);
//        alarmsGridPane.add(new Button(), 1, 0);
//        alarmsGridPane.add(new Button(), 0, 1);

        VBox.setVgrow(alarmsGridPane, Priority.ALWAYS);
        return alarmsGridPane;
    }

    public void update() {
        long alarms0 = Long.parseLong(alarmsAttribute0.readModbusParameter());
        long alarms1 = Long.parseLong(alarmsAttribute1.readModbusParameter());
        long warnings = Long.parseLong(warningsAttribute.readModbusParameter());
        char[] binaryAlarms0 = alarmsToBinaryCharArray(alarms0);
        char[] binaryAlarms1 = alarmsToBinaryCharArray(alarms1);
        char[] binaryWarnings = alarmsToBinaryCharArray(warnings);
        System.out.println(binaryAlarms0);
        System.out.println(binaryAlarms1);
        System.out.println(binaryWarnings);
        Label alarmDescriptionLabel = new Label();
        for (int i = 0; i < binaryAlarms0.length; i++) {
            if (binaryAlarms0[i] == '1') {
                alarmDescriptionLabel.setText(alarmsList0.get(i));
//                alarmsGridPane.getRowConstraints().add(alarmDescriptionLabel, 1, alarmsGridPane.getRowCount());
                System.out.println(alarmsList0.get(i));
            }
        }
        for (int i = 0; i < binaryAlarms1.length; i++) {
            if (binaryAlarms1[i] == '1') {
                System.out.println(alarmsList1.get(i));
            }
        }
        for (int i = 0; i < binaryWarnings.length; i++) {
            if (binaryWarnings[i] == '1') {
                System.out.println(warningsList.get(i));
            }
        }
    }

    private char[] alarmsToBinaryCharArray(long alarmsNumber) {
        return new StringBuilder(
                String.format("%32s", Long.toBinaryString(alarmsNumber))
                        .replace(' ', '0'))
                .reverse().toString().toCharArray();
    }
}
