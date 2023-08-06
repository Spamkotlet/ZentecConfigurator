package com.zenconf.zentecconfigurator.models;

import java.util.List;

public abstract class Element {

    private String name;
    private Boolean isUsedDefault;
    private List<Attribute> attributes;
    private Attribute attributeForMonitoring;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getIsUsedDefault() {
        return this.isUsedDefault;
    }

    public void setIsUsedDefault(Boolean isUsedDefault) {
        this.isUsedDefault = isUsedDefault;
    }

    public List<Attribute> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(List<Attribute> attributes) {
        this.attributes = attributes;
    }

    public Attribute getAttributeForMonitoring() {
        return attributeForMonitoring;
    }

    public void setAttributeForMonitoring(Attribute attributeForMonitoring) {
        this.attributeForMonitoring = attributeForMonitoring;
    }
}
