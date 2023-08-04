package com.zenconf.zentecconfigurator.models.modbus;

import com.intelligt.modbus.jlibmodbus.utils.ModbusFunctionCode;
import com.zenconf.zentecconfigurator.models.enums.VarFunctions;
import com.zenconf.zentecconfigurator.models.enums.VarTypes;

public class ModbusParameter {

    private int address;
    private VarTypes varType;

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setVarType(VarTypes varType) {
        this.varType = varType;
    }

    public VarTypes getVarType() {
        return varType;
    }
}
