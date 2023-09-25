package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Element;
import javafx.application.Platform;
import javafx.scene.text.Text;

import java.util.Locale;

public class MonitorValueText extends Text {

    private Element element;

    public MonitorValueText(String val1) {
        this.setText(val1);
    }

    public void update() throws Exception {
        float value;
        value = (float) Integer.parseInt(element.getAttributeForMonitoring().readModbus()) / 10;
        float finalValue = value;
        Platform.runLater(() -> this.setText(String.format(Locale.ROOT, "%.2f", finalValue)));
    }

    public void reset() {
        Platform.runLater(() -> this.setText("***"));
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
