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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;




/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    Calendar c = Calendar.getInstance();
    int date = c.get(Calendar.DATE);
    ArrayAdapter<String> flightAdapter;

    public void ThisIsANewClass(){
        String deleteMe;
    }
    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public class ShowFlightsTask extends AsyncTask<String , Void, String[]>{
        private final String LOG_TAG = ShowFlightsTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String flightJsonString = null;

            try{
                final String BASE_URL = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?";
                final String API_KEY = "apikey";
                final String ORIGIN = "&origin";
                final String DESTINATION = "&destination";
                final String DEPART_DATE = "&departure_date";
                final String NO_OF_RESULTS = "&number_of_results";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.AMADEUS_KEY)
                        .appendQueryParameter(ORIGIN, "SKG")
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
            // This will only happen if there was an error getting or parsing the forecast.
            return null;

            }
    }
}

