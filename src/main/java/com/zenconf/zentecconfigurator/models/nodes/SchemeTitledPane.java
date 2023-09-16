package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.controllers.configurator.ChangeSchemeController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Element;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.enums.Controls;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SchemeTitledPane extends TitledPane {

    private Element element;
    private CheckBox isUsedDefaultCheckBox;
    private Label errorLabel;
    private String errorText = "*";
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

        List<Node> nodes = new ArrayList<>();
        errorLabel = createErrorLabel("(" + errorText + ")");

        if (inWorkAttribute != null) {
            if (inWorkAttribute.getControl() != null) {
                if (inWorkAttribute.getControl().equals(Controls.CHECKBOX)) {
                    isUsedDefaultCheckBox = new CheckBox();
                    isUsedDefaultCheckBox.setSelected(element.getIsUsedDefault());
                    isUsedDefaultCheckBox.setOnAction(e -> {
                        try {
                            errorLabel.setVisible(false);
                            onSelectedCheckBox();
                        } catch (Exception ex) {
                            errorText = "Ошибка записи";
                            errorLabel.setText(errorText);
                            errorLabel.setVisible(true);
//                            throw new RuntimeException(ex);
                        }
                    });
                    isUsedDefaultCheckBox.setText("Используется");

                    nodes.add(isUsedDefaultCheckBox);
                    inWorkAttribute.writeModbusParameter(element.getIsUsedDefault());
                } else if (inWorkAttribute.getControl().equals(Controls.CHOICE_BOX)) {
                    int isInWorkInteger = element.getIsUsedDefault() ? 1 : 0;
                    isUsedDefaultChoiceBox = new ChoiceBox<>();
                    isUsedDefaultChoiceBox.setItems(getElementTypes());
                    isUsedDefaultChoiceBox.setValue(getElementTypes().get(isInWorkInteger));
                    isUsedDefaultChoiceBox.setOnAction(e -> {
                        try {
                            errorLabel.setVisible(false);
                            onSelectedChoiceBox();
                        } catch (Exception ex) {
                            errorText = "Ошибка записи";
                            errorLabel.setText(errorText);
                            errorLabel.setVisible(true);
//                            throw new RuntimeException(ex);
                        }
                    });
                    nodes.add(isUsedDefaultChoiceBox);
                    inWorkAttribute.writeModbusParameter(isInWorkInteger);
                }
            }
        } else {
            isUsedDefaultCheckBox = new CheckBox();
            isUsedDefaultCheckBox.setSelected(true);
            isUsedDefaultCheckBox.setText("Используется");
            isUsedDefaultCheckBox.setDisable(true);
            errorLabel.setVisible(false);
            nodes.add(isUsedDefaultCheckBox);
        }
        nodes.add(errorLabel);

        VBox vBox = new VBox();
        vBox.getChildren().add(createHBox(nodes));
        vBox.setAlignment(Pos.CENTER);

        this.setText(element.getName());
        this.setContent(vBox);
    }

    private Label createErrorLabel(String labelText) {
        Label label = new Label(labelText);
        label.setPrefWidth(100);
        label.setVisible(false);
        label.setTextFill(Color.RED);

        return label;
    }

    private HBox createHBox(List<Node> nodes) {
        HBox hBox = new HBox();
        if (!nodes.isEmpty()) {
            hBox.getChildren().addAll(nodes);
        }
        hBox.setFillHeight(true);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(hBox, Priority.ALWAYS);

        AnchorPane.setLeftAnchor(hBox, 0.0);
        AnchorPane.setRightAnchor(hBox, 0.0);
        AnchorPane.setTopAnchor(hBox, 0.0);
        AnchorPane.setBottomAnchor(hBox, 0.0);

        return hBox;
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
            if (element.getClass().equals(Actuator.class)) {
                if (!ChangeSchemeController.actuatorsUsed.contains((Actuator) element)) {
                    ChangeSchemeController.actuatorsUsed.add((Actuator) element);
                } else {
                    ChangeSchemeController.actuatorsUsed.remove((Actuator) element);
                }
            } else if (element.getClass().equals(Sensor.class)) {
                if (!ChangeSchemeController.sensorsUsed.contains((Sensor) element)) {
                    ChangeSchemeController.sensorsUsed.add((Sensor) element);
                } else {
                    ChangeSchemeController.sensorsUsed.remove((Sensor) element);
                }
            }
        }
    }

    private void onSelectedChoiceBox() throws Exception {
        errorLabel.setVisible(false);
        if (inWorkAttribute != null) {
            boolean isUsed = isUsedDefaultChoiceBox.getSelectionModel().getSelectedIndex() > 0;
            inWorkAttribute.writeModbusParameter(isUsedDefaultChoiceBox.getSelectionModel().getSelectedIndex());
            element.setIsUsedDefault(isUsed);
            if (element.getClass().equals(Actuator.class)) {
                if (!ChangeSchemeController.actuatorsUsed.contains((Actuator) element)) {
                    ChangeSchemeController.actuatorsUsed.add((Actuator) element);
                } else {
                    ChangeSchemeController.actuatorsUsed.remove((Actuator) element);
                }
            } else if (element.getClass().equals(Sensor.class)) {
                if (!ChangeSchemeController.sensorsUsed.contains((Sensor) element)) {
                    ChangeSchemeController.sensorsUsed.add((Sensor) element);
                } else {
                    ChangeSchemeController.sensorsUsed.remove((Sensor) element);
                }
            }
        }
    }

    private ObservableList<String> getElementTypes() {
        return FXCollections.observableArrayList(inWorkAttribute.getValues());
    }
}