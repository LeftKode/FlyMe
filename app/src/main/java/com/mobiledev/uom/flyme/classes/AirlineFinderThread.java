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
import java.util.Iterator;
import java.util.Map;

public class AirlineFinderThread extends Thread {

    private static final String LOG_TAG = AirlineFinderThread.class.getSimpleName();

    //static Queue<Airline> queue = new ConcurrentLinkedQueue<Airline>();

    private Map<String, Airline> airlinesMap;
    private String codes;


    public AirlineFinderThread(Map<String, Airline> airlinesMap, String codes) {
        this.airlinesMap = airlinesMap;
        this.codes = codes;
    }

    @Override
    public void run() {
        String airlineJsonString = sendUrlForFindAirlinesNames();

        try {
            getAirlineDataFromJson(airlineJsonString);
            //return getAirlineDataFromJson(airlineJsonString);
        } catch (JSONException e){
            Log.e(LOG_TAG, e.getMessage(), e);
        }
    }


    String sendUrlForFindAirlinesNames(){
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String airlineJsonStr = null;

        try{
            final String BASE_URL = "https://iatacodes.org/api/v6/airlines?";
            final String API_KEY = "api_key";
            final String AIRLINE_CODE = "code";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY, BuildConfig.IATA_KEY)
                    .appendQueryParameter(AIRLINE_CODE, codes.substring(0,codes.length()-1)) //αγνοεί το τελευταίο κόμμα που θα έχει
                    .build();


            Iterator<String> keySetIterator = airlinesMap.keySet().iterator();
            String uriStr = builtUri.toString();
            /*String key;
            key = keySetIterator.next();
            uriStr+=key;
            while(keySetIterator.hasNext()) {
                key = keySetIterator.next();
                uriStr += "," + key;
            }*/

            Log.e(LOG_TAG,uriStr);
            URL url = new URL(uriStr);

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                Log.v(LOG_TAG,"Empty inputstream!");
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
                Log.v(LOG_TAG,"Empty stream!");
                return null;
            }

            airlineJsonStr = buffer.toString();

            Log.v(LOG_TAG, "Forecast string: " + airlineJsonStr);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.

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
        return airlineJsonStr;
    }

    void getAirlineDataFromJson(String airlineJsonStr) throws JSONException {
        JSONObject object = new JSONObject(airlineJsonStr);
        JSONArray responseArray = object.getJSONArray("response");
        String code, name;
        Airline airln;
        for (int i = 0; i < responseArray.length(); i++) {
            JSONObject responseObject = responseArray.getJSONObject(i);
            code = responseObject.getString("code");
            name = responseObject.getString("name");
            airln=new Airline(code,name);
            airlinesMap.put(code,airln);
        }


    }
}
