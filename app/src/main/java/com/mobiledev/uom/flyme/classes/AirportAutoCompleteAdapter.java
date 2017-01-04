package com.mobiledev.uom.flyme.classes;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiledev.uom.flyme.BuildConfig;
import com.mobiledev.uom.flyme.R;

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


public class AirportAutoCompleteAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_RESULTS = 5; //Ο μέγιστος αριθμός προτάσεων αεροδρομίων που θα εμφανίζει στο autocomplete textView
    private Context mContext;
    private List<Airport> resultList = new ArrayList<>();
    private SingleToast toast;



    public void setToast(SingleToast toast) {
        this.toast = toast;
    }

    public List<Airport> getResultList() {
        return resultList;
    }

    public AirportAutoCompleteAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return resultList.size();
    }

    @Override
    public Airport getItem(int index) {
        return resultList.get(index);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.airport_dropdown_item, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.airportNameText)).setText(getItem(position).getLabel());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            //Τρέχει στο background και στέλενει ένα url για να πάρει το JSON με προτάσεις αεροδρομίων
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    List<Airport> airports = findAirports(mContext, constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = airports;
                    filterResults.count = airports.size();

                }
                return filterResults;
            }

            @Override //Ενημερώνει τον χρήστη αν δεν υπάρχει ίντερνετ αλλιώς γεμίζει την resultsList με τα προτεινόμενα αεροδρόμια
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if(!isOnline()){
                    toast.show(mContext,"No Internet Connection", Toast.LENGTH_SHORT);
                }
                else if (results != null && results.count > 0) {
                    resultList = (List<Airport>) results.values;
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }};


        return filter;
    }

    /**
     * Returns a search result for the given book title.
     */
    private List<Airport> findAirports(Context context, String airportTitle) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String airportsJsonString;
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
                    .appendQueryParameter(Term, airportTitle)
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

            airportsJsonString = buffer.toString();

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


        final String A_Value = "value";
        final String A_Label = "label";

        try {
            JSONArray airportsRusults = new JSONArray(airportsJsonString);
            String value, label;
            for(int i=0; i < airportsRusults.length(); i++){
                JSONObject airportObject = airportsRusults.getJSONObject(i);
                value = airportObject.getString(A_Value);
                label = airportObject.getString(A_Label);
                Airport airport = new Airport(value,label);
                airports.add(airport);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return airports;
    }

    //Βλέπει αν είναι συνδεδεμένος στο ίντερνετ ο χρήστης
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}

