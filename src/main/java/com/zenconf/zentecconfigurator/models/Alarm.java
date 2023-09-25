package com.zenconf.zentecconfigurator.models;

public class Alarm {

    private int number;
    private String description;
    private String datetime;

    public Alarm(int number, String description, String datetime) {
        this.number = number;
        this.description = description;
        this.datetime = datetime;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }
}
