package com.zenconf.zentecconfigurator.models;

import java.util.List;

public class MainParameters {
    private Attribute startStopAttribute;
    private Attribute resetAlarmsAttribute;
    private Attribute controlModeAttribute;
    private Attribute seasonAttribute;
    private Attribute statusAttribute;
    private List<Attribute> heatExchangerAttributes;
    private List<Parameter> peripheryParameters;
    private List<Attribute> alarms;
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

    public List<Attribute> getHeatExchangerAttributes() {
        return heatExchangerAttributes;
    }

    public List<Parameter> getPeripheryParameters() {
        return peripheryParameters;
    }

    public List<Attribute> getAlarms() {
        return alarms;
    }
}
