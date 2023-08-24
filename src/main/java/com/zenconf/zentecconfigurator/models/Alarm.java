package com.zenconf.zentecconfigurator.models;

import java.util.Date;

public class Alarm {

    private int number;
    private int index;
    private String description;
    private Date datetime;

    public Alarm(int number, String description, Date datetime) {
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

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }
}
