package com.mobiledev.uom.flyme.classes;

/**
 * Created by Tolis on 19/12/2016.
 */

public class FlightModel {


//    private String currency;
//    private String price;
//    private List<outboundFlights> outboundFlightsList;
//    private List<inboundFlights> inboundFlightsList;
//
//    public List<outboundFlights> getOutboundFlightsList() {
//        return outboundFlightsList;
//    }
//
//    public void setOutboundFlightsList(List<outboundFlights> outboundFlightsList) {
//        this.outboundFlightsList = outboundFlightsList;
//    }
//
//    public String getCurrency() {
//        return currency;
//    }
//
//    public void setCurrency(String currency) {
//        this.currency = currency;
//    }
//
//    public String getPrice() {
//        return price;
//    }
//
//    public void setPrice(String price) {
//        this.price = price;
//    }
//
//    public List<inboundFlights> getInboundFlightsList() {
//        return inboundFlightsList;
//    }
//
//    public void setInboundFlightsList(List<inboundFlights> inboundFlightsList) {
//        this.inboundFlightsList = inboundFlightsList;
//    }
//
//    public static class outboundFlights{
//        private String departureDate;
//        private String departureTime;
//        private String airline;
//        private String originLoc;
//        private String destinationLoc;
//
//        public String getDepartureDate() {
//            return departureDate;
//        }
//
//        public void setDepartureDate(String departureDate) {
//            this.departureDate = departureDate;
//        }
//
//        public String getDepartureTime() {
//            return departureTime;
//        }
//
//        public void setDepartureTime(String departureTime) {
//            this.departureTime = departureTime;
//        }
//
//        public String getAirline() {
//            return airline;
//        }
//
//        public void setAirline(String airline) {
//            this.airline = airline;
//        }
//
//        public String getOriginLocation() {
//            return originLoc;
//        }
//
//        public void setOriginLocation(String originLoc) {
//            this.originLoc = originLoc;
//        }
//
//        public String getDestinationLocation() {
//            return destinationLoc;
//        }
//
//        public void setDestinationLocation(String destinationLoc) {
//            this.destinationLoc = destinationLoc;
//        }
//    }
//
//    public static class inboundFlights{
//        private String departureDate;
//        private String departureTime;
//        private String arrivalDate;
//        private String arrivalTime;
//        private String airline;
//        private String originLoc;
//        private String destinationLoc;
//
//        public String getDepartureDate() {
//            return departureDate;
//        }
//
//        public void setDepartureDate(String departureDate) {
//            this.departureDate = departureDate;
//        }
//
//        public String getDepartureTime() {
//            return departureTime;
//        }
//
//        public void setDepartureTime(String departureTime) {
//            this.departureTime = departureTime;
//        }
//
//        public String getArrivalDate() {
//            return departureDate;
//        }
//
//        public void setArrivalDate(String arrivalDate) {
//            this.arrivalDate = arrivalDate;
//        }
//
//        public String getArrivalTime() {
//            return arrivalTime;
//        }
//
//        public void setArrivalTime(String arrivalTime) {
//            this.arrivalTime = arrivalTime;
//        }
//
//        public String getAirline() {
//            return airline;
//        }
//
//        public void setAirline(String airline) {
//            this.airline = airline;
//        }
//
//        public String getOriginLocation() {
//            return originLoc;
//        }
//
//        public void setOriginLocation(String originLoc) {
//            this.originLoc = originLoc;
//        }
//
//        public String getDestinationLocation() {
//            return destinationLoc;
//        }
//
//        public void setDestinationLocation(String destinationLoc) {
//            this.destinationLoc = destinationLoc;
//        }
//    }
    private String currency;
    private String departureDate;
    private String arrivalDate;
    private String originLocation;
    private String destinationLocation;
    private String price;
    private String airline;

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
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


}
