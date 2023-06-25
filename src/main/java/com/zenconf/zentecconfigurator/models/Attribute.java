package com.zenconf.zentecconfigurator.models;

import com.zenconf.zentecconfigurator.models.modbus.ModbusParameter;

import java.util.List;

public class Attribute {

    private String name;
    private Controls control;
    private double minValue;
    private double maxValue;
    private List<String> values;
    private ModbusParameter modbusParameters;

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

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public List<String> getValues() {
        return values;
    }

    public ModbusParameter getModbusParameters() {
        return modbusParameters;
    }

    public void setAddress(ModbusParameter modbusParameter) {
        this.modbusParameters = modbusParameter;
    }
}
