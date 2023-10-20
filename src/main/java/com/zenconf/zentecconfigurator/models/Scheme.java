package com.zenconf.zentecconfigurator.models;

import com.zenconf.zentecconfigurator.models.elements.Actuator;
import com.zenconf.zentecconfigurator.models.elements.Sensor;

import java.util.List;

public class Scheme {

    private int number;
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

    public int getNumber() {
        return number;
    }

    public Scheme() {

    }
}
