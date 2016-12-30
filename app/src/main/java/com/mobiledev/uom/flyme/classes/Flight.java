package com.mobiledev.uom.flyme.classes;

import java.util.Calendar;

/**
 * Created by Lefteris on 25/12/2016.
 */

public class Flight {
    private Calendar departureDate;
    private Calendar arrivalDate;
    private Airline airline;
    private Airport originAirport;
    private Airport destinationAirport;

    public Flight(Airline airline, Calendar departureDate, Calendar arrivalDate, Airport originAirport, Airport destinationAirport) {
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

    public Calendar getArrivalDate() {
        return arrivalDate;
    }

    public Calendar getDepartureDate() {
        return departureDate;
    }

    public Airport getDestinationAirport() {
        return destinationAirport;
    }

    public Airport getOriginAirport() {
        return originAirport;
    }
}