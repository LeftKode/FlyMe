package com.mobiledev.uom.flyme.classes;

import java.io.Serializable;
import java.util.List;

//Κλάση που θα περιέχει ένα result από το JSON
public class FlightModel implements Serializable {


    private float totalPrice;
    private float pricePerAdult;
    //private float taxAdults;
    private float pricePerInfant;

    public float getPricePerChild() {
        return pricePerChild;
    }

    public void setPricePerChild(float pricePerChild) {
        this.pricePerChild = pricePerChild;
    }

    private float pricePerChild;
    //private float taxInfants;
    private List<Itinerary> itineraries;

    public List<Itinerary> getItineraries() {
        return itineraries;
    }

    public void setItineraries(List<Itinerary> itineraries) {
        this.itineraries = itineraries;
    }

    public float getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void setPricePerAdult(float pricePerAdult) {
        this.pricePerAdult = pricePerAdult;
    }

    public void setPricePerInfant(float pricePerInfant) {
        this.pricePerInfant = pricePerInfant;
    }

    /*public void setTaxAdults(float taxAdults) {
        this.taxAdults = taxAdults;
    }*/

    /*public void setTaxInfants(float taxInfants) {
        this.taxInfants = taxInfants;
    }*/

    public float getPricePerAdult() {
        return pricePerAdult;
    }

    public float getPricePerInfant() {
        return pricePerInfant;
    }

    /*public float getTaxAdults() {
        return taxAdults;
    }*/

    /*public float getTaxInfants() {
        return taxInfants;
    }*/


}
