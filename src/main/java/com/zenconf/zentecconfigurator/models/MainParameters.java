package com.zenconf.zentecconfigurator.models;

import java.util.List;

public class MainParameters {
    private Attribute schemeNumberAttribute;
    private Attribute startStopAttribute;
    private Attribute resetAlarmsAttribute;
    private Attribute clearJournalAttribute;
    private Attribute controlModeAttribute;
    private Attribute seasonAttribute;
    private Attribute statusAttribute;
    private Attribute statusRemainingTimeAttribute;
    private Attribute waitingTimeAttribute;
    private List<Attribute> heatExchangerAttributes;
    private List<Attribute> valveHeatersAttributes;
    private List<Attribute> valveAttributes;
    private List<Parameter> peripheryParameters;
    private Attribute alarmsAttribute0;
    private Attribute alarmsAttribute1;
    private Attribute warningsAttribute;
    private Attribute deviceAddressAttribute; // атрибут для проверки соединения M245
    private Attribute indexHMIAttribute; // атрибут для проверки соединения Z031

    public Attribute getSchemeNumberAttribute() {
        return schemeNumberAttribute;
    }

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

    public Attribute getStatusRemainingTimeAttribute() {
        return statusRemainingTimeAttribute;
    }

    public Attribute getWaitingTimeAttribute() {
        return waitingTimeAttribute;
    }

    public Attribute getDeviceAddressAttribute() {
        return deviceAddressAttribute;
    }

    public List<Attribute> getHeatExchangerAttributes() {
        return heatExchangerAttributes;
    }

    public List<Attribute> getValveHeatersAttributes() {
        return valveHeatersAttributes;
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

    public Attribute getIndexHMIAttribute() {
        return indexHMIAttribute;
    }

    public List<Attribute> getValveAttributes() {
        return valveAttributes;
    }
}
