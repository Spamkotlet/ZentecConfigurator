package com.zenconf.zentecconfigurator.models;

import com.intelligt.modbus.jlibmodbus.utils.ModbusFunctionCode;
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
        String value = "";
        if (modbusUtilSingleton.getMaster() != null) {
            if (modbusParameters.getReadFunctionCode() == ModbusFunctionCode.READ_COILS) {
                value = String.valueOf(modbusUtilSingleton.readModbusCoil(modbusParameters.getAddress()));
            } else if (modbusParameters.getReadFunctionCode() == ModbusFunctionCode.READ_HOLDING_REGISTERS) {
                value = String.valueOf(modbusUtilSingleton.readSingleModbusRegister(modbusParameters.getAddress()));
            }
        }
        return value;
    }

    public void writeModbusParameter(Object value) {
        modbusUtilSingleton = ModbusUtilSingleton.getInstance();
        if (modbusUtilSingleton.getMaster() != null) {
            if (value.getClass().equals(Boolean.class)) {
                modbusUtilSingleton.writeModbusCoil(modbusParameters.getAddress(), (boolean) value);
            } else if (value.getClass().equals(Integer.class)) {
                modbusUtilSingleton.writeSingleModbusRegister(modbusParameters.getAddress(), (int) value);
            }
        }
    }
}
