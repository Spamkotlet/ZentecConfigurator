package com.zenconf.zentecconfigurator.models;

import java.util.List;

public class Scheme {

    private String number;
    private String name;
    private List<Actuator> actuators;

    public String getName() {
        return name;
    }

    public List<Actuator> getActuators() {
        return actuators;
    }

    public String getNumber() {
        return number;
    }

    public Scheme() {

    }

    public Scheme(String number, String name, List<Actuator> actuators) {
        this.number = number;
        this.name = name;
        this.actuators = actuators;
    }
}
