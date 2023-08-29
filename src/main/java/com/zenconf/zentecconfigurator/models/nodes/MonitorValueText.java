package com.zenconf.zentecconfigurator.models.nodes;

import com.zenconf.zentecconfigurator.models.Element;
import javafx.scene.text.Text;

import java.util.Locale;

public class MonitorValueText extends Text {

    private Element element;

    public MonitorValueText(String val1) {
        this.setText(val1);
    }

    public void update() throws Exception {
        float value = 0.0f;
        value = Float.parseFloat(element.getAttributeForMonitoring().readModbusParameter());
        this.setText(String.format(Locale.ROOT, "%.2f", value));
    }

    public void setElement(Element element) {
        this.element = element;
    }
}
