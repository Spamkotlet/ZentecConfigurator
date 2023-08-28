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
import java.util.Arrays;
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
    private final char[] activeAlarms0CharArray = new char[32];
    private final char[] activeAlarms1CharArray = new char[32];
    private final char[] activeWarningsCharArray = new char[32];
    private int alarmsCount;

    public AlarmTableView() {
        MainParameters mainParameters = MainController.mainParameters;
        alarmsAttribute0 = mainParameters.getAlarmsAttribute0();
        alarmsAttribute1 = mainParameters.getAlarmsAttribute1();
        warningsAttribute = mainParameters.getWarningsAttribute();
        alarmsList0 = MainController.alarms0;
        alarmsList1 = MainController.alarms1;
        warningsList = MainController.warnings;
        Arrays.fill(activeAlarms0CharArray, '0');
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
        alarmsTableView = new TableView<>();
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

    public void updateJournal() {
        char[] binaryAlarms0 = getActiveAlarmsBits0();
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss dd MMM yy");
        String dateString = formatter.format(new Date());
        for (int i = 0; i < binaryAlarms0.length; i++) {
            if (binaryAlarms0[i] == '1') {
                Alarm alarm = new Alarm(activeAlarmsList.size(), alarmsList0.get(i), dateString);
                alarmsCount++;
                activeAlarmsList.add(alarm);
                System.out.println(alarmsList0.get(i));
            }
        }

        char[] binaryAlarms1 = getActiveAlarmsBits1();
        for (int i = 0; i < binaryAlarms1.length; i++) {
            if (binaryAlarms1[i] == '1') {
                Alarm alarm = new Alarm(activeAlarmsList.size(), alarmsList1.get(i), dateString);
                alarmsCount++;
                activeAlarmsList.add(alarm);
                System.out.println(alarmsList1.get(i));
            }
        }

        char[] binaryWarnings = getActiveWarningsBits();
        for (int i = 0; i < binaryWarnings.length; i++) {
            if (binaryWarnings[i] == '1') {
                Alarm alarm = new Alarm(activeAlarmsList.size(), warningsList.get(i), dateString);
                alarmsCount++;
                activeAlarmsList.add(alarm);
                System.out.println(warningsList.get(i));
            }
        }
        System.out.println("Active alarms list size:" + activeAlarmsList.size());
        ObservableList<Alarm> alarms = FXCollections.observableArrayList(activeAlarmsList);
        alarmsTableView.setItems(alarms);
        alarmsTableView.scrollTo(alarmsTableView.getItems().size());
    }

    public void resetAlarms() {
        Arrays.fill(activeAlarms0CharArray, '0');
        Arrays.fill(activeAlarms1CharArray, '0');
        Arrays.fill(activeWarningsCharArray, '0');
    }

    public void clearJournal() {
        alarmsTableView.getItems().clear();
        activeAlarmsList.clear();
        alarmsCount = 0;
        resetAlarms();
    }

    private char[] getActiveAlarmsBits0() {
        long alarms0 = Long.parseLong(alarmsAttribute0.readModbusParameter());
        char[] binaryAlarms0 = alarmsToBinaryCharArray(alarms0);

        char[] newAlarms0CharArray = new char[32];
        Arrays.fill(newAlarms0CharArray, '0');
        for (int i = 0; i < binaryAlarms0.length; i++) {
            if (binaryAlarms0[i] == '1' && activeAlarms0CharArray[i] == '0') {
                activeAlarms0CharArray[i] = '1';
                newAlarms0CharArray[i] = '1';
            }
        }
        return newAlarms0CharArray;
    }

    private char[] getActiveAlarmsBits1() {
        long alarms1 = Long.parseLong(alarmsAttribute1.readModbusParameter());
        char[] binaryAlarms = alarmsToBinaryCharArray(alarms1);

        char[] newAlarmsCharArray = new char[32];
        Arrays.fill(newAlarmsCharArray, '0');
        for (int i = 0; i < binaryAlarms.length; i++) {
            if (binaryAlarms[i] == '1' && activeAlarms1CharArray[i] == '0') {
                activeAlarms1CharArray[i] = '1';
                newAlarmsCharArray[i] = '1';
            }
        }
        System.out.println(newAlarmsCharArray);
        return newAlarmsCharArray;
    }

    private char[] getActiveWarningsBits() {
        long warnings = Long.parseLong(warningsAttribute.readModbusParameter());
        char[] binaryWarnings = alarmsToBinaryCharArray(warnings);

        char[] newAlarmsCharArray = new char[32];
        Arrays.fill(newAlarmsCharArray, '0');
        for (int i = 0; i < binaryWarnings.length; i++) {
            if (binaryWarnings[i] == '1' && activeWarningsCharArray[i] == '0') {
                activeWarningsCharArray[i] = '1';
                newAlarmsCharArray[i] = '1';
            }
        }
        return newAlarmsCharArray;
    }

    private char[] alarmsToBinaryCharArray(long alarmsNumber) {
        return new StringBuilder(
                String.format("%32s", Long.toBinaryString(alarmsNumber))
                        .replace(' ', '0'))
                .reverse().toString().toCharArray();
    }
}
