package com.mobiledev.uom.flyme.classes;

import android.net.Uri;
import android.os.AsyncTask;
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

/**
 * Created by Tolis on 20/12/2016.
 */

public class OriginLocationFinderTask extends AsyncTask<String, Void, String> {

    public OriginResponse delegate = null;

    private final String LOG_TAG = OriginLocationFinderTask.class.getSimpleName();

    private String getAirportDataFromJson(String airport) throws JSONException {
        String airpName;
        JSONArray termArray = new JSONArray(airport);
        JSONObject termObject = termArray.getJSONObject(0);
        airpName = termObject.getString("label");

        return airpName;
    }
    @Override
    protected String doInBackground(String... params) {
        String airportJson;
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        try{
            final String BASE_URL = "https://api.sandbox.amadeus.com/v1.2/airports/autocomplete?";
            final String API_KEY = "apikey";
            final String TERM = "term";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, BuildConfig.AMADEUS_KEY)
                    .appendQueryParameter(TERM, params[0])
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

            airportJson = buffer.toString();


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

        try{
            return getAirportDataFromJson(airportJson);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onPostExecute(String result){
        delegate.GiveMyOriginAirportName(result);
    }

}
