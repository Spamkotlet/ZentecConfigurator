package com.zenconf.zentecconfigurator.models;

public class MainParameters {
    private Attribute startStopAttribute;
    private Attribute resetAlarmsAttribute;
    private Attribute controlModeAttribute;
    private Attribute seasonAttribute;
    private Attribute statusAttribute;
    private Attribute deviceAddressAttribute; // атрибут для проверки соединения

    public Attribute getStartStopAttribute() {
        return startStopAttribute;
    }

    public Attribute getResetAlarmsAttribute() {
        return resetAlarmsAttribute;
    }

    public Attribute getControlModeAttribute() {
        return controlModeAttribute;
    }

    public Attribute getSeasonAttribute() {
        return seasonAttribute;
    }

    public Attribute getStatusAttribute() {
        return statusAttribute;
    }

    public Attribute getDeviceAddressAttribute() {
        return deviceAddressAttribute;
    }
}
