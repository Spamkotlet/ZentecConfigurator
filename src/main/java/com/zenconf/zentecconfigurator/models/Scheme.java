package com.zenconf.zentecconfigurator.models;

import java.util.List;

public class Scheme {

    private String number;
    private String name;
    private List<String> actuators;

    public String getName() {
        return name;
    }

    public List<String> getActuators() {
        return actuators;
    }

    public String getNumber() {
        return number;
    }

    public Scheme() {

    }

    public Scheme(String number, String name, List<String> actuators) {
        this.number = number;
        this.name = name;
        this.actuators = actuators;
    }
}
