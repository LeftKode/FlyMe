package com.mobiledev.uom.flyme.classes;


import java.io.Serializable;
import java.util.List;

public class Itinerary implements Serializable {
    private FlightModel model;  //Το flightModel που το περιέχει
    private List<Flight> outboundFlightsList; //Λίστα με τις πτήσεις αναχώρησης
    private List<Flight> inboundFlightsList; //Λίστα με τις πτήσεις επιστροφής

    public Itinerary(FlightModel model) {
        this.model = model;
        this.outboundFlightsList = outboundFlightsList;
    }

    public Itinerary(List<Flight> outboundFlightsList, List<Flight> inboundFlightsList) {
        this.inboundFlightsList = inboundFlightsList;
        this.outboundFlightsList = outboundFlightsList;
    }

    public FlightModel getModel() {
        return model;
    }

    public void setInboundFlightsList(List<Flight> inboundFlightsList) {
        this.inboundFlightsList = inboundFlightsList;
    }

    public void setOutboundFlightsList(List<Flight> outboundFlightsList) {
        this.outboundFlightsList = outboundFlightsList;
    }

    public List<Flight> getInboundFlightsList() {

        return inboundFlightsList;
    }

    public List<Flight> getOutboundFlightsList() {
        return outboundFlightsList;
    }
}
