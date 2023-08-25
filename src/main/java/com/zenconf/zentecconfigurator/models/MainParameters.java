package com.zenconf.zentecconfigurator.models;

import java.util.List;

public class MainParameters {
    private Attribute startStopAttribute;
    private Attribute resetAlarmsAttribute;
    private Attribute clearJournalAttribute;
    private Attribute controlModeAttribute;
    private Attribute seasonAttribute;
    private Attribute statusAttribute;
    private List<Attribute> heatExchangerAttributes;
    private List<Parameter> peripheryParameters;
    private Attribute alarmsAttribute0;
    private Attribute alarmsAttribute1;
    private Attribute warningsAttribute;
    private Attribute commonAlarmAttribute;
    private Attribute deviceAddressAttribute; // атрибут для проверки соединения

    public Attribute getStartStopAttribute() {
        return startStopAttribute;
    }

    public Attribute getResetAlarmsAttribute() {
        return resetAlarmsAttribute;
    }

    public Attribute getClearJournalAttribute() {
        return clearJournalAttribute;
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

    public Attribute getAlarmsAttribute0() {
        return alarmsAttribute0;
    }

    public Attribute getAlarmsAttribute1() {
        return alarmsAttribute1;
    }

    public Attribute getWarningsAttribute() {
        return warningsAttribute;
    }

    public Attribute getCommonAlarmAttribute() {
        return commonAlarmAttribute;
    }

}
