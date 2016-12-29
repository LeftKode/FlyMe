package com.mobiledev.uom.flyme.classes;

import java.util.List;

/**
 * Created by Tolis on 19/12/2016.
 */

public class FlightModel {

    private String currency;
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

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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
/*
    public static class inboundFlights {
        private String departureDate;
        private String departureTime;
        private String arrivalDate;
        private String arrivalTime;
        private String airline;
        private String originLoc;
        private String destinationLoc;

        public String getDepartureDate() {
            return departureDate;
        }

        public void setDepartureDate(String departureDate) {
            this.departureDate = departureDate;
        }

        public String getDepartureTime() {
            return departureTime;
        }

        public void setDepartureTime(String departureTime) {
            this.departureTime = departureTime;
        }

        public String getArrivalDate() {
            return departureDate;
        }

        public void setArrivalDate(String arrivalDate) {
            this.arrivalDate = arrivalDate;
        }

        public String getArrivalTime() {
            return arrivalTime;
        }

        public void setArrivalTime(String arrivalTime) {
            this.arrivalTime = arrivalTime;
        }

        public String getAirline() {
            return airline;
        }

        public void setAirline(String airline) {
            this.airline = airline;
        }

        public String getOriginLocation() {
            return originLoc;
        }

        public void setOriginLocation(String originLoc) {
            this.originLoc = originLoc;
        }

        public String getDestinationLocation() {
            return destinationLoc;
        }

        public void setDestinationLocation(String destinationLoc) {
            this.destinationLoc = destinationLoc;
        }
    }
}*/
/*    private String currency;
    private String departureDate;
    private String arrivalDate;
    private String originLocation;
    private String destinationLocation;
    private String totalPrice;
    private String airline;

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public void setDepartureDate(String departureDate) {
        this.departureDate = departureDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOriginLocation() {
        return originLocation;
    }

    public void setOriginLocation(String originLocation) {
        this.originLocation = originLocation;
    }


}*/
