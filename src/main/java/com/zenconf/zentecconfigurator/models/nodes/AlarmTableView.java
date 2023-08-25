package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.controllers.MainController;
import com.zenconf.zentecconfigurator.models.Alarm;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.MainParameters;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AlarmTableView {

    private TableView<Alarm> alarmsTableView;
    private final List<Alarm> activeAlarmsList = new ArrayList<>();
    private final Attribute alarmsAttribute0;
    private final Attribute alarmsAttribute1;
    private final Attribute warningsAttribute;
    private final List<String> alarmsList0;
    private final List<String> alarmsList1;
    private final List<String> warningsList;

    public AlarmTableView() {
        MainParameters mainParameters = MainController.mainParameters;
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

    private TableView<Alarm> createGridPane() {
        alarmsTableView = new TableView<Alarm>();
        alarmsTableView.setPrefWidth(250);
        alarmsTableView.setPrefHeight(200);
        alarmsTableView.setPlaceholder(new Label("Аварий нет"));

        // Столбец с номером аварии
        TableColumn<Alarm, Integer> numberColumn = new TableColumn<>("№");
        numberColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        numberColumn.setPrefWidth(80);
        numberColumn.setResizable(false);
        alarmsTableView.getColumns().add(numberColumn);

        // Столбец с описанием аварии
        TableColumn<Alarm, String> descriptionColumn = new TableColumn<>("Описание");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        descriptionColumn.setPrefWidth(200);
        alarmsTableView.getColumns().add(descriptionColumn);

        // Столбец с датой
        TableColumn<Alarm, String> dateColumn = new TableColumn<>("Время");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("datetime"));
        dateColumn.setPrefWidth(150);
        dateColumn.setResizable(false);
        alarmsTableView.getColumns().add(dateColumn);

        VBox.setVgrow(alarmsTableView, Priority.ALWAYS);
        return alarmsTableView;
    }

    public void update() {
        alarmsTableView.getItems().clear();
        activeAlarmsList.clear();

        long alarms0 = Long.parseLong(alarmsAttribute0.readModbusParameter());
        long alarms1 = Long.parseLong(alarmsAttribute1.readModbusParameter());
        long warnings = Long.parseLong(warningsAttribute.readModbusParameter());
        char[] binaryAlarms0 = alarmsToBinaryCharArray(alarms0);
        char[] binaryAlarms1 = alarmsToBinaryCharArray(alarms1);
        char[] binaryWarnings = alarmsToBinaryCharArray(warnings);
        System.out.println(binaryAlarms0);
        System.out.println(binaryAlarms1);
        System.out.println(binaryWarnings);
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd MMM yy");
        String dateString = formatter.format(new Date());
        for (int i = 0; i < binaryAlarms0.length; i++) {
            if (binaryAlarms0[i] == '1') {
                activeAlarmsList.add(new Alarm(activeAlarmsList.size(), alarmsList0.get(i), dateString));
                System.out.println(alarmsList0.get(i));
            }
        }
        for (int i = 0; i < binaryAlarms1.length; i++) {
            if (binaryAlarms1[i] == '1') {
                activeAlarmsList.add(new Alarm(activeAlarmsList.size(), alarmsList1.get(i), dateString));
                System.out.println(alarmsList1.get(i));
            }
        }
        for (int i = 0; i < binaryWarnings.length; i++) {
            if (binaryWarnings[i] == '1') {
                activeAlarmsList.add(new Alarm(activeAlarmsList.size(), warningsList.get(i), dateString));
                System.out.println(warningsList.get(i));
            }
        }

        ObservableList<Alarm> alarms = FXCollections.observableArrayList(activeAlarmsList);
        alarmsTableView.setItems(alarms);
    }

    public void clear() {
        alarmsTableView.getItems().clear();
        activeAlarmsList.clear();
    }

    private char[] alarmsToBinaryCharArray(long alarmsNumber) {
        return new StringBuilder(
                String.format("%32s", Long.toBinaryString(alarmsNumber))
                        .replace(' ', '0'))
                .reverse().toString().toCharArray();
    }
}
