package com.zenconf.zentecconfigurator.models;

import java.util.List;

public abstract class Element {

    private String name;
    private boolean isUsed;
    private boolean isUsedDefault;
    private Attribute isInWorkAttribute;
    private List<Attribute> attributes;
    private Attribute attributeForMonitoring;
    private List<Attribute> attributesForControlling;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getUsed() {
        return isUsed;
    }

    public void setUsed(boolean used) {
        isUsed = used;
    }

    public boolean getIsUsedDefault() {
        return this.isUsedDefault;
    }

    public void setIsUsedDefault(boolean isUsedDefault) {
        this.isUsedDefault = isUsedDefault;
    }

    public Attribute getIsInWorkAttribute() {
        return isInWorkAttribute;
    }

    public void setIsInWorkAttribute(Attribute isInWorkAttribute) {
        this.isInWorkAttribute = isInWorkAttribute;
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

    public List<Attribute> getAttributesForControlling() {
        return attributesForControlling;
    }

    public void setAttributesForControlling(List<Attribute> attributesForControlling) {
        this.attributesForControlling = attributesForControlling;
    }
}
