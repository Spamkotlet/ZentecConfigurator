package com.zenconf.zentecconfigurator.models.enums;

public enum VarFunctions {
    READ("Чтение"),
    WRITE("Запись");

    private final String displayValue;

    VarFunctions(String displayValue) {
        this.displayValue = displayValue;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }
}
