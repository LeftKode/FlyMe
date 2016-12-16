package com.mobiledev.uom.flyme;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import java.util.Calendar;




/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    Calendar c = Calendar.getInstance();
    int date = c.get(Calendar.DATE);
    ArrayAdapter<String> flightAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        flightAdapter =
                new ArrayAdapter<String>(
                        getActivity(),
                        R.layout.list_item_flight,
                        R.id.list_item_flight_textview,
                        new ArrayList<String>());

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.main_page_flight_list);
        listView.setAdapter(flightAdapter);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        new ShowFlightsTask().execute("SKG");
    }

    public class ShowFlightsTask extends AsyncTask<String , Void, String[]>{
        private final String LOG_TAG = ShowFlightsTask.class.getSimpleName();

        private String[] getFlightDataFromJson(String flightJsonStr, int numRes) throws JSONException{

            final String F_RES = "results";
            final String F_ITINS = "itineraries";
            final String F_OUTBOUNDS = "outbound";
            final String F_FLIGHTS = "flights";
            final String F_DEPS = "departs_at";
            final String F_ARRS = "arrives_at";
            final String F_ORIGIN = "origin";
            final String F_DEST = "destination";
            final String F_AIRP = "airport";

            JSONObject flightJson = new JSONObject(flightJsonStr);
            JSONArray flightResult = flightJson.getJSONArray(F_RES);

            String resultStrs[] = new String[numRes];

            for(int i=0; i < flightResult.length(); i++){
                String departure;
                String arrival;
                String originLoc;
                String destinationLoc;

                JSONObject newObject = flightResult.getJSONObject(0);

                JSONArray itinArray = newObject.getJSONArray(F_ITINS);
                for(int j=0; j<itinArray.length(); j++) {
                    JSONObject itinObject = itinArray.getJSONObject(0);
                    JSONObject outboundObject = itinObject.getJSONObject(F_OUTBOUNDS);
                    JSONObject flightObject = outboundObject.getJSONArray(F_FLIGHTS).getJSONObject(0);
                    departure = flightObject.getString(F_DEPS);
                    arrival = flightObject.getString(F_ARRS);
                    JSONObject originObject = flightObject.getJSONObject(F_ORIGIN);
                    originLoc = originObject.getString(F_AIRP);
                    JSONObject destinationObject = flightObject.getJSONObject(F_DEST);
                    destinationLoc = destinationObject.getString(F_AIRP);

                    resultStrs[i] = originLoc + " " + departure + '\n' + destinationLoc + " " + arrival;
                }
            }

            return resultStrs;

        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String flightJsonString;
            int numRes = 5;

            try{
                final String BASE_URL = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?";
                final String API_KEY = "apikey";
                final String ORIGIN = "origin";
                final String DESTINATION = "destination";
                final String DEPART_DATE = "departure_date";
                final String NO_OF_RESULTS = "number_of_results";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.AMADEUS_KEY)
                        .appendQueryParameter(ORIGIN, params[0])
                        .appendQueryParameter(DESTINATION, "ATH")
                        .appendQueryParameter(DEPART_DATE, "2016-12-20")
                        .appendQueryParameter(NO_OF_RESULTS, "5")
                        .build();

                URL url = new URL(builtUri.toString());

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

                flightJsonString = buffer.toString();

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
                return getFlightDataFromJson(flightJsonString,numRes);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            // This will only happen if there was an error getting or parsing the forecast.
            return null;

            }
        @Override
        protected void onPostExecute(String[] result){
            if (result != null){
                flightAdapter.clear();
                for (String flightStr : result){
                    if(flightStr == null){
                        break;
                    }
                    flightAdapter.add(flightStr);
                }
            }
        }
    }
}

