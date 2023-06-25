package com.zenconf.zentecconfigurator.models;

import java.util.List;

public class Scheme {

    private String number;
    private String name;
    private List<Actuator> actuators;
    private List<Sensor> sensors;

    public String getName() {
        return name;
    }

    public List<Actuator> getActuators() {
        return actuators;
    }
    public List<Sensor> getSensors() {
        return sensors;
    }

    public String getNumber() {
        return number;
    }

    public Scheme() {

    }
}
