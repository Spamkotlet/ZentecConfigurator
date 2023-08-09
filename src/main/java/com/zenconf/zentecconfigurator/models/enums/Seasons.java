package com.zenconf.zentecconfigurator.models.enums;

public enum Seasons {
    VENTILATION("Вентиляция", 1),
    HEATING("Нагревание", 2),
    COOLING("Охлаждение", 4),
    AUTO("АВТО", 8);

    private final String displayValue;
    private final int number;

    Seasons(String displayValue, int value) {
        this.displayValue = displayValue;
        this.number = value;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }

    public int getNumber() {
        return this.number;
    }
}
