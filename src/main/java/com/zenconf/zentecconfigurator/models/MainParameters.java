package com.zenconf.zentecconfigurator.models;

public class MainParameters {
    private Attribute startStopAttribute;
    private Attribute resetAlarmsAttribute;
    private Attribute controlModeAttribute;
    private Attribute seasonAttribute;
    private Attribute statusAttribute;

    public Attribute getStartStopAttribute() {
        return startStopAttribute;
    }

    public void setStartStopAttribute(Attribute startStopAttribute) {
        this.startStopAttribute = startStopAttribute;
    }

    public Attribute getResetAlarmsAttribute() {
        return resetAlarmsAttribute;
    }

    public void setResetAlarmsAttribute(Attribute resetAlarmsAttribute) {
        this.resetAlarmsAttribute = resetAlarmsAttribute;
    }

    public Attribute getControlModeAttribute() {
        return controlModeAttribute;
    }

    public void setControlModeAttribute(Attribute controlModeAttribute) {
        this.controlModeAttribute = controlModeAttribute;
    }

    public Attribute getSeasonAttribute() {
        return seasonAttribute;
    }

    public void setSeasonAttribute(Attribute seasonAttribute) {
        this.seasonAttribute = seasonAttribute;
    }

    public Attribute getStatusAttribute() {
        return statusAttribute;
    }

    public void setStatusAttribute(Attribute statusAttribute) {
        this.statusAttribute = statusAttribute;
    }
}
