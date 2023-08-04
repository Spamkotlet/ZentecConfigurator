package com.zenconf.zentecconfigurator.models.enums;

public enum VarTypes {
    BOOL("Логический"),
    UINT8("Беззнак.целый 1-байт"),
    UINT16("Беззнак.целый 2-байт"),
    UINT32("Беззнак.целый 4-байт"),
    SINT8("Знак.целый 1-байт"),
    SINT16("Знак.целый 2-байт"),
    SINT32("Знак.целый 4-байт"),
    FLOAT("Дробный 4-байт");

    private final String displayValue;

    VarTypes(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }
}
