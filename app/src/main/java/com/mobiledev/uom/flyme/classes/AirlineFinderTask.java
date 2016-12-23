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
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 21/12/2016.
 */

public class AirlineFinderTask extends AsyncTask<String, Void, Map<String,String>> {

    public AirlineNameResponse delegate = null;

    private Map<String, String> responseMap = new HashMap<String, String>();
    private final String LOG_TAG = AirlineFinderTask.class.getSimpleName();

    private Map<String, String> getAirlineDataFromJson (String airlineJsonString) throws JSONException {

        JSONObject object = new JSONObject(airlineJsonString);
        JSONArray responseArray = object.getJSONArray("response");
        for (int i=0; i < responseArray.length(); i++){
            JSONObject responseObject = responseArray.getJSONObject(i);
            String code = responseObject.getString("code");
            String name = responseObject.getString("name");
            responseMap.put(code,name);
        }

        return responseMap;
    }


    @Override
    protected Map<String, String> doInBackground(String... params){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String airlineJsonString = null;

        try{
            final String BASE_URL = "https://iatacodes.org/api/v6/airlines?";
            final String API_KEY = "api_key";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, BuildConfig.IATA_KEY)
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

            airlineJsonString = buffer.toString();

            Log.v(LOG_TAG, "Forecast string: " + airlineJsonString);
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
            return getAirlineDataFromJson(airlineJsonString);
        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        // This will only happen if there was an error getting or parsing the forecast.
        return null;
    }

    @Override
    protected void onPostExecute (Map<String,String> result){
        delegate.GetMyAirlineName(result);
    }
}
