package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.controllers.configurator.ChangeSchemeController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Element;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.enums.Controls;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class SchemePane extends VBox {

    private Element element;
    private CheckBox isUsedDefaultCheckBox;
    private ChoiceBox<String> isUsedDefaultChoiceBox;
    private Attribute inWorkAttribute = null;

    public SchemePane() {
    }

    public SchemePane(Element element) throws Exception {
        this.element = element;
        List<Attribute> elementAttributes = element.getAttributes();
        if (elementAttributes != null) {
            for (Attribute attribute : elementAttributes) {
                if (attribute.getName().equals("В работе")) {
                    inWorkAttribute = attribute;
                }
            }
        }

        VBox mainVBox = createVBox();
        mainVBox.setFillWidth(true);
        mainVBox.getChildren().add(createLabelAnchorPane(element.getName()));

        AnchorPane.setLeftAnchor(mainVBox, 0.0);
        AnchorPane.setRightAnchor(mainVBox, 0.0);
        AnchorPane.setTopAnchor(mainVBox, 0.0);
        AnchorPane.setBottomAnchor(mainVBox, 0.0);

        VBox secondVBox = createVBox();
        secondVBox.setPadding(new Insets(5));
        secondVBox.setAlignment(Pos.CENTER_LEFT);
        VBox.setVgrow(secondVBox, Priority.ALWAYS);

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
                    secondVBox.getChildren().add(isUsedDefaultCheckBox);
                    inWorkAttribute.writeModbus(element.getIsUsedDefault());
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
                    secondVBox.getChildren().add(isUsedDefaultChoiceBox);
                    inWorkAttribute.writeModbus(isInWorkInteger);
                }
            }
        } else {
            isUsedDefaultCheckBox = new CheckBox();
            isUsedDefaultCheckBox.setSelected(true);
            isUsedDefaultCheckBox.setText("Используется");
            isUsedDefaultCheckBox.setDisable(true);
            secondVBox.getChildren().add(isUsedDefaultCheckBox);
        }

        mainVBox.getChildren().add(secondVBox);
        mainVBox.getStyleClass().add("element-pane-border");
        mainVBox.setAlignment(Pos.CENTER);

        this.getChildren().add(mainVBox);
    }

    private VBox createVBox() {
        VBox vBox = new VBox();
        vBox.setSpacing(10);
        return vBox;
    }

    private AnchorPane createLabelAnchorPane(String elementName) {
        AnchorPane anchorPane = new AnchorPane();
        anchorPane.setPadding(new Insets(5));

        VBox.setVgrow(anchorPane, Priority.ALWAYS);

        Label label = new Label(elementName);

        AnchorPane.setLeftAnchor(label, 0.0);
        AnchorPane.setRightAnchor(label, 0.0);
        AnchorPane.setTopAnchor(label, 0.0);
        AnchorPane.setBottomAnchor(label, 0.0);

        anchorPane.getStyleClass().add("element-pane-header");
        anchorPane.getChildren().add(label);
        return anchorPane;
    }

    public void setAttributeIsUsedOff() throws Exception {
        if (inWorkAttribute != null) {
            if (inWorkAttribute.getControl() != null) {
                if (inWorkAttribute.getControl().equals(Controls.CHECKBOX)) {
                    inWorkAttribute.writeModbus(false);
                } else if (inWorkAttribute.getControl().equals(Controls.CHOICE_BOX)) {
                    inWorkAttribute.writeModbus(0);
                }
            }
        }
    }

    private void onSelectedCheckBox() throws Exception {
        if (inWorkAttribute != null) {
            boolean isUsed = isUsedDefaultCheckBox.isSelected();
            inWorkAttribute.writeModbus(isUsed);
            element.setIsUsedDefault(isUsed);
            if (element.getClass().equals(Actuator.class)
                    && !ChangeSchemeController.actuatorsUsed.contains((Actuator) element)) {
                if (isUsed) {
                    ChangeSchemeController.actuatorsUsed.add((Actuator) element);
                } else {
                    ChangeSchemeController.actuatorsUsed.remove((Actuator) element);
                }
            } else if (element.getClass().equals(Sensor.class)
                    && !ChangeSchemeController.sensorsUsed.contains((Sensor) element)) {
                if (isUsed) {
                    ChangeSchemeController.sensorsUsed.add((Sensor) element);
                } else {
                    ChangeSchemeController.sensorsUsed.remove((Sensor) element);
                }
            }
        }
    }

    private void onSelectedChoiceBox() throws Exception {
        if (inWorkAttribute != null) {
            inWorkAttribute.writeModbus(isUsedDefaultChoiceBox.getSelectionModel().getSelectedIndex());
            element.setIsUsedDefault(isUsedDefaultChoiceBox.getSelectionModel().getSelectedIndex() > 0);
        }
    }

    private ObservableList<String> getElementTypes() {
        return FXCollections.observableArrayList(inWorkAttribute.getValues());
    }
}
