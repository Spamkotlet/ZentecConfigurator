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
    public ObjectProperty<Integer> currentValueProperty;
    private Object defaultValue;
    public ObjectProperty<Boolean> isIgnoreDefaultValueProperty = new SimpleObjectProperty<>(false);
    private Boolean isIgnoreDefaultValue;
    private Object initialValue;
    private int minValue;
    private int maxValue;
    private List<String> values;
    private ModbusParameter modbusParameters;
    private final ModbusUtilSingleton modbusUtilSingleton = ModbusUtilSingleton.getInstance();

    private Attribute() {
//        currentValueProperty.addListener(e -> {
//            Platform.runLater(this::addAttributeToDefaultResetList);
//        });
//
        isIgnoreDefaultValueProperty.addListener(e -> {
            addOrRemoveAttributeToDefaultResetList();
        });
    }

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

    public Boolean isIgnoreDefaultValue() {
        return isIgnoreDefaultValue;
    }

    public void setIsIgnoreDefaultValue(boolean isIgnoreDefaultValue) {
        this.isIgnoreDefaultValue = isIgnoreDefaultValue;
    }

    public Object getInitialValue() {
        return initialValue;
    }

    public void setInitialValue(Object initialValue) {
        this.initialValue = initialValue;
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

    public Boolean getIsIgnoreDefaultValueProperty() {
        return isIgnoreDefaultValueProperty.get();
    }

    public ObjectProperty<Boolean> isIgnoreDefaultValuePropertyProperty() {
        return isIgnoreDefaultValueProperty;
    }

    public void setIsIgnoreDefaultValueProperty(Boolean isIgnoreDefaultValueProperty) {
        this.isIgnoreDefaultValueProperty.set(isIgnoreDefaultValueProperty);
    }

    public String readModbus() throws Exception {
        String value = "0";
        value = String.valueOf(modbusUtilSingleton.readModbus(modbusParameters.getAddress(), modbusParameters.getVarType()));
        if (control != null) {
            if (currentValueProperty == null) {
                currentValueProperty = new SimpleObjectProperty<>(0);
            }
            if (control.equals(Controls.SPINNER)) {
                currentValueProperty.setValue(Integer.valueOf(value));
            }
        }
        addOrRemoveAttributeToDefaultResetList();
        return value;
    }

    public void writeModbus(Object value) throws Exception {
        modbusUtilSingleton.writeModbus(modbusParameters.getAddress(), modbusParameters.getVarType(), value);
        if (control != null) {
            if (currentValueProperty == null) {
                currentValueProperty = new SimpleObjectProperty<>(0);
            }
            if (control.equals(Controls.SPINNER)) {
                currentValueProperty.setValue(Integer.parseInt(value.toString()));
            }
        }
        addOrRemoveAttributeToDefaultResetList();
    }

    public void writeModbusDefaultValue(Object value) throws Exception {
        modbusUtilSingleton.writeModbus(modbusParameters.getAddress(), modbusParameters.getVarType(), value);
        if (control != null) {
            if (currentValueProperty == null) {
                currentValueProperty = new SimpleObjectProperty<>(0);
            }
            if (control.equals(Controls.SPINNER)) {
                currentValueProperty.setValue(Integer.parseInt(value.toString()));
            }
        }
    }

    public synchronized void addOrRemoveAttributeToDefaultResetList() {
        if (defaultValue == null) {
            return;
        }
        if (currentValueProperty == null) {
            return;
        }

        if (currentValueProperty.getValue() != Integer.parseInt(defaultValue.toString())) {
            if (!ConfiguratorController.attributesForResetToDefault.contains(this)) {
                if (isIgnoreDefaultValue != null) {
                    if (isIgnoreDefaultValueProperty.getValue()) {
                        ConfiguratorController.attributesForResetToDefault.add(this);
                    } else {
                        ConfiguratorController.attributesForResetToDefault.remove(this);
                    }
                } else {
                    ConfiguratorController.attributesForResetToDefault.add(this);
                }
            } else {
                if (isIgnoreDefaultValue != null) {
                    if (!isIgnoreDefaultValueProperty.getValue()) {
                        ConfiguratorController.attributesForResetToDefault.remove(this);
                    }
                }
            }
        } else {
            ConfiguratorController.attributesForResetToDefault.remove(this);
        }
    }
}
