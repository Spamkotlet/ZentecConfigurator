package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Element;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MonitorTextFlow {

    private final SplitPane parentSplitPane;
    private final Element element;
    private final Text valueText = new Text("***");
    private final List<Float> values = new ArrayList<>();
    XYChart.Series<Number, Number> series = new XYChart.Series<>();

    public MonitorTextFlow(SplitPane parentSplitPane, Element element) {
        this.parentSplitPane = parentSplitPane;
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
        valueText.setFont(Font.font("Arial", 32));
        valueText.setFill(new Color(0.996, 0.4, 0.247, 1));
        valueText.setTextAlignment(TextAlignment.CENTER);
        return valueText;
    }

    private VBox createVBoxForTextFlow() {
        VBox vBox = new VBox();
        vBox.getChildren().add(createValueText());
        vBox.getChildren().add(createNameText());
        vBox.setSpacing(10);
        vBox.setFillWidth(true);
        vBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(vBox, Priority.ALWAYS);
        vBox.setOnMouseClicked(e -> onClickToMonitorTextFlow());
        return vBox;
    }

    public Node getTextFlow() {
        TextFlow textFlow = new TextFlow();
        textFlow.setBackground(new Background(new BackgroundFill(new Color(0.831, 0.831, 0.831, 1), CornerRadii.EMPTY, Insets.EMPTY)));
        textFlow.setTextAlignment(TextAlignment.CENTER);
        textFlow.setPadding(new Insets(10));

        textFlow.getChildren().add(createVBoxForTextFlow());

        return textFlow;
    }

    public void update() {
        float value = Float.parseFloat(element.getAttributeForMonitoring().readModbusParameter());
        valueText.setText(String.format(Locale.ROOT, "%.2f", value));
        if (values.size() > 300) {
            series.getData().clear();
            values.remove(0);
            for (int i = 0; i < values.size(); i++) {
                series.getData().add(new XYChart.Data<>(i, values.get(i)));
            }
        } else {
            series.getData().add(new XYChart.Data<>(series.getData().size(), value));
        }
        values.add(value);
    }

    private void onClickToMonitorTextFlow() {
        VBox plotPane = null;
        for (Node node : parentSplitPane.getItems()) {
            if (node instanceof VBox)
                if (node.getId() != null && node.getId().equals("plotPane")) {
                plotPane = (VBox) node;
                break;
            }
        }

        LineChart<Number, Number> chart = new LineChart<>(new NumberAxis(), new NumberAxis());
        chart.setAnimated(false);
        chart.setCreateSymbols(false);
        chart.getData().add(series);
        chart.setTitle(element.getName());

        AnchorPane.setBottomAnchor(chart, 0d);
        AnchorPane.setLeftAnchor(chart, 0d);
        AnchorPane.setRightAnchor(chart, 0d);
        AnchorPane.setTopAnchor(chart, 0d);

        if (plotPane != null) {
            plotPane.getChildren().clear();
            plotPane.getChildren().add(chart);
        } else {
            plotPane = new VBox();
            plotPane.setId("plotPane");
            plotPane.getChildren().add(chart);
            parentSplitPane.getItems().add(plotPane);
        }
    }
}
