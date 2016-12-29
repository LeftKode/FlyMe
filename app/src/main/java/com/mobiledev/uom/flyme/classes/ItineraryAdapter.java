package com.mobiledev.uom.flyme.classes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobiledev.uom.flyme.R;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by Lefteris on 27/12/2016.
 */

public class ItineraryAdapter extends ArrayAdapter {

    public List<Itinerary> itinerariesList;

    //public List<FlightModel> flightModelList;
    private int resource;
    private LayoutInflater inflater;

    public ItineraryAdapter(Context context, int resource, List<Itinerary> objects){
        super(context, resource, objects);
        itinerariesList = objects;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
    }

    //καλείται τόσες φόρες όσα και τα αντικείμενα που έχουμε
    @Override
    public View getView(int position, View convertView, ViewGroup parent){


        if (convertView == null){
            convertView = inflater.inflate(R.layout.results_listview_item, null);
        }

        TextView resultsDepDetTV = (TextView)convertView.findViewById(R.id.results_depart_details);
        TextView resultsRetDetTV = (TextView)convertView.findViewById(R.id.results_return_details);
        TextView resultsStopNoTV = (TextView)convertView.findViewById(R.id.results_stop_no);
        TextView resultsTotalPriceTV = (TextView)convertView.findViewById(R.id.results_total_price);

        List<Flight> outboundsList = itinerariesList.get(position).getOutboundFlightsList();
        List<Flight> inboundsList = itinerariesList.get(position).getInboundFlightsList();

        Flight originFlight = outboundsList.get(0);
        Flight returnFlight = inboundsList.get(0);
        Flight arrivalFlight = outboundsList.get(outboundsList.size()-1);
        Flight arrivalReturnFlight = inboundsList.get(inboundsList.size()-1);

        String originDepartDateStr = originFlight.getDepartureDate();
        String originDepartTimeStr = originDepartDateStr.substring(originDepartDateStr.length()-5,originDepartDateStr.length());

        String arrivalDepartDateStr = arrivalFlight.getDepartureDate();
        String arrivalDepartTimeStr = arrivalDepartDateStr.substring(arrivalDepartDateStr.length()-5,arrivalDepartDateStr.length());



        String originStr = originFlight.getOriginAirport().getValue();
        String destinStr = returnFlight.getOriginAirport().getValue();

        String returnStr;

        int stopsDepart = 0;
        int stopsReturn = 0;

        if(inboundsList!=null){
            String returnDepartDateStr = returnFlight.getDepartureDate();
            String returnDepartTimeStr = returnDepartDateStr.substring(returnDepartDateStr.length()-5,returnDepartDateStr.length());

            String arrivalReturnDateStr = arrivalReturnFlight.getDepartureDate();
            String arrivalReturnTimeStr = arrivalReturnDateStr.substring(arrivalReturnDateStr.length()-5,arrivalReturnDateStr.length());

            stopsReturn = inboundsList.size()-1;
            returnStr = "ΕΠ " + destinStr + "-" + originStr + " [" + returnDepartTimeStr + "-" + arrivalReturnTimeStr+"]";
            resultsRetDetTV.setVisibility(View.VISIBLE);
            resultsRetDetTV.setText(returnStr);
        }else{
            resultsRetDetTV.setVisibility(View.GONE);
        }

        String stopsStr = "Στάσεις" + '(' + (outboundsList.size()-1) + ')';

        if(inboundsList!=null){
            String stopsReturnStr;
            stopsReturnStr = "Στάσεις Επιστροφής" + '(' + (inboundsList.size()-1) + ')';
        }

        float totalPrice = itinerariesList.get(position).getModel().getTotalPrice();

        String departStr = "ΑΝ " + originStr + "-" + destinStr + " [" + originDepartTimeStr + "-" + arrivalDepartTimeStr+"]";
        resultsDepDetTV.setText(departStr);


        resultsStopNoTV.setText(stopsStr);
        resultsTotalPriceTV.setText(Float.toString(totalPrice));

        /*//καλώ το substring για να μην εμφανίζει το [code]
        originLoc.setText(originAirportName.substring(0,originAirportName.indexOf("[")-1));

        destinationLoc.setText(destinationAirportName.substring(0,destinationAirportName.indexOf("[")-1));

        departDate.setText(":" +" "+flightModelList.get(position).getDepartureDate());
        arrivalDate.setText(":" +" "+flightModelList.get(position).getArrivalDate());

        airline.setText(airlineMap.get(flightModelList.get(position).getAirline()));

        currency.setText(flightModelList.get(position).getPrice()
                + " " + flightModelList.get(position).getCurrency());*/


        return convertView;
    }
}
