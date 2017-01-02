package com.mobiledev.uom.flyme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
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
import android.widget.TableRow;
import android.widget.TextView;

import com.mobiledev.uom.flyme.classes.DBHelper;
import com.mobiledev.uom.flyme.classes.Flight;
import com.mobiledev.uom.flyme.classes.Itinerary;
import com.mobiledev.uom.flyme.classes.ItineraryAdapter;

import java.text.DateFormat;
import java.text.DecimalFormat;
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
    private TextView departAirportTv, departDateTimeTv, arrivalAirportTv, arrivalDateTimeTv;
    private TextView retDepartAirportTv, retDepartDateTimeTv, retArrivalAirportTv, retArrivalDateTimeTv;
    private TextView adultsPriceTv, childrenPriceTv, infantsPriceTv,totalPriceTv;
    private View rootView, flightItem;
    private  LayoutInflater inflater2;
    private DateFormat dateTimeFormat;
    private int db_id;
    DBHelper myDBHelper;
    private String urlText;
    private int adultNo;
    private int childrenNo;
    private int infantNo;
    private char currencySymbol;

    public DetailsActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDBHelper = new DBHelper(getActivity());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String currencyType = sharedPreferences.getString(
                getActivity().getString(R.string.currency_key),
                getActivity().getString(R.string.currency_eur));

        if(currencyType.equals(getResources().getString(R.string.currency_eur)))
            currencySymbol = '€';
        else if(currencyType.equals(getResources().getString(R.string.currency_usd)))
            currencySymbol = '$';
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_details, container, false);

        departAirportTv = (TextView) rootView.findViewById(R.id.details_depart_airport);
        departDateTimeTv = (TextView) rootView.findViewById(R.id.details_depart_date_time);
        arrivalAirportTv = (TextView) rootView.findViewById(R.id.details_arrival_airport);
        arrivalDateTimeTv = (TextView) rootView.findViewById(R.id.details_arrival_date_time);

        retDepartAirportTv = (TextView) rootView.findViewById(R.id.details_ret_depart_airport);
        retDepartDateTimeTv = (TextView) rootView.findViewById(R.id.details_ret_date_time);
        retArrivalAirportTv = (TextView) rootView.findViewById(R.id.details_ret_arrival_airport);
        retArrivalDateTimeTv = (TextView) rootView.findViewById(R.id.details_ret_arrival_date_time);


        Intent intent = getActivity().getIntent();
        //Bundle bundle = intent.getExtras();

        if (intent != null && intent.hasExtra("ItinObj")) {
            //Παίρνει το url που του στάλθηκε από κάποια activity
            //itin = (Itinerary) bundle.getSerializable("ItinObj");
            itin = (Itinerary) intent.getSerializableExtra("ItinObj");
            setDetailsTexts();
            if (intent.hasExtra("db_id")){
                db_id = intent.getExtras().getInt("db_id");
                Cursor data = myDBHelper.getTableRow(db_id);
                data.moveToFirst();
                urlText = data.getString(data.getColumnIndexOrThrow("url"));
                adultNo = data.getInt(data.getColumnIndexOrThrow("adultsNumber"));
                childrenNo = data.getInt(data.getColumnIndexOrThrow("childrenNumber"));
                infantNo = data.getInt(data.getColumnIndexOrThrow("infantNumber"));



                setPricesTexts();
            }
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

        departAirportTv.setText(getResources().getString(R.string.from) + departFirstFlight.getOriginAirport().getLabel());
        arrivalAirportTv.setText(getResources().getString(R.string.to) +departLastFlight.getDestinationAirport().getLabel());

        dateTimeFormat = new SimpleDateFormat("HH:mm \n d MMM yyyy");
        Calendar departDateTime = departFirstFlight.getDepartureDate();
        String departDateTimeStr = dateTimeFormat.format(departDateTime.getTime());
        departDateTimeTv.setText(departDateTimeStr);
        Calendar arrivalDateTime = departLastFlight.getArrivalDate();
        String arrivalDateTimeStr = dateTimeFormat.format(arrivalDateTime.getTime());
        arrivalDateTimeTv.setText(arrivalDateTimeStr);


        LinearLayout returnLayout = (LinearLayout) rootView.findViewById(R.id.details_return_layout);

        inflater2 = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        LinearLayout departFlightsLayout = (LinearLayout) rootView.findViewById(R.id.details_depart_flights_layout);
        LinearLayout returnFlightsLayout = (LinearLayout) rootView.findViewById(R.id.details_return_flights_layout);


        createFlightsItems(outboundsList,departFlightsLayout);



        //TODO Να βάλω να ελέγχει αν υπάρχει επιστροφή
        if(inboundsList!=null){
            returnLayout.setVisibility(View.VISIBLE);
            Flight returnFirstFlight = inboundsList.get(0);
            Flight returnLastFlight = inboundsList.get(inboundsList.size() - 1);

            retDepartAirportTv.setText(getResources().getString(R.string.from) + returnFirstFlight.getOriginAirport().getLabel());
            retArrivalAirportTv.setText(getResources().getString(R.string.to) + returnLastFlight.getDestinationAirport().getLabel());

            Calendar retDepartDateTime = returnFirstFlight.getDepartureDate();
            String retDepartDateTimeStr = dateTimeFormat.format(retDepartDateTime.getTime());
            retDepartDateTimeTv.setText(retDepartDateTimeStr);
            Calendar retArrivalDateTime = returnLastFlight.getArrivalDate();
            String retArrivalDateTimeStr = dateTimeFormat.format(retArrivalDateTime.getTime());
            retArrivalDateTimeTv.setText(retArrivalDateTimeStr);

            createFlightsItems(inboundsList,returnFlightsLayout);
        }
        else{
            returnLayout.setVisibility(View.GONE);
        }


            //((RadioButton)ll.findViewById(R.id.radioButton)).setText(Integer.toString(i));
            //lin.addView(ll);
            //lin.addView(ll);


    }

    void setPricesTexts(){
        float price,productPrice;
        String productPriceStr;
        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");


        TableRow adultsRow = (TableRow) rootView.findViewById(R.id.details_row_adults);
        TableRow childrenRow = (TableRow) rootView.findViewById(R.id.details_row_children);
        TableRow infantsRow = (TableRow) rootView.findViewById(R.id.details_row_infants);

        TextView adultsOperationTv = (TextView) rootView.findViewById(R.id.details_adults_operation_tv);
        TextView childrenOperationTv = (TextView) rootView.findViewById(R.id.details_children_operation_tv);
        TextView infantsOperationTv = (TextView) rootView.findViewById(R.id.details_infants_operation_tv);




        if(adultNo>0){
            adultsPriceTv = (TextView) rootView.findViewById(R.id.details_adult_price);
            price = itin.getModel().getPricePerAdult();
            productPrice = price * adultNo;
            productPriceStr = decimalFormat.format(productPrice);
            adultsOperationTv.setText(decimalFormat.format(itin.getModel().getPricePerAdult())+ "  *  "+adultNo + "  =");
            adultsPriceTv.setText(productPriceStr + " " + currencySymbol);
        } else{
            adultsRow.setVisibility(View.GONE);
        }

        if(childrenNo>0){
            childrenPriceTv = (TextView) rootView.findViewById(R.id.details_child_price);
            price = itin.getModel().getPricePerChild();
            productPrice = price * childrenNo;
            productPriceStr = decimalFormat.format(productPrice);
            childrenOperationTv.setText(decimalFormat.format(itin.getModel().getPricePerChild()) +"  *  "+ childrenNo +"  =");
            childrenPriceTv.setText(productPriceStr + " " + currencySymbol);
        } else{
            childrenRow.setVisibility(View.GONE);
        }

        if(infantNo>0){
            infantsPriceTv = (TextView) rootView.findViewById(R.id.details_infant_price);
            price = itin.getModel().getPricePerInfant();
            productPrice = price * infantNo;
            productPriceStr = decimalFormat.format(productPrice);
            infantsOperationTv.setText(decimalFormat.format(itin.getModel().getPricePerInfant()) + "  *  " + infantNo + "  =");
            infantsPriceTv.setText(productPriceStr + " " + currencySymbol);
        } else{
            infantsRow.setVisibility(View.GONE);
        }

        totalPriceTv = (TextView) rootView.findViewById(R.id.details_total_price);
        price = itin.getModel().getTotalPrice();
        productPriceStr = decimalFormat.format(price);
        totalPriceTv.setText(productPriceStr + " " + currencySymbol);
    }

    void createFlightsItems (List<Flight> flghtsList, LinearLayout lin) {
        Flight flight, nextFlight;
        DateFormat dateFormat  = new SimpleDateFormat("d MMM yyyy - HH:mm");
        String text;
        if(flghtsList.size()>0) nextFlight = flghtsList.get(0);
        else nextFlight=null;

        for(int i = 0; i < flghtsList.size(); i++) {
            flight = nextFlight;
            if(i+1<flghtsList.size()) nextFlight= flghtsList.get(i+1);
            else nextFlight=null;


            flightItem = inflater2.inflate(R.layout.flight_item, null);

            ((TextView) flightItem.findViewById(R.id.flight_id_tv)).setText(getResources().getString(R.string.flight_no) + Integer.toString(i + 1));
            ((TextView) flightItem.findViewById(R.id.flight_origin_airport_tv)).
                    setText(flight.getOriginAirport().getLabel());
            Calendar departDate = flight.getDepartureDate();
            String departDateStr = dateFormat.format(departDate.getTime());
            ((TextView) flightItem.findViewById(R.id.flight_origin_date_tv)).setText(departDateStr);

            Calendar arrivalDate = flight.getArrivalDate();
            String arrivalDateStr = dateFormat.format(arrivalDate.getTime());
            ((TextView) flightItem.findViewById(R.id.flight_destin_date_tv)).setText(arrivalDateStr);

            ((TextView) flightItem.findViewById(R.id.flight_duration_tv)).
                    setText(ItineraryAdapter.getTimeDiffernceString(departDate,arrivalDate));
            ((TextView) flightItem.findViewById(R.id.flight_destin_airport_tv)).
                    setText(flight.getDestinationAirport().getLabel());
            ((TextView) flightItem.findViewById(R.id.flight_airline_tv)).
                    setText(flight.getAirline().getName() + " - "+flight.getAirline().getCode());

            if(nextFlight!=null){
                String arrValue = flight.getDestinationAirport().getValue();
                String nextOrigValue = nextFlight.getOriginAirport().getValue();
                text = getResources().getString(R.string.stop) + " " + (i+1) + ": " + arrValue;
                if(!arrValue.equals(nextOrigValue))
                    text+=" --> " + nextOrigValue;
                ((TextView) flightItem.findViewById(R.id.flight_stop_tv)).setText(text);
                ((TextView) flightItem.findViewById(R.id.flight_stop_duration_tv)).
                        setText(getResources().getString(R.string.duration) + ItineraryAdapter.getTimeDiffernceString(arrivalDate,nextFlight.getDepartureDate()));

            }else {
                ((TextView) flightItem.findViewById(R.id.flight_stop_tv)).
                        setText(getResources().getString(R.string.total_duration) +
                                ItineraryAdapter.getTimeDiffernceString(flghtsList.get(0).getDepartureDate(),arrivalDate));
                ((TextView) flightItem.findViewById(R.id.flight_stop_duration_tv)).setVisibility(View.GONE);
            }


            //createFlightItem(flight,nextFlight,i);
            lin.addView(flightItem);


        }


    //(Flight flight, Flight flight2, int noID){


    }

}
