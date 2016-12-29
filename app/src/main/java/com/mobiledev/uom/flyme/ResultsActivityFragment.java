package com.mobiledev.uom.flyme;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobiledev.uom.flyme.classes.Airline;
import com.mobiledev.uom.flyme.classes.AirlineFinderThread;
import com.mobiledev.uom.flyme.classes.Airport;
import com.mobiledev.uom.flyme.classes.AirportFinderThread;
import com.mobiledev.uom.flyme.classes.DBHelper;
import com.mobiledev.uom.flyme.classes.Flight;
import com.mobiledev.uom.flyme.classes.FlightModel;
import com.mobiledev.uom.flyme.classes.Itinerary;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * A placeholder fragment containing a simple view.
 */
public class ResultsActivityFragment extends Fragment {

    private int db_id;
    DBHelper myDBHelper;
    private String urlText;
    private int adultNo;
    private int childrenNo;
    private int infantNo;
    private int id;

    List<String> airportsCodesList = new ArrayList<>();     //Λίστα με τα αεροδρόμια που βρέθηκαν
    Map<String, Airport> airportsMap = new ConcurrentHashMap<>();
    Map<String, Airline> airlinesMap = new HashMap<>();

    View rootView;

    public ResultsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        myDBHelper = new DBHelper(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //H SearchActivity καλείται μέσω ενός intent που περιέχει ένα string που είναι το url
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);
        if (intent != null && intent.hasExtra("db_id")) {
            //Παίρνει το url που του στάλθηκε από κάποια activity
            db_id = intent.getExtras().getInt("db_id");
            Cursor data = myDBHelper.getTableRow(db_id);
            data.moveToFirst();
            urlText = data.getString(data.getColumnIndexOrThrow("url"));
            adultNo = data.getInt(data.getColumnIndexOrThrow("adultsNumber"));
            childrenNo = data.getInt(data.getColumnIndexOrThrow("childrenNumber"));
            infantNo = data.getInt(data.getColumnIndexOrThrow("infantNumber"));

            Log.e("Test",Integer.toString(adultNo));
            Log.e("Test",Integer.toString(childrenNo));
            Log.e("Test",Integer.toString(infantNo));
            Log.e("Test", urlText);
            new ShowFlightsTask().execute(urlText);
        }

       /* if (intent != null && intent.hasExtra("databaseUrl")) {
            //Παίρνει το url που του στάλθηκε από κάποια activity
            databaseUrlText = intent.getStringExtra("databaseUrl");
            Log.v("TestUrl",databaseUrlText);
        }*/

        //TODO Να εμφανίζει ένα textView αν μας έρθει από τον server ότι δεν υπάρχει απάντηση

        //TODO Οι ημερομηνίες μπορεί να είναι λάθος αν στο κινητό έχει ψεύτικη ημερομηνία.
        //Να του εμφανίζεται μήνυμα "Κάτι πήγε στραβά! Μήπως έχετε λάνθασμένη ημερομηνία?"
        this.rootView = rootView;

