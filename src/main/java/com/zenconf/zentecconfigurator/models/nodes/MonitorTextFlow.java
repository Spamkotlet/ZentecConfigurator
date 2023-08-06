package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Element;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

public class MonitorTextFlow {

    private Element element;
    private Text valueText = new Text("***");

    public MonitorTextFlow(Element element) {
        this.element = element;
    }

    private Text createNameText() {
        Text nameText = new Text(element.getAttributeForMonitoring().getName());
        nameText.setFont(Font.font("Verdana", 14));
        nameText.setFill(Color.BLACK);
        nameText.setTextAlignment(TextAlignment.CENTER);

        return nameText;
    }

    private Text createValueText() {
        valueText.setFont(Font.font("Arial", 28));
        valueText.setFill(new Color(0.996, 0.4, 0.247, 1));
        valueText.setTextAlignment(TextAlignment.CENTER);
        return valueText;
    }

    public Node getTextFlow() {
        TextFlow textFlow = new TextFlow();
        textFlow.setBackground(new Background(new BackgroundFill(new Color(0.831, 0.831, 0.831, 1), CornerRadii.EMPTY, Insets.EMPTY)));
        textFlow.setTextAlignment(TextAlignment.CENTER);
        VBox vBox = new VBox();
        vBox.getChildren().add(createValueText());
        vBox.getChildren().add(createNameText());
        vBox.setSpacing(10);
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox, Priority.ALWAYS);

        textFlow.getChildren().add(vBox);

        return textFlow;
    }

    public void update() {
        valueText.setText(element.getAttributeForMonitoring().readModbusParameter());
    }
}
