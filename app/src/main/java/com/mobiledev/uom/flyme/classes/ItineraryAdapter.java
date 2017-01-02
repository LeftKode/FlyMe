package com.mobiledev.uom.flyme.classes;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.mobiledev.uom.flyme.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * Created by Lefteris on 27/12/2016.
 */

public class ItineraryAdapter extends ArrayAdapter {

    public List<Itinerary> itinerariesList;

    //public List<FlightModel> flightModelList;
    private int resource;
    private LayoutInflater inflater;
    private static Context context;
    private Activity activity;
    private DecimalFormat decimalFormat = new DecimalFormat("#,###.00");


    public ItineraryAdapter(Context context, int resource, List<Itinerary> objects, Activity activity){
        super(context, resource, objects);
        itinerariesList = objects;
        this.resource = resource;
        inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        ItineraryAdapter.context = context;
        this.activity = activity;
    }

    //καλείται τόσες φόρες όσα και τα αντικείμενα που έχουμε
    @Override
    public View getView(int position, View convertView, ViewGroup parent){


        if (convertView == null){
            convertView = inflater.inflate(R.layout.results_listview_item, null);
        }

        TextView transition_airportLoc_TV = (TextView)convertView.findViewById(R.id.results_transition_airport_loc);
        TextView transition_travelTime_TV = (TextView)convertView.findViewById(R.id.results_transition_times_tv);
        TextView transition_flightDuration_TV = (TextView)convertView.findViewById(R.id.results_transition_duration_tv);
        TextView results_return_details_TV = (TextView)convertView.findViewById(R.id.results_return_details_tv);
        TextView return_airportLoc_TV = (TextView)convertView.findViewById(R.id.results_return_airport_loc);
        TextView return_travelTime_TV = (TextView)convertView.findViewById(R.id.results_return_times_tv);
        TextView return_flightDuration_TV = (TextView)convertView.findViewById(R.id.results_return_duration_tv);

        TextView leavingStopsTV = (TextView)convertView.findViewById(R.id.results_leaving_stops_tv);
        TextView returnStopsTV = (TextView)convertView.findViewById(R.id.results_return_stops_tv);
        TextView totalPriceTV = (TextView)convertView.findViewById(R.id.results_total_price_tv);

        List<Flight> outboundsList = itinerariesList.get(position).getOutboundFlightsList();
        List<Flight> inboundsList = itinerariesList.get(position).getInboundFlightsList();

        //Η πρώτη και τελευταία πτήση μετάβασης
        Flight departFlight = outboundsList.get(0);
        Flight arrivalFlight = outboundsList.get(outboundsList.size()-1);

        //Τα αεροδρόμια αναχώρησης-άφιξης της μετάβασης
        String originStr = departFlight.getOriginAirport().getValue();
        String destinStr = arrivalFlight.getDestinationAirport().getValue();


        //To format που θα εμφανίζονται οι ώρες
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");

        //Οι ώρες αναχώρησης-άφιξης της μετάβασης
        Calendar departTime = departFlight.getDepartureDate();
        String departTimeStr = dateFormat.format(departTime.getTime());
        Calendar arrivalTime = arrivalFlight.getArrivalDate();
        String arrivalTimeStr = dateFormat.format(arrivalTime.getTime());

        String departdiffTimeStr = getTimeDiffernceString(departTime,arrivalTime);

//        //Το text των λεπτομερειών της αναχώρησης
//        String departStr = convertView.getResources().getString(R.string.depart_title_abbrev) +
//                " " + originStr + "→" + destinStr + " [" + departTimeStr + "-" + arrivalTimeStr+"] " + departdiffTimeStr;



        //Τα αντίστοιχα textViews παίρνουν τα text
        transition_airportLoc_TV.setText(originStr + "->" + destinStr);
        transition_travelTime_TV.setText("[" + departTimeStr + "-" + arrivalTimeStr+"]");
        transition_flightDuration_TV.setText(departdiffTimeStr);
        if(outboundsList.size()-1 != 0){
            //Το text των στάσεων της αναχώρησης
            String stopsStr = convertView.getResources().getString(R.string.stops) +
                    '(' + (outboundsList.size()-1) + ')';

            //Προσθέση αν υπάρχουν των αεροδρομίων αναχώρησης-στάσεων
            stopsStr+=getStopsString(outboundsList);
            leavingStopsTV.setVisibility(View.VISIBLE);
            leavingStopsTV.setText(stopsStr);
        }else if(outboundsList.size() -1 == 0)
            leavingStopsTV.setVisibility(View.INVISIBLE);

        //String originDepartDateStr = departFlight.getDepartureDate();
        //String originDepartTimeStr = originDepartDateStr.substring(originDepartDateStr.length()-5,originDepartDateStr.length());

        //String arrivalDepartDateStr = arrivalFlight.getDepartureDate();
        //String arrivalDepartTimeStr = arrivalDepartDateStr.substring(arrivalDepartDateStr.length()-5,arrivalDepartDateStr.length());


        if(inboundsList!=null){
            //Η πρώτη και τελευταία πτήση επιστροφής
            Flight returnDepartFlight = inboundsList.get(0);
            Flight returnArrivalFlight = inboundsList.get(inboundsList.size()-1);

            //Τα αεροδρόμια αναχώρησης-άφιξης της επιστροφής
            String returnOriginStr = returnDepartFlight.getOriginAirport().getValue();
            String returnDestinStr = returnArrivalFlight.getDestinationAirport().getValue();


            //Οι ώρες αναχώρησης-άφιξης Eπιστροφής
            Calendar returnDepartTime = returnDepartFlight.getDepartureDate();
            String returnDepartTimeStr = dateFormat.format(returnDepartTime.getTime());
            Calendar returnArrivalTime = returnArrivalFlight.getArrivalDate();
            String returnArrivalTimeStr = dateFormat.format(returnArrivalTime.getTime());

            //String returnDepartDateStr = returnFlight.getDepartureDate();
            //String returnDepartTimeStr = returnDepartDateStr.substring(returnDepartDateStr.length()-5,returnDepartDateStr.length());

            //String arrivalReturnDateStr = arrivalReturnFlight.getDepartureDate();
            //String arrivalReturnTimeStr = arrivalReturnDateStr.substring(arrivalReturnDateStr.length()-5,arrivalReturnDateStr.length());

            String returndiffTimeStr = getTimeDiffernceString(returnDepartTime,returnArrivalTime);

            //To text των λεπτομερειών της επιστροφής
            String returnStr =  "EP" +
                    " " + returnOriginStr + "→" + returnDestinStr +
                    " [" + returnDepartTimeStr + "-" + returnArrivalTimeStr+"] " + returndiffTimeStr;



            //Το text των στάσεων της επιστροφής
            String stopsReturnStr = convertView.getResources().getString(R.string.return_stops) + '(' + (inboundsList.size()-1) + ")";


            //Προσθέση αν υπάρχουν των αεροδρομίων αναχώρησης-στάσεων στην επιστροφή
            stopsReturnStr+=getStopsString(inboundsList);


            //Τα αντίστοιχα textViews παίρνουν τα text και εμφανίζονται αν υπάρχει επιστροφή
            return_airportLoc_TV.setVisibility(View.VISIBLE);
            return_travelTime_TV.setVisibility(View.VISIBLE);
            return_flightDuration_TV.setVisibility(View.VISIBLE);
            results_return_details_TV.setVisibility(View.VISIBLE);
            return_airportLoc_TV.setText(returnOriginStr + "->" + returnDestinStr);
            return_travelTime_TV.setText("[" + returnDepartTimeStr + "-" + returnArrivalTimeStr+"]");
            return_flightDuration_TV.setText(returndiffTimeStr);
            if(inboundsList.size()-1 != 0) {
                returnStopsTV.setVisibility(View.VISIBLE);
                returnStopsTV.setText(stopsReturnStr);
            }else if(inboundsList.size()-1 == 0)
                returnStopsTV.setVisibility(View.INVISIBLE);
        }else{
            //Αφού δεν υπάρχει επιστροφή να μην εμφανίζονται τα αντίστοιχα textViews
            return_airportLoc_TV.setVisibility(View.GONE);
            return_travelTime_TV.setVisibility(View.GONE);
            return_flightDuration_TV.setVisibility(View.GONE);
            results_return_details_TV.setVisibility(View.GONE);
            returnStopsTV.setVisibility(View.GONE);
        }


        float totalPrice = itinerariesList.get(position).getModel().getTotalPrice();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        String currencyType = sharedPreferences.getString(
                activity.getString(R.string.currency_key),
                activity.getString(R.string.currency_eur));

        if(currencyType.equals(activity.getString(R.string.currency_eur)))
            totalPriceTV.setText(decimalFormat.format(totalPrice) + " €");
        else if(currencyType.equals(activity.getString(R.string.currency_usd)))
            totalPriceTV.setText(decimalFormat.format(totalPrice) + " $");



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

    //Επιστρέφει string με τη διαφορά σε ώρες και λεπτά
    public static String getTimeDiffernceString(Calendar startDate, Calendar endDate){

        String text = getHoursDifference(startDate,endDate) + context.getResources().getString(R.string.hours_first_letter) +
                " " + getMinutesDifference(startDate,endDate) + context.getResources().getString(R.string.minutes_first_letter);

        return text;
    }

    //Επιστρέφει τη διαφορά σε ώρες
    public static long getHoursDifference(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toHours(Math.abs(end - start));
    }

    //Επιστρέφει τη διαφορά σε λεπτά (0-59)
    public static long getMinutesDifference(Calendar startDate, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startDate.getTimeInMillis();
        return (TimeUnit.MILLISECONDS.toMinutes(Math.abs(end - start))%60);
    }

    public static String getStopsString(List<Flight> flightsList){
        String stopsText="";
        if(flightsList.size()-1 != 0){
            stopsText+=':';
            String lastDestValue, newOriginValue;
            for (int i = 1; i < flightsList.size(); i++) {
                lastDestValue = flightsList.get(i-1).getDestinationAirport().getValue();
                newOriginValue = flightsList.get(i).getOriginAirport().getValue();
                if(lastDestValue.equals(newOriginValue))
                    stopsText+=   " "+ newOriginValue;
                else
                    stopsText+= " ["+ lastDestValue+"->"+ newOriginValue+"]";
                if(i<(flightsList.size()-1))
                    stopsText+= " -";
            }
        }
        return stopsText;
    }
}
