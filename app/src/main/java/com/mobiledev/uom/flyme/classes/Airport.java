package com.mobiledev.uom.flyme.classes;

public class Airport {

    private String value;
    private String label;

    public Airport(String value, String label) {
        this.value = value;
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public String getLabel() {
        return label;
    }
}
