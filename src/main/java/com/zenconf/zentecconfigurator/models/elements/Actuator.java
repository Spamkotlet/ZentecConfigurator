package com.zenconf.zentecconfigurator.models.elements;

import com.zenconf.zentecconfigurator.models.Attribute;

import java.util.List;

public class Actuator extends Element {
    private List<Attribute> functionalities;

    public List<Attribute> getFunctionalities() {
        return functionalities;
    }

    public void setFunctionalities(List<Attribute> functionalities) {
        this.functionalities = functionalities;
    }
}
