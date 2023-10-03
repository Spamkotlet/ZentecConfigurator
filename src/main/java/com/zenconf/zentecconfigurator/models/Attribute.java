package com.zenconf.zentecconfigurator.models;

import com.zenconf.zentecconfigurator.controllers.ConfiguratorController;
import com.zenconf.zentecconfigurator.models.enums.Controls;
import com.zenconf.zentecconfigurator.models.modbus.ModbusParameter;
import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.List;

public class Attribute {

    private String name;
    private String description;
    private Controls control;
    public ObjectProperty<Integer> currentValueProperty = new SimpleObjectProperty<>(0);
    private Object defaultValue;
    private int minValue;
    private int maxValue;
    private List<String> values;
    private ModbusParameter modbusParameters;
    private final ModbusUtilSingleton modbusUtilSingleton = ModbusUtilSingleton.getInstance();

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Object getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
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

    public ModbusParameter getModbusParameters() {
        return modbusParameters;
    }

    public void setAddress(ModbusParameter modbusParameter) {
        this.modbusParameters = modbusParameter;
    }

    public String readModbus() throws Exception {
        String value = "0";
        value = String.valueOf(modbusUtilSingleton.readModbus(modbusParameters.getAddress(), modbusParameters.getVarType()));
        if (control != null) {
            if (control.equals(Controls.SPINNER)) {
                currentValueProperty.setValue(Integer.valueOf(value));
            }
        }
        if (defaultValue != null) {
            if (currentValueProperty.getValue() != Integer.parseInt(defaultValue.toString())) {
                if (!ConfiguratorController.attributesForResetToDefault.contains(this)) {
                    ConfiguratorController.attributesForResetToDefault.add(this);
                }
            } else {
                ConfiguratorController.attributesForResetToDefault.remove(this);
            }
        }
        return value;
    }

    public void writeModbus(Object value) throws Exception {
        modbusUtilSingleton.writeModbus(modbusParameters.getAddress(), modbusParameters.getVarType(), value);
        if (control != null) {
            if (control.equals(Controls.SPINNER)) {
                currentValueProperty.setValue(Integer.parseInt(value.toString()));
            }
        }
        if (defaultValue != null) {
            if (currentValueProperty.getValue() != Integer.parseInt(defaultValue.toString())) {
                if (!ConfiguratorController.attributesForResetToDefault.contains(this)) {
                    ConfiguratorController.attributesForResetToDefault.add(this);
                }
            } else {
                ConfiguratorController.attributesForResetToDefault.remove(this);
            }
        }
    }

    public void writeModbusDefaultValue(Object value) throws Exception{
        modbusUtilSingleton.writeModbus(modbusParameters.getAddress(), modbusParameters.getVarType(), value);
        if (control != null) {
            if (control.equals(Controls.SPINNER)) {
                currentValueProperty.setValue(Integer.parseInt(value.toString()));
            }
        }
    }
}
