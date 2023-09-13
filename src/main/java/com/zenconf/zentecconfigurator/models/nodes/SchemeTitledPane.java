package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.controllers.configurator.ChangeSchemeController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Element;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.enums.Controls;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class SchemeTitledPane extends TitledPane {

    private Element element;
    private CheckBox isUsedDefaultCheckBox;
    private ChoiceBox<String> isUsedDefaultChoiceBox;
    private Attribute inWorkAttribute = null;

    public SchemeTitledPane() {
    }

    public SchemeTitledPane(Element element) throws Exception {
        this.element = element;
        List<Attribute> elementAttributes = element.getAttributes();
        if (elementAttributes != null) {
            for (Attribute attribute : elementAttributes) {
                if (attribute.getName().equals("В работе")) {
                    inWorkAttribute = attribute;
                }
            }
        }

        AnchorPane anchorPane = new AnchorPane();

        if (inWorkAttribute != null) {
            if (inWorkAttribute.getControl() != null) {
                if (inWorkAttribute.getControl().equals(Controls.CHECKBOX)) {
                    isUsedDefaultCheckBox = new CheckBox();
                    isUsedDefaultCheckBox.setSelected(element.getIsUsedDefault());
                    isUsedDefaultCheckBox.setOnAction(e -> {
                        try {
                            onSelectedCheckBox();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    isUsedDefaultCheckBox.setText("Используется");
                    anchorPane.getChildren().add(isUsedDefaultCheckBox);
                    inWorkAttribute.writeModbusParameter(element.getIsUsedDefault());
                } else if (inWorkAttribute.getControl().equals(Controls.CHOICE_BOX)) {
                    int isInWorkInteger = element.getIsUsedDefault() ? 1 : 0;
                    isUsedDefaultChoiceBox = new ChoiceBox<>();
                    isUsedDefaultChoiceBox.setItems(getElementTypes());
                    isUsedDefaultChoiceBox.setValue(getElementTypes().get(isInWorkInteger));
                    isUsedDefaultChoiceBox.setOnAction(e -> {
                        try {
                            onSelectedChoiceBox();
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    });
                    anchorPane.getChildren().add(isUsedDefaultChoiceBox);
                    inWorkAttribute.writeModbusParameter(isInWorkInteger);
                }
            }
        } else {
            isUsedDefaultCheckBox = new CheckBox();
            isUsedDefaultCheckBox.setSelected(true);
            isUsedDefaultCheckBox.setText("Используется");
            isUsedDefaultCheckBox.setDisable(true);
            anchorPane.getChildren().add(isUsedDefaultCheckBox);
        }

        VBox vBox = new VBox();
        vBox.getChildren().add(anchorPane);
        vBox.setAlignment(Pos.CENTER);

        this.setText(element.getName());
        this.setContent(vBox);
    }

    public void setAttributeIsUsedOff() throws Exception {
        if (inWorkAttribute != null) {
            if (inWorkAttribute.getControl() != null) {
                if (inWorkAttribute.getControl().equals(Controls.CHECKBOX)) {
                    inWorkAttribute.writeModbusParameter(false);
                } else if (inWorkAttribute.getControl().equals(Controls.CHOICE_BOX)) {
                    inWorkAttribute.writeModbusParameter(0);
                }
            }
        }
    }

    private void onSelectedCheckBox() throws Exception {
        if (inWorkAttribute != null) {
            boolean isUsed = isUsedDefaultCheckBox.isSelected();
            inWorkAttribute.writeModbusParameter(isUsed);
            element.setIsUsedDefault(isUsed);
            if (element.getClass().equals(Actuator.class)
                    && !ChangeSchemeController.actuatorsInScheme.contains((Actuator) element)) {
                if (isUsed) {
                    ChangeSchemeController.actuatorsInScheme.add((Actuator) element);
                } else {
                    ChangeSchemeController.actuatorsInScheme.remove((Actuator) element);
                }
            } else if (element.getClass().equals(Sensor.class)
                    && !ChangeSchemeController.sensorsInScheme.contains((Sensor) element)) {
                if (isUsed) {
                    ChangeSchemeController.sensorsInScheme.add((Sensor) element);
                } else {
                    ChangeSchemeController.sensorsInScheme.remove((Sensor) element);
                }
            }
            System.out.println(ChangeSchemeController.actuatorsInScheme.toString());
        }
    }

    private void onSelectedChoiceBox() throws Exception {
        if (inWorkAttribute != null) {
            inWorkAttribute.writeModbusParameter(isUsedDefaultChoiceBox.getSelectionModel().getSelectedIndex());
            element.setIsUsedDefault(isUsedDefaultChoiceBox.getSelectionModel().getSelectedIndex() > 0);
        }
    }

    private ObservableList<String> getElementTypes() {
        return FXCollections.observableArrayList(inWorkAttribute.getValues());
    }
}