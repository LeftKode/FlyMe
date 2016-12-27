package com.mobiledev.uom.flyme.classes;

import java.util.List;

/**
 * Created by Lefteris on 26/12/2016.
 */

public class Itinerary {
    private List<Flight> outboundFlightsList;
    private List<Flight> inboundFlightsList;

    public Itinerary(List<Flight> outboundFlightsList) {
        this.outboundFlightsList = outboundFlightsList;
    }

    public Itinerary(List<Flight> outboundFlightsList, List<Flight> inboundFlightsList) {
        this.inboundFlightsList = inboundFlightsList;
        this.outboundFlightsList = outboundFlightsList;
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
