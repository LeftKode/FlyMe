package com.mobiledev.uom.flyme.classes;

public class Airport {

    private String code;
    private String label;

    public Airport(String value, String label) {
        this.code = value;
        this.label = label;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }
}
