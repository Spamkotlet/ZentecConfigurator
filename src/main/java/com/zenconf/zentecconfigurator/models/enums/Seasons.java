package com.zenconf.zentecconfigurator.models.enums;

public enum Seasons {
    VENTILATION("Вентиляция", 1, 0),
    HEATING("Нагревание", 2, 1),
    COOLING("Охлаждение", 4, 2),
    AUTO("АВТО", 8, 3),
    VENT_AUTO("Вентиляция (АВТО)", 9, 4),
    HEAT_AUTO("Нагревание (АВТО)", 10, 5),
    COOL_AUTO("Нагревание (АВТО)", 12, 6);

    private final String displayValue;
    private final int number;
    private final int index;

    Seasons(String displayValue, int value, int index) {
        this.displayValue = displayValue;
        this.number = value;
        this.index = index;
    }

    public String getDisplayValue() {
        return this.displayValue;
    }

    public int getNumber() {
        return this.number;
    }

    public int getIndex() {
        return index;
    }
}
