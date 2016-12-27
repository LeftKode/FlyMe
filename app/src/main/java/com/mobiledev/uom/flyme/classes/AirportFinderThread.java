package com.mobiledev.uom.flyme.classes;

import android.net.Uri;
import android.util.Log;

import com.mobiledev.uom.flyme.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Lefteris on 27/12/2016.
 */

public class AirportFinderThread extends Thread {

    private static final String LOG_TAG = AirlineFinderThread.class.getSimpleName();

    String code;
    Map<String,Airport> airportsMap;

    public AirportFinderThread(Map<String, Airport> airportsMap, String code) {
        this.airportsMap = airportsMap;
        this.code = code;
    }

    @Override
    public void run() {
        String airportJsonString = getJsonWithAirportDetails();
        Airport airport;
        try {
            airport = getAirportDataFromJson(airportJsonString);
            airportsMap.put(code,airport);
            //return getAirlineDataFromJson(airlineJsonString);
        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }

    String getJsonWithAirportDetails(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String airportsJsonStr;
        List<Airport> airports = new ArrayList<>();

        try{
            final String BASE_URL = "http://api.sandbox.amadeus.com/v1.2/airports/autocomplete?";
            final String API_KEY = "apikey";
            final String Term = "term";
            //final String DESTINATION = "destination";
            // final String DEPART_DATE = "departure_date";
            //final String NO_OF_RESULTS = "number_of_results";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, BuildConfig.AMADEUS_KEY)
                    .appendQueryParameter(Term, code)
                    .build();

            URL url = new URL(builtUri.toString());

            Log.e(LOG_TAG,builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
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

            airportsJsonStr = buffer.toString();

        } catch (IOException e) {
            // If the code didn't successfully get the airport data, there's no point in attemping
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
                    Log.e("", "Error closing stream", e);
                }
            }
        }


        return airportsJsonStr;
    }

    Airport getAirportDataFromJson(String airportJsonStr) throws JSONException {


        final String A_Value = "value";
        final String A_Label = "label";
        String value = null;
        String label = null;

        JSONArray airportsRusults = new JSONArray(airportJsonStr);

        JSONObject airportObject;
        for(int i=0; i < airportsRusults.length(); i++){
            airportObject = airportsRusults.getJSONObject(i);
            value = airportObject.getString(A_Value);
            label = airportObject.getString(A_Label);
            if(value.equals(code))
                return (new Airport(value,label));

        }

        Log.e(LOG_TAG,value +" "+  code +" " +label);
        return null;
    }
}
