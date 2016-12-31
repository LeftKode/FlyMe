package com.mobiledev.uom.flyme;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobiledev.uom.flyme.classes.Flight;
import com.mobiledev.uom.flyme.classes.Itinerary;
import com.mobiledev.uom.flyme.classes.ItineraryAdapter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailsActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailsActivityFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #FlyMe";
    //TODO να βάλουμε το string για share εδώ
    private String shareString;

    private Itinerary itin;
    private TextView departAirportTv, departDateTimeTv, arrivalAirportTv, arrivalDateTimeTv, arrivalDurationTv, airlinesTv;
    private TextView retDepartAirportTv, retDepartDateTimeTv, retArrivalAirportTv, retArrivalDateTimeTv, retArrivalDurationTv, retAirlinesTv;
    private View rootView, flightItem;
    private  LayoutInflater inflater2;
    private DateFormat dateTimeFormat;

    public DetailsActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_details, container, false);

        departAirportTv = (TextView) rootView.findViewById(R.id.details_depart_airport);
        departDateTimeTv = (TextView) rootView.findViewById(R.id.details_depart_date_time);
        arrivalAirportTv = (TextView) rootView.findViewById(R.id.details_arrival_airport);
        arrivalDateTimeTv = (TextView) rootView.findViewById(R.id.details_arrival_date_time);
        arrivalDurationTv = (TextView) rootView.findViewById(R.id.details_arrival_duration);
        airlinesTv = (TextView) rootView.findViewById(R.id.details_airlines);

        retDepartAirportTv = (TextView) rootView.findViewById(R.id.details_ret_depart_airport);
        retDepartDateTimeTv = (TextView) rootView.findViewById(R.id.details_ret_date_time);
        retArrivalAirportTv = (TextView) rootView.findViewById(R.id.details_ret_arrival_airport);
        retArrivalDateTimeTv = (TextView) rootView.findViewById(R.id.details_ret_arrival_date_time);
        retArrivalDurationTv = (TextView) rootView.findViewById(R.id.details_ret_duration);
        retAirlinesTv = (TextView) rootView.findViewById(R.id.details_ret_airlines);

        Intent intent = getActivity().getIntent();
        //Bundle bundle = intent.getExtras();

        if (intent != null && intent.hasExtra("ItinObj")) {
            //Παίρνει το url που του στάλθηκε από κάποια activity
            //itin = (Itinerary) bundle.getSerializable("ItinObj");
            itin = (Itinerary) intent.getSerializableExtra("ItinObj");
            setDetailsTexts();
            Log.v("TEEE",itin.getOutboundFlightsList().get(0).getDestinationAirport().getValue());
        }




        return rootView;
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                shareString + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        inflater.inflate(R.menu.menu_details,menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share action provider = null");
        }
    }

    void setDetailsTexts(){
        List<Flight> outboundsList = itin.getOutboundFlightsList();
        List<Flight> inboundsList = itin.getInboundFlightsList();

        Flight departFirstFlight = outboundsList.get(0);
        Flight departLastFlight = outboundsList.get(outboundsList.size() - 1);

        departAirportTv.setText(departFirstFlight.getOriginAirport().getLabel());
        arrivalAirportTv.setText(departLastFlight.getDestinationAirport().getLabel());

        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm");
        Calendar departDateTime = departFirstFlight.getDepartureDate();
        String departDateTimeStr = dateTimeFormat.format(departDateTime.getTime());
        departDateTimeTv.setText(departDateTimeStr);
        Calendar arrivalDateTime = departLastFlight.getArrivalDate();
        String arrivalDateTimeStr = dateTimeFormat.format(arrivalDateTime.getTime());
        arrivalDateTimeTv.setText(arrivalDateTimeStr);
        arrivalDurationTv.setText(ItineraryAdapter.getTimeDiffernceString(departDateTime,arrivalDateTime));


        LinearLayout returnLayout = (LinearLayout) rootView.findViewById(R.id.details_return_layout);

        inflater2 = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        LinearLayout departFlightsLayout = (LinearLayout) rootView.findViewById(R.id.details_depart_flights_layout);
        LinearLayout returnFlightsLayout = (LinearLayout) rootView.findViewById(R.id.details_return_flights_layout);

        Flight flight;

        for(int i = 0; i < outboundsList.size(); i++) {
            flight = outboundsList.get(i);
            createFlightItem(flight,i);
            departFlightsLayout.addView(flightItem);
        }

        //TODO Να βάλω να ελέγχει αν υπάρχει επιστροφή
        if(inboundsList!=null){
            returnLayout.setVisibility(View.VISIBLE);
            Flight returnFirstFlight = inboundsList.get(0);
            Flight returnLastFlight = inboundsList.get(inboundsList.size() - 1);

            retDepartAirportTv.setText(returnFirstFlight.getOriginAirport().getLabel());
            retArrivalAirportTv.setText(returnLastFlight.getDestinationAirport().getLabel());

            Calendar retDepartDateTime = returnFirstFlight.getDepartureDate();
            String retDepartDateTimeStr = dateTimeFormat.format(retDepartDateTime.getTime());
            retDepartDateTimeTv.setText(retDepartDateTimeStr);
            Calendar retArrivalDateTime = returnLastFlight.getArrivalDate();
            String retArrivalDateTimeStr = dateTimeFormat.format(retArrivalDateTime.getTime());
            retArrivalDateTimeTv.setText(retArrivalDateTimeStr);
            retArrivalDurationTv.setText(ItineraryAdapter.getTimeDiffernceString(retDepartDateTime,retArrivalDateTime));

            for(int i = 0; i < inboundsList.size(); i++) {
                flight = inboundsList.get(i);
                createFlightItem(flight,i);
                returnFlightsLayout.addView(flightItem);
            }
        }
        else{
            returnLayout.setVisibility(View.GONE);
        }


            //((RadioButton)ll.findViewById(R.id.radioButton)).setText(Integer.toString(i));
            //lin.addView(ll);
            //lin.addView(ll);


    }

    void createFlightItem(Flight flight, int noID){
        flightItem = inflater2.inflate(R.layout.flight_item, null);
        ((TextView) flightItem.findViewById(R.id.flight_id_tv)).setText(Integer.toString(noID));
        ((TextView) flightItem.findViewById(R.id.flight_origin_airport_tv)).
                setText(flight.getOriginAirport().getLabel());

        Calendar departDate = flight.getDepartureDate();
        String departDateStr = dateTimeFormat.format(departDate.getTime());
        ((TextView) flightItem.findViewById(R.id.flight_origin_date_tv)).setText(departDateStr);

        Calendar returnDate = flight.getArrivalDate();
        String returnDateStr = dateTimeFormat.format(returnDate.getTime());
        ((TextView) flightItem.findViewById(R.id.flight_destin_date_tv)).setText(returnDateStr);

        ((TextView) flightItem.findViewById(R.id.flight_duration_tv)).
                setText(ItineraryAdapter.getTimeDiffernceString(departDate,returnDate));
        ((TextView) flightItem.findViewById(R.id.flight_destin_airport_tv)).
                setText(flight.getDestinationAirport().getLabel());
        ((TextView) flightItem.findViewById(R.id.flight_airline_tv)).
                setText(flight.getAirline().getName() + " - "+flight.getAirline().getCode());
    }

}
