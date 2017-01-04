package com.mobiledev.uom.flyme.classes;

import java.io.Serializable;
import java.util.Calendar;

//Η κλάση με τις λεπτομέρειες κάθε πτήσης
public class Flight implements Serializable {
    private Calendar departureDate; //Ημερομηνία Αναχώρησης
    private Calendar arrivalDate; //Ημερομηνία Άφιξης
    private Airline airline; //Αεροπορική Εταιρία Πτήσης
    private Airport originAirport; //Αεροδρόμιο Αναχώρησης
    private Airport destinationAirport; //Αεροδρόμιο Προορισμού

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