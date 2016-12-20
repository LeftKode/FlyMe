package com.mobiledev.uom.flyme;

import android.content.Context;
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
import android.widget.TextView;

import com.mobiledev.uom.flyme.classes.DestLocationFinderTask;
import com.mobiledev.uom.flyme.classes.DestinationResponse;
import com.mobiledev.uom.flyme.classes.OriginResponse;
import com.mobiledev.uom.flyme.classes.FlightModel;
import com.mobiledev.uom.flyme.classes.OriginLocationFinderTask;

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
import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment implements OriginResponse, DestinationResponse {

    private ListView listView;
    private String originAirportName;
    private String destinationAirportName;
    OriginLocationFinderTask originFinder = new OriginLocationFinderTask();
    DestLocationFinderTask destinationFinder = new DestLocationFinderTask();

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        originFinder.delegate = this;
        originFinder.execute("SKG");
        destinationFinder.delegate = this;
        destinationFinder.execute("ATH");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        listView = (ListView) rootView.findViewById(R.id.main_page_flight_list);

        return rootView;
    }

    @Override
    public void onStart(){
        super.onStart();
        new ShowFlightsTask().execute("SKG");
    }

    @Override
    public void GiveMyDestinationAirportName(String output) {
        destinationAirportName = output;
    }

    @Override
    public void GiveMyOriginAirportName(String output) {
        originAirportName = output;
    }


    public class ShowFlightsTask extends AsyncTask<String , Void, List<FlightModel>>{
        private final String LOG_TAG = ShowFlightsTask.class.getSimpleName();

        private List<FlightModel> getFlightDataFromJson(String flightJsonStr) throws JSONException, ParseException {

            final String F_CUR = "currency";
            final String F_RES = "results";
            final String F_ITINS = "itineraries";
            final String F_OUTBOUNDS = "outbound";
            final String F_FLIGHTS = "flights";
            final String F_DEPS = "departs_at";
            final String F_ARRS = "arrives_at";
            final String F_ORIGIN = "origin";
            final String F_DEST = "destination";
            final String F_AIRP = "airport";
            final String F_FARE = "fare";
            final String F_PRICE = "total_price";
            final String currency;


            JSONObject flightJson = new JSONObject(flightJsonStr);
            currency = flightJson.getString(F_CUR);
            JSONArray flightResult = flightJson.getJSONArray(F_RES);
            List<FlightModel> modelList = new ArrayList<FlightModel>();

            for(int i=0; i < flightResult.length(); i++){
                String departure;
                String arrival;
                String originLoc;
                String destinationLoc;
                String price;

                JSONObject newObject = flightResult.getJSONObject(i);
                JSONObject fare = newObject.getJSONObject(F_FARE);
                price = fare.getString(F_PRICE);
                JSONArray itinArray = newObject.getJSONArray(F_ITINS);
                for(int j=0; j<itinArray.length(); j++) {
                    FlightModel model = new FlightModel();
                    SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                    SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");

                    JSONObject itinObject = itinArray.getJSONObject(j);
                    JSONObject outboundObject = itinObject.getJSONObject(F_OUTBOUNDS);
                    JSONObject flightObject = outboundObject.getJSONArray(F_FLIGHTS).getJSONObject(0);

                    departure = flightObject.getString(F_DEPS);
                    Date myDepDate = dateFormat1.parse(departure);
                    dateFormat1.applyPattern("dd-MM-yyyy HH:MM");
                    String departDate = dateFormat1.format(myDepDate);
                    model.setDepartureDate(departDate);

                    arrival = flightObject.getString(F_ARRS);
                    Date myArrDate = dateFormat2.parse(arrival);
                    dateFormat2.applyPattern("dd-MM-yyyy HH:mm");
                    String arrivalDate = dateFormat2.format(myArrDate);
                    model.setArrivalDate(arrivalDate);


                    JSONObject originObject = flightObject.getJSONObject(F_ORIGIN);
                    originLoc = originObject.getString(F_AIRP);
                    model.setOriginLocation(originLoc);

                    JSONObject destinationObject = flightObject.getJSONObject(F_DEST);
                    destinationLoc = destinationObject.getString(F_AIRP);

                    model.setDestinationLocation(destinationLoc);
                    model.setCurrency(currency);
                    model.setPrice(price);

                    modelList.add(model);
                }

            }
            return modelList;
        }

        @Override
        protected List<FlightModel> doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String flightJsonString = null;

            try{
                final String BASE_URL = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?";
                final String API_KEY = "apikey";
                final String ORIGIN = "origin";
                final String DESTINATION = "destination";
                final String DEPART_DATE = "departure_date";
                final String CURRENCY = "currency";
                final String NO_OF_RESULTS = "number_of_results";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.AMADEUS_KEY)
                        .appendQueryParameter(ORIGIN, params[0])
                        .appendQueryParameter(DESTINATION, "ATH")
                        .appendQueryParameter(DEPART_DATE, "2016-12-20")
                        .appendQueryParameter(CURRENCY, "EUR")
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
                return getFlightDataFromJson(flightJsonString);
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
            FlightAdapter adapter = new FlightAdapter(getContext(), R.layout.fragment_main, result);
            listView.setAdapter(adapter);
        }
    }

    public class FlightAdapter extends ArrayAdapter{

        public List<FlightModel> flightModelList;
        private int resource;
        private LayoutInflater inflater;

        public FlightAdapter(Context context, int resource, List<FlightModel> objects){
            super(context, resource, objects);
            flightModelList = objects;
            this.resource = resource;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);
        }


        //καλείται τόσες φόρες όσα και τα αντικείμενα που έχουμε
        @Override
        public View getView(int position, View convertView, ViewGroup parent){


            if (convertView == null){
                convertView = inflater.inflate(R.layout.list_item_flight, null);
            }

            TextView originLoc = (TextView)convertView.findViewById(R.id.originLoc);
            TextView destinationLoc = (TextView)convertView.findViewById(R.id.destinationLoc);
            TextView departDate = (TextView)convertView.findViewById(R.id.departureDate);
            TextView arrivalDate = (TextView)convertView.findViewById(R.id.arrivalDate);
            TextView currency = (TextView)convertView.findViewById(R.id.price_textView);

            originLoc.setText(originAirportName);

            destinationLoc.setText(destinationAirportName);

            departDate.setText(":" +" "+flightModelList.get(position).getDepartureDate());
            arrivalDate.setText(":" +" "+flightModelList.get(position).getArrivalDate());
            currency.setText(flightModelList.get(position).getPrice()
                    + " " + flightModelList.get(position).getCurrency());


            return convertView;
        }
    }

}

