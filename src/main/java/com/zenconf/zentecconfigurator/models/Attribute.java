package com.zenconf.zentecconfigurator.models;

import com.zenconf.zentecconfigurator.models.enums.Controls;
import com.zenconf.zentecconfigurator.models.enums.VarTypes;
import com.zenconf.zentecconfigurator.models.modbus.ModbusParameter;
import com.zenconf.zentecconfigurator.utils.modbus.ModbusUtilSingleton;

import java.util.List;

public class Attribute {

    private String name;
    private Controls control;
    private double minValue;
    private double maxValue;
    private List<String> values;
    private ModbusParameter modbusParameters;
    private ModbusUtilSingleton modbusUtilSingleton;

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

    public String readModbusParameter() {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        String value;
        if (modbusUtilSingleton.getMaster() != null) {
            value = String.valueOf(modbusUtilSingleton.readModbus(modbusParameters.getAddress(), modbusParameters.getVarType()));
        } else {
            value = "0";
        }
        return value;
    }

    public void writeModbusParameter(Object value) {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        if (modbusUtilSingleton.getMaster() != null) {
            if (modbusParameters.getVarType().equals(VarTypes.BOOL)) {
                modbusUtilSingleton.writeModbusCoil(modbusParameters.getAddress(), Boolean.parseBoolean(value.toString()));
            } else if (modbusParameters.getVarType().equals(VarTypes.FLOAT)) {
                modbusUtilSingleton.writeMultipleModbusRegister(modbusParameters.getAddress(), Float.parseFloat(value.toString()));
            } else {
                modbusUtilSingleton.writeSingleModbusRegister(modbusParameters.getAddress(), Math.round(Float.parseFloat(value.toString())), modbusParameters.getVarType());
            }
        }
    }
}
