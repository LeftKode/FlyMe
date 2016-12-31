package com.mobiledev.uom.flyme.classes;

import java.io.Serializable;

/**
 * Created by Lefteris on 26/12/2016.
 */

public class Airline implements Serializable {

    private String code;    //Kωδικός Αεροπορικής Εταιρείας
    private String name;    //Όνομα Αεροπορικής Εταιρείας

    public Airline(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }
}