        return rootView;
    }

    public class ShowFlightsTask extends AsyncTask<String , Void, List<FlightModel>> {
        private final String LOG_TAG = ResultsActivityFragment.ShowFlightsTask.class.getSimpleName();
        private String airlinesCodes = "";
        private boolean returnDateExists;
        ExecutorService executor = Executors.newFixedThreadPool(4);

        private List<FlightModel> getFlightDataFromJson(String urlStr, String flightJsonStr) throws JSONException, ParseException {

            final String F_CUR = "currency";
            final String F_RES = "results";
            final String F_ITINS = "itineraries";
            final String F_OUTBOUNDS = "outbound";
            final String F_FLIGHTS = "flights";
            final String F_FARE = "fare";
            final String F_PRICE = "total_price";
            final String F_PRICE_PER_ADULT = "price_per_adult";
            final String F_PRICE_PER_INFANT = "price_per_infant";
            final String F_TOTAL_FARE = "total_fare";
            final String F_TAX = "tax";
            final String F_INBOUNDS = "inbound";
            final String currency;


            JSONObject flightJson = new JSONObject(flightJsonStr);
            currency = flightJson.getString(F_CUR);
            JSONArray flightResult = flightJson.getJSONArray(F_RES);
            List<FlightModel> modelList = new ArrayList<FlightModel>();


            /*String departure;
            String arrival;*/
            String originLoc;
            String destinationLoc;

            String priceStr;
            float price, totalFarePerAdult, totalFarePerChild, totalFarePerInfant;
            JSONObject priceAdultObj, priceInfantObj;
            JSONObject newObject;
            JSONObject fare;
            JSONArray itinArray;
            JSONObject inbound;
            JSONObject itinObject;
            JSONObject outboundObject;
            JSONArray outboundFlightArray;
            JSONObject inboundObject;
            JSONArray inboundFlightArray;
            JSONArray flightArray;
            JSONObject flightObject;
            FlightModel model;
            Flight flight;
            List<Flight> outboundFlightList;
            List<Flight> inboundFlightList;




            returnDateExists = urlText.contains("return_date");
            boolean zeroAdults = urlText.contains("adults=0");
            boolean infantsExist = urlText.contains("infants");

            for(int i=0; i < flightResult.length(); i++){


                newObject = flightResult.getJSONObject(i);
                fare = newObject.getJSONObject(F_FARE);
                priceStr = fare.getString(F_PRICE);
                price = Float.valueOf(priceStr);

                if(!zeroAdults){
                    priceAdultObj = fare.getJSONObject(F_PRICE_PER_ADULT);
                    priceStr = priceAdultObj.getString(F_TOTAL_FARE);
                    totalFarePerAdult = Float.valueOf(priceStr);

                }
                if(infantsExist){
                    priceInfantObj = fare.getJSONObject(F_PRICE_PER_INFANT);
                    priceStr = priceInfantObj.getString(F_TOTAL_FARE);
                    totalFarePerInfant = Float.valueOf(priceStr);
                }


                //TODO Να βάλω να παίρνει την τιμή του καθενός επιβάτη
                itinArray = newObject.getJSONArray(F_ITINS);
                model = new FlightModel();
                model.setItineraries(new ArrayList<Itinerary>());
                for(int j=0; j<itinArray.length(); j++) {
                    itinObject = itinArray.getJSONObject(j);
                    /*outboundObject = itinObject.getJSONObject(F_OUTBOUNDS);


                    flightArray = outboundObject.getJSONArray(F_FLIGHTS);
                    for(int k=0; k<flightArray.length(); k++) {
                        flightObject = flightArray.getJSONObject(k);

                        model = new FlightModel();

                        departure = flightObject.getString(F_DEPS);
                        arrival = flightObject.getString(F_ARRS);

                        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                        myDepDate = dateFormat.parse(departure);
                        myArrDate = dateFormat.parse(arrival);

                        dateFormat.applyPattern("dd-MM-yyyy HH:mm");
                        departDate = dateFormat.format(myDepDate);
                        model.setDepartureDate(departDate);
                        arrivalDate = dateFormat.format(myArrDate);
                        model.setArrivalDate(arrivalDate);

                        originObject = flightObject.getJSONObject(F_ORIGIN);
                        originLoc = originObject.getString(F_AIRP);
                        model.setOriginLocation(originLoc);

                        destinationObject = flightObject.getJSONObject(F_DEST);
                        destinationLoc = destinationObject.getString(F_AIRP);
                        model.setDestinationLocation(destinationLoc);

                        airlineCode = flightObject.getString(F_AIRL);
                        model.setAirline(airlineCode);

                        model.setCurrency(currency);
                        model.setTotalPrice(price);

                        modelList.add(model);
                    }

                    if(returnDateExists) {
                        inbound = itinObject.getJSONObject("inbound");
                    }*/


                    outboundObject = itinObject.getJSONObject(F_OUTBOUNDS);
                    outboundFlightArray = outboundObject.getJSONArray(F_FLIGHTS);

                    outboundFlightList = new ArrayList<>();
                    for(int l=0; l<outboundFlightArray.length(); l++){
                        flightObject = outboundFlightArray.getJSONObject(l);
                        flight = createFlight(flightObject);
                        outboundFlightList.add(flight);

                        /*dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                        departure = flightObject.getString(F_DEPS);
                        arrival = flightObject.getString(F_ARRS);
                        myDepDate = dateFormat.parse(departure);
                        myArrDate = dateFormat.parse(arrival);

                        dateFormat.applyPattern("dd-MM-yyyy HH:mm");
                        departDate = dateFormat.format(myDepDate);
                        flight.setDepartureDate(departDate);
                        arrivalDate = dateFormat.format(myArrDate);
                        flight.setArrivalDate(arrivalDate);

                        originObject = flightObject.getJSONObject(F_ORIGIN);
                        originLoc = originObject.getString(F_AIRP);
                        //TODO Να μετατρέπω τον κωδικό του αεροδρομίου αναχώρησης

                        flight.setOriginLocation(originLoc);

                        destinationObject = flightObject.getJSONObject(F_DEST);
                        destinationLoc = destinationObject.getString(F_AIRP);
                        //TODO Να μετατρέπω τον κωδικό του αεροδρομίου προορισμού
                        flight.setDestinationLocation(destinationLoc);

                        airlineCode = flightObject.getString(F_AIRL);
                        //TODO Να μετατρέπω τον κωδικό της αεροπορικής εταιρείας
                        *//*if(!airlinesMap.containsKey(airlineCode)){
                            airlinesMap.put(airlineCode,null);
                        }*//*
                        airlinesCodes+=airlineCode+",";
                            //executor.execute(new AirlineFinderThread(airlinesMap,airlineCode));

                        //flight.setAirline(airlineCode);
                        //outboundFlightList.add(flight);*/
                    }
                    model.getItineraries().add(new Itinerary(outboundFlightList));

                    if(returnDateExists){
                        inboundObject = itinObject.getJSONObject(F_INBOUNDS);
                        inboundFlightArray = inboundObject.getJSONArray(F_FLIGHTS);

                        inboundFlightList = new ArrayList<>();
                        for(int l=0; l<inboundFlightArray.length(); l++){
                            flightObject = inboundFlightArray.getJSONObject(l);
                            flight = createFlight(flightObject);
                            inboundFlightList.add(flight);
                            /*flightObject = inboundFlightArray.getJSONObject(l);
                            flight = new Flight();

                            dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                            departure = flightObject.getString(F_DEPS);
                            arrival = flightObject.getString(F_ARRS);
                            myDepDate = dateFormat.parse(departure);
                            myArrDate = dateFormat.parse(arrival);

                            dateFormat.applyPattern("dd-MM-yyyy HH:mm");
                            departDate = dateFormat.format(myDepDate);
                            flight.setDepartureDate(departDate);
                            arrivalDate = dateFormat.format(myArrDate);
                            flight.setArrivalDate(arrivalDate);

                            originObject = flightObject.getJSONObject(F_ORIGIN);
                            originLoc = originObject.getString(F_AIRP);
                            //TODO Να μετατρέπω τον κωδικό του αεροδρομίου αναχώρησης
                            flight.setOriginLocation(originLoc);

                            destinationObject = flightObject.getJSONObject(F_DEST);
                            destinationLoc = destinationObject.getString(F_AIRP);
                            //TODO Να μετατρέπω τον κωδικό του αεροδρομίου προορισμού
                            flight.setDestinationLocation(destinationLoc);

                            airlineCode = flightObject.getString(F_AIRL);
                            //TODO Να μετατρέπω τον κωδικό της αεροπορικής εταιρείας
                            flight.setAirline(airlineCode);
                            inboundFlightList.add(flight);*/
                        }
                        model.getItineraries().get(i).setInboundFlightsList(inboundFlightList);
                    }



                    /*List<FlightModel.inboundFlights> inboundFlightsList = new ArrayList<>();
                    for(int l=0; l<inboundFlightArray; l++){
                        JSONObject flightObject = inboundFlightArray.getJSONObject(l);
                        FlightModel.inboundFlights inboundFlight = new FlightModel.inboundFlights();
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

                        departure = flightObject.getString(F_DEPS);
                        Date myDepDate = dateFormat1.parse(departure);
                        dateFormat1.applyPattern("dd-MM-yyyy HH:mm");
                        String departDate = dateFormat1.format(myDepDate);
                        inboundFlight.setDepartureDate(departDate);

                        arrival = flightObject.getString(F_ARRS);
                        Date myArrDate = dateFormat2.parse(arrival);
                        dateFormat2.applyPattern("dd-MM-yyyy HH:mm");
                        String arrivalDate = dateFormat2.format(myArrDate);
                        inboundFlight.setArrivalDate(arrivalDate);

                        JSONObject originObject = flightObject.getJSONObject(F_ORIGIN);
                        originLoc = originObject.getString(F_AIRP);
                        inboundFlight.setOriginLocation(originLoc);

                        JSONObject destinationObject = flightObject.getJSONObject(F_DEST);
                        destinationLoc = destinationObject.getString(F_AIRP);
                        inboundFlight.setDestinationLocation(destinationLoc);

                        airlineCode = flightObject.getString(F_AIRL);
                        inboundFlight.setAirline(airlineCode);
                        inboundFlightList.add(inboundFlight);
                    }*/




                }
                model.setCurrency(currency);
                model.setTotalPrice(price);

                modelList.add(model);

            }
            executor.execute(new AirlineFinderThread(airlinesMap,airlinesCodes));

            //Τερματίζονται τα νήματα με τα αιτήματα από τα api
            try {
                executor.shutdown();
                executor.awaitTermination(20, TimeUnit.SECONDS);
            }
            catch (InterruptedException e) {
                System.err.println("tasks interrupted");
            }
            finally {
                if (!executor.isTerminated()) {
                    System.err.println("cancel non-finished tasks");
                }
                executor.shutdownNow();
            }

            addStuffFromAnswersInFlights(modelList);

            return modelList;
        }

        void addStuffFromAnswersInFlights(List<FlightModel> modelList){
            for (FlightModel model: modelList) {
                for (Itinerary itin: model.getItineraries()) {
                    for(Flight flight: itin.getOutboundFlightsList()){
                        flight.setOriginAirport(airportsMap.get(flight.getOriginAirport().getValue()));
                        flight.setDestinationAirport(airportsMap.get(flight.getOriginAirport().getValue()));
                        flight.setAirline(airlinesMap.get(flight.getAirline().getCode()));
                    }

                    if(returnDateExists){
                        for(Flight flight: itin.getInboundFlightsList()){
                            flight.setOriginAirport(airportsMap.get(flight.getOriginAirport().getValue()));
                            flight.setDestinationAirport(airportsMap.get(flight.getOriginAirport().getValue()));
                            flight.setAirline(airlinesMap.get(flight.getAirline().getCode()));
                        }
                    }

                }
            }
        }

        Flight createFlight(JSONObject flightObject) throws ParseException, JSONException {

            final String F_DEPS = "departs_at";
            final String F_ARRS = "arrives_at";
            final String F_ORIGIN = "origin";
            final String F_DEST = "destination";
            final String F_AIRP = "airport";
            final String F_AIRL = "marketing_airline";

            Airport originValueOnly,destinValueOnly;

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String departure = flightObject.getString(F_DEPS);
            String arrival = flightObject.getString(F_ARRS);
            Date myDepDate = dateFormat.parse(departure);
            Date myArrDate = dateFormat.parse(arrival);

            dateFormat.applyPattern("dd-MM-yyyy HH:mm");
            String departDate = dateFormat.format(myDepDate);
            String arrivalDate = dateFormat.format(myArrDate);

            JSONObject originObject = flightObject.getJSONObject(F_ORIGIN);
            String originValue = originObject.getString(F_AIRP);
            originValueOnly = new Airport(originValue,null);

            if(!airportsCodesList.contains(originValue)){
                airportsCodesList.add(originValue);
                executor.execute(new AirportFinderThread(airportsMap,originValue));
            }


            JSONObject destinationObject = flightObject.getJSONObject(F_DEST);
            String destinationValue = destinationObject.getString(F_AIRP);
            destinValueOnly = new Airport(originValue,null);
            //TODO Να μετατρέπω τον κωδικό του αεροδρομίου προορισμού
            if(!airportsCodesList.contains(destinationValue)){
                airportsCodesList.add(destinationValue);
                executor.execute(new AirportFinderThread(airportsMap,destinationValue));
            }

            String airlineCode = flightObject.getString(F_AIRL);
            Airline airlineCodeOnly = new Airline(airlineCode,null);


            if(!airlinesCodes.contains(airlineCode)){
                airlinesCodes+=airlineCode+",";
            }


            return new Flight(airlineCodeOnly,departDate,arrivalDate,originValueOnly,destinValueOnly);
        }

        @Override
        protected List<FlightModel> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String flightJsonString = null;
            try{

                URL url = new URL(params[0]);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                flightJsonString = buffer.toString();
                if(flightJsonString.substring(0,15).contains("status")){
                    return null;
                }

                Log.v(LOG_TAG, "Forecast string: " + flightJsonString);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getFlightDataFromJson(params[0],flightJsonString);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;

        }

        @Override
        protected void onPostExecute(List<FlightModel> result){

            TextView textView = (TextView) rootView.findViewById(R.id.resultsInfoTextView);
            if(result == null){
                textView.setText("Δεν υπήρχαν διαθέσιμες πτήσεις!");
            }
            else{

            }
            //MainActivityFragment.FlightAdapter adapter = new MainActivityFragment.FlightAdapter(getContext(), R.layout.fragment_main, result);
            //listView.setAdapter(adapter);

        }
    }


}
