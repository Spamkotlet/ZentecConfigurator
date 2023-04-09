package com.zenconf.zentecconfigurator.models;

import java.util.List;

public class VentSystemSettings {

    private List<Scheme> schemes;

    public VentSystemSettings() {

    }

    public VentSystemSettings(List<Scheme> schemes) {
        this.schemes = schemes;
    }

    public List<Scheme> getSchemes() {
        return schemes;
    }

    public void setSchemes(List<Scheme> schemes) {
        this.schemes = schemes;
    }
}
