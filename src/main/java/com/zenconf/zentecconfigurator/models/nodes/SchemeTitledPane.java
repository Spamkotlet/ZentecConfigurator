package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.controllers.ConfiguratorController;
import com.zenconf.zentecconfigurator.models.Actuator;
import com.zenconf.zentecconfigurator.models.Attribute;
import com.zenconf.zentecconfigurator.models.Element;
import com.zenconf.zentecconfigurator.models.Sensor;
import com.zenconf.zentecconfigurator.models.enums.Controls;
import javafx.application.Platform;
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

    public SchemeTitledPane(Element element) {
        this.element = element;
        inWorkAttribute = element.getIsInWorkAttribute();

        List<Node> nodes = new ArrayList<>();
        errorLabel = createErrorLabel("(" + errorText + ")");

        if (inWorkAttribute != null) {
            if (inWorkAttribute.getControl() != null) {
                if (inWorkAttribute.getControl().equals(Controls.CHECKBOX)) {
                    isUsedDefaultCheckBox = new CheckBox();
                    isUsedDefaultCheckBox.setOnAction(e -> {
                        try {
                            errorLabel.setVisible(false);
                            onSelectedCheckBox();
                        } catch (Exception ex) {
                            errorText = "Ошибка записи";
                            errorLabel.setText(errorText);
                            errorLabel.setVisible(true);
                        }
                    });
                    isUsedDefaultCheckBox.setText("Используется");
                    nodes.add(isUsedDefaultCheckBox);
                } else if (inWorkAttribute.getControl().equals(Controls.CHOICE_BOX)) {
                    isUsedDefaultChoiceBox = new ChoiceBox<>();
                    isUsedDefaultChoiceBox.setItems(getElementTypes());
                    isUsedDefaultChoiceBox.setOnAction(e -> {
                        try {
                            errorLabel.setVisible(false);
                            onSelectedChoiceBox();
                        } catch (Exception ex) {
                            errorText = "Ошибка записи";
                            errorLabel.setText(errorText);
                            errorLabel.setVisible(true);
                        }
                    });
                    nodes.add(isUsedDefaultChoiceBox);
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

        this.setAnimated(false);
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

    public void setAttributeIsUsedDefault() throws Exception {
        if (inWorkAttribute != null) {
            if (inWorkAttribute.getControl() != null) {
                if (inWorkAttribute.getControl().equals(Controls.CHECKBOX)) {
                    isUsedDefaultCheckBox.setSelected(element.getIsUsedDefault());
                    try {
                        errorLabel.setVisible(false);
                        inWorkAttribute.writeModbus(element.getIsUsedDefault());
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            errorText = "Ошибка записи";
                            errorLabel.setText(errorText);
                            errorLabel.setVisible(true);
                        });
                        throw e;
                    }
                } else if (inWorkAttribute.getControl().equals(Controls.CHOICE_BOX)) {
                    Platform.runLater(() -> isUsedDefaultChoiceBox.getSelectionModel().select(!element.getIsUsedDefault() ? 0 : 1));
                    try {
                        errorLabel.setVisible(false);
                        inWorkAttribute.writeModbus(!element.getIsUsedDefault() ? 0 : 1);
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            errorText = "Ошибка записи";
                            errorLabel.setText(errorText);
                            errorLabel.setVisible(true);
                        });
                        throw e;
                    }
                }
            }
        }
    }

    private void onSelectedCheckBox() throws Exception {
        if (inWorkAttribute != null) {
            boolean isUsed = isUsedDefaultCheckBox.isSelected();
            inWorkAttribute.writeModbus(isUsed);
            element.setUsed(isUsed);
            if (element.getClass().equals(Actuator.class)) {
                if (!ConfiguratorController.actuatorsUsed.contains((Actuator) element)) {
                    if (element.getAttributes() != null) {
                        ConfiguratorController.actuatorsUsed.add((Actuator) element);
                    }
                } else {
                    ConfiguratorController.actuatorsUsed.remove((Actuator) element);
                }
            } else if (element.getClass().equals(Sensor.class)) {
                if (!ConfiguratorController.sensorsUsed.contains((Sensor) element)) {
                    if (element.getAttributes() != null) {
                        ConfiguratorController.sensorsUsed.add((Sensor) element);
                    }
                } else {
                    ConfiguratorController.sensorsUsed.remove((Sensor) element);
                }
            }
        }
    }

    private void onSelectedChoiceBox() throws Exception {
        errorLabel.setVisible(false);
        if (inWorkAttribute != null) {
            boolean isUsed = isUsedDefaultChoiceBox.getSelectionModel().getSelectedIndex() > 0;
            inWorkAttribute.writeModbus(isUsedDefaultChoiceBox.getSelectionModel().getSelectedIndex());
            element.setUsed(isUsed);
            if (element.getClass().equals(Actuator.class) && isUsed) {
                if (!ConfiguratorController.actuatorsUsed.contains((Actuator) element)) {
                    if (element.getAttributes() != null) {
                        ConfiguratorController.actuatorsUsed.add((Actuator) element);
                    }
                } else {
                    ConfiguratorController.actuatorsUsed.remove((Actuator) element);
                }
            } else if (element.getClass().equals(Sensor.class)) {
                if (!ConfiguratorController.sensorsUsed.contains((Sensor) element) && isUsed) {
                    if (element.getAttributes() != null) {
                        ConfiguratorController.sensorsUsed.add((Sensor) element);
                    }
                } else {
                    ConfiguratorController.sensorsUsed.remove((Sensor) element);
                }
            }
        }
    }

    private ObservableList<String> getElementTypes() {
        return FXCollections.observableArrayList(inWorkAttribute.getValues());
    }

    public void setElement(Element element) {
        this.element = element;
    }

    public Element getElement() {
        return this.element;
    }
}