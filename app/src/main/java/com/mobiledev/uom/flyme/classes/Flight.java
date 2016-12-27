package com.mobiledev.uom.flyme.classes;

/**
 * Created by Lefteris on 25/12/2016.
 */

public class Flight {
    private String departureDate;
    private String arrivalDate;
    private Airline airline;
    private Airport originAirport;
    private Airport destinationAirport;

    public Flight(Airline airline, String departureDate, String arrivalDate, Airport originAirport, Airport destinationAirport) {
        this.airline = airline;
        this.departureDate = departureDate;
        this.arrivalDate = arrivalDate;
        this.originAirport = originAirport;
        this.destinationAirport = destinationAirport;
    }

    public void setOriginAirport(Airport originAirport) {
        this.originAirport = originAirport;
    }

    public void setDestinationAirport(Airport destinationAirport) {
        this.destinationAirport = destinationAirport;
    }

    public void setAirline(Airline airline) {
        this.airline = airline;
    }

    public Airline getAirline() {
        return airline;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public String getDepartureDate() {
        return departureDate;
    }

    public Airport getDestinationAirport() {
        return destinationAirport;
    }

    public Airport getOriginAirport() {
        return originAirport;
    }
}