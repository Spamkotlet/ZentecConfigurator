package com.zenconf.zentecconfigurator.models;

import java.util.List;

public class Attribute {

    private String name;
    private Controls control;
    private int minValue;
    private int maxValue;
    private List<String> values;
    private int address;

    public void setName(String name) {
        this.name = name;
    }

    public void setControl(Controls control) {
        this.control = control;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public Controls getControl() {
        return control;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public List<String> getValues() {
        return values;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }
}
