package com.zenconf.zentecconfigurator.models.modbus;

import com.intelligt.modbus.jlibmodbus.utils.ModbusFunctionCode;

public class ModbusParameter {

    private int address;
    private ModbusFunctionCode readFunctionCode;
    private ModbusFunctionCode writeFunctionCode;

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public ModbusFunctionCode getReadFunctionCode() {
        return readFunctionCode;
    }

    public void setReadFunctionCode(ModbusFunctionCode readFunctionCode) {
        this.readFunctionCode = readFunctionCode;
    }

    public ModbusFunctionCode getWriteFunctionCode() {
        return writeFunctionCode;
    }

    public void setWriteFunctionCode(ModbusFunctionCode writeFunctionCode) {
        this.writeFunctionCode = writeFunctionCode;
    }
}
