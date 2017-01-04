package com.mobiledev.uom.flyme;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mobiledev.uom.flyme.classes.Airline;
import com.mobiledev.uom.flyme.classes.AirlineFinderThread;
import com.mobiledev.uom.flyme.classes.Airport;
import com.mobiledev.uom.flyme.classes.AirportFinderThread;
import com.mobiledev.uom.flyme.classes.AlertDialogButtonListener;
import com.mobiledev.uom.flyme.classes.DBHelper;
import com.mobiledev.uom.flyme.classes.Flight;
import com.mobiledev.uom.flyme.classes.FlightModel;
import com.mobiledev.uom.flyme.classes.Itinerary;
import com.mobiledev.uom.flyme.classes.ItineraryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static android.view.View.GONE;
import static java.lang.Boolean.FALSE;


/**
 * A placeholder fragment containing a simple view.
 */
public class ResultsActivityFragment extends Fragment {

    private ProgressDialog progressDialog;
    private int db_id;
    DBHelper myDBHelper;
    private String urlText;
    private String originLoc;
    private String destinationLoc;
    private String departDateString;
    private String retunDateString;
    private int adultNo;
    private int childrenNo;
    private int infantNo;
    private int nonStopValue;
    private List<Itinerary> itineraries;


    List<String> airportsCodesList = new ArrayList<>();     //Λίστα με τα αεροδρόμια που βρέθηκαν
    Map<String, Airport> airportsMap = new ConcurrentHashMap<>(); //Map  με τα αεροδρόμια (Κωδικος τους το κλειδί)
    Map<String, Airline> airlinesMap = new HashMap<>();  //Map  με τις αεροπορικές εταιρίες (Κωδικος τους το κλειδί)

    private ListView listView;

    View rootView;

    public ResultsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        myDBHelper = new DBHelper(getActivity());
        //Δημιουργία μηνύματος να περιμένει ο χρήστης όσο τα δεδομένα φορτώνουν
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        progressDialog.setMessage(getResources().getString(R.string.loading_message));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //H SearchActivity καλείται μέσω ενός intent που περιέχει ένα string που είναι το url
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);
        if (intent != null && intent.hasExtra("db_id")) {
            //Παίρνει το id της γραμμής της βάσης πόυ ειναι αποθηκευμενη η αναζήτηση
            db_id = intent.getExtras().getInt("db_id");
            Cursor data = myDBHelper.getTableRow(db_id);
            data.moveToFirst();

            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String currencyType = sharedPreferences.getString(
                    getString(R.string.currency_key),
                    getString(R.string.currency_eur));

            String dbUrlText = data.getString(data.getColumnIndexOrThrow("url"));
            if(currencyType.equals(getString(R.string.currency_eur)))
                urlText = dbUrlText + "EUR";
            else
                urlText = dbUrlText + "USD";

            adultNo = data.getInt(data.getColumnIndexOrThrow("adultsNumber"));
            childrenNo = data.getInt(data.getColumnIndexOrThrow("childrenNumber"));
            infantNo = data.getInt(data.getColumnIndexOrThrow("infantNumber"));
            originLoc = data.getString(data.getColumnIndexOrThrow("originLocation"));
            destinationLoc = data.getString(data.getColumnIndexOrThrow("destinationLocation"));
            departDateString = data.getString(data.getColumnIndexOrThrow("departureDate"));
            retunDateString = data.getString(data.getColumnIndexOrThrow("returnDate"));
            nonStopValue =  data.getInt(data.getColumnIndexOrThrow("nonStop"));

            ShowFlightsTask flightsTask = new ShowFlightsTask();

            //Αν υπάρχει σύνδεση στο δίκτυο να καλέσει την flightTask
            if(isOnline()){
                flightsTask.execute(urlText);
            }else{
                //Αλλιώς ενημερώνει τον χρήστη οτι δεν βρέθηκε δίκτυο
                DialogInterface.OnClickListener listener = null;


                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle(getResources().getString(R.string.result_internet_not_found_title));
                alertDialogBuilder.setMessage(getResources().getString(R.string.result_internet_not_found_message));
                alertDialogBuilder.setPositiveButton(getResources().getString(R.string.result_internet_not_found_button), listener);
                alertDialogBuilder.setCancelable(FALSE);

                AlertDialog dialog = alertDialogBuilder.create();
                dialog.show();
                Button dialogButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                dialogButton.setOnClickListener(new AlertDialogButtonListener(dialog,flightsTask,getActivity(),urlText));
            }
            //Αρχικοποίήση της listView και των textViews που θα περιγράφουν την αναζήτηση
            listView = (ListView) rootView.findViewById(R.id.results_listview);
            TextView searchResults = (TextView) rootView.findViewById(R.id.resultsSearchInfo);
            TextView locationInfo = (TextView) rootView.findViewById(R.id.resultsLocationInfo);
            TextView dateInfo = (TextView) rootView.findViewById(R.id.resultsDateInfo);
            TextView adultInfo = (TextView) rootView.findViewById(R.id.resultsAdultInfo);
            TextView childrenInfo = (TextView) rootView.findViewById(R.id.resultsChildrenInfo);
            TextView infantInfo = (TextView) rootView.findViewById(R.id.resultsInfantInfo);
            TextView stopsInfo = (TextView) rootView.findViewById(R.id.results_stops);

            //Ανάκτηση πληροφοριών από τα πεδία εγγραφής της βάσης
            String locationInfoString = originLoc.substring(originLoc.indexOf('[')+1,originLoc.lastIndexOf(']')) +" - "
                    + destinationLoc.substring(destinationLoc.indexOf('[')+1,destinationLoc.lastIndexOf(']'));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date departDate = null;
            Date returnDate = null;
            try{
                departDate = format.parse(departDateString);

            }catch (ParseException e){
                e.printStackTrace();
            }

            String finalDepartDate = new SimpleDateFormat("d MMM").format(departDate);


            searchResults.setText(getResources().getString(R.string.result_search));
            locationInfo.setText(locationInfoString);
            dateInfo.setText(finalDepartDate);
            //Αν υπάρχει ημερομηνία επιστροφής να εμφανίσει την ημερομηνία επιστροφής
            if(retunDateString!=null){
                try {
                    returnDate = format.parse(retunDateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String finalReturnDate = new SimpleDateFormat("d MMM").format(returnDate);
                dateInfo.setText(finalDepartDate + " - " + finalReturnDate);
            }


            //Άμα είχε επιλέξει ο χρήστης μόνο απευθείας πτήσεις να του το εμφανίσει στον τίτλο των αποτελεσμάτων
            if(nonStopValue == 1)
                stopsInfo.setText(getResources().getString(R.string.result_direct_fligths));
            else
                stopsInfo.setVisibility(GONE);

            //Εμφάνιση αριθμών επιβατών
            adultInfo.setText("Ενήλικες: " + adultNo);
            childrenInfo.setText("Παιδιά: " + childrenNo);
            infantInfo.setText("Βρέφη: " + infantNo);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intentDetails = new Intent(getContext(), DetailsActivity.class);

                    //Bundle bundle = new Bundle();
                    //bundle.putSerializable("ItinObj", itineraries.get(i));
                    //intentDetails.putExtras(bundle);

                    intentDetails.putExtra("ItinObj", itineraries.get(i));
                    intentDetails.putExtra("db_id", db_id);
                    startActivity(intentDetails);
                }
            });
            Log.e("Test", urlText);

        }

       /* if (intent != null && intent.hasExtra("databaseUrl")) {
            //Παίρνει το url που του στάλθηκε από κάποια activity
            databaseUrlText = intent.getStringExtra("databaseUrl");
            Log.v("TestUrl",databaseUrlText);
        }*/

        //TODOm Οι ημερομηνίες μπορεί να είναι λάθος αν στο κινητό έχει ψεύτικη ημερομηνία.
        //Να του εμφανίζεται μήνυμα "Κάτι πήγε στραβά! Μήπως έχετε λάνθασμένη ημερομηνία?"
        this.rootView = rootView;

        return rootView;
    }

    //Στέλνει τα απαραίτητα url και παίρνει τις απαντήσεις για τα διαθέσιμα δρομολόγια
    public class ShowFlightsTask extends AsyncTask<String , Void, List<FlightModel>> {
        private final String LOG_TAG = ResultsActivityFragment.ShowFlightsTask.class.getSimpleName();
        private String airlinesCodes = "";
        private boolean returnDateExists;
        ExecutorService executor = Executors.newFixedThreadPool(4); //Executor που θα περιέχει όλα τα νήματα που θα δημιουργηθούν

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show(); //Εμφάνιση του μηνύματος να περιμένει ο χρήστης
        }

        //Παίρνει τις πληροφορίες των αποτελεσμάτων της αναζήτησης δρομολογίων με JSON
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
            float totalPrice, totalFarePerAdult, totalFarePerChild, totalFarePerInfant;
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



            //Αν συμπεριλαμβάνεται και ημερομηνία επιστροφής να πάρει true η αντίστοιχη μεταβλητή
            returnDateExists = urlText.contains("return_date");
            //boolean zeroAdults = urlText.contains("adults=0");
            //boolean infantsExist = urlText.contains("infants");

            for(int i=0; i < flightResult.length(); i++){

                //Παίρνει την συνολική τιμή
                newObject = flightResult.getJSONObject(i);
                fare = newObject.getJSONObject(F_FARE);
                priceStr = fare.getString(F_PRICE);
                totalPrice = Float.valueOf(priceStr);

                //Παίρνει τις τιμές των επιβατών
                if(adultNo>0){
                    priceAdultObj = fare.getJSONObject(F_PRICE_PER_ADULT);
                    priceStr = priceAdultObj.getString(F_TOTAL_FARE);
                    totalFarePerAdult = Float.valueOf(priceStr);
                }else
                    totalFarePerAdult = 0;

                if(infantNo>0){
                    priceInfantObj = fare.getJSONObject(F_PRICE_PER_INFANT);
                    priceStr = priceInfantObj.getString(F_TOTAL_FARE);
                    totalFarePerInfant = Float.valueOf(priceStr);
                }else
                    totalFarePerInfant = 0;

                if(childrenNo>0){
                    totalFarePerChild = (float) ((totalPrice -((adultNo*totalFarePerAdult) + (infantNo*totalFarePerInfant)))/childrenNo);
                    totalFarePerChild = (float) Math.round(totalFarePerChild * 100) / 100; //Για να παίρνει μέχρι 2 δεκαδικά
                }else
                    totalFarePerChild = 0;

                itinArray = newObject.getJSONArray(F_ITINS);

                //Φτιάχνει ένα FlightModel που μπορεί να περιέχει ένα ή περισσότερα itineraries
                model = new FlightModel();
                model.setItineraries(new ArrayList<Itinerary>());

                //Θέτει στο model τις τιμές
                model.setPricePerAdult(totalFarePerAdult);
                model.setPricePerChild(totalFarePerChild);
                model.setPricePerInfant(totalFarePerInfant);
                model.setTotalPrice(totalPrice);

                //Για κάθε δρομολόγιο
                for(int j=0; j<itinArray.length(); j++) {
                    itinObject = itinArray.getJSONObject(j);

                    outboundObject = itinObject.getJSONObject(F_OUTBOUNDS);
                    outboundFlightArray = outboundObject.getJSONArray(F_FLIGHTS);

                    outboundFlightList = new ArrayList<>();
                    for(int l=0; l<outboundFlightArray.length(); l++){
                        flightObject = outboundFlightArray.getJSONObject(l);
                        flight = createFlight(flightObject);
                        outboundFlightList.add(flight);
                    }
                    //TODOm Να παιρνω τις διευθυνσεις του model
                    Itinerary itin = new Itinerary(model);
                    model.getItineraries().add(itin);
                    model.getItineraries().get(j).setOutboundFlightsList(outboundFlightList);


                    if(returnDateExists){
                        inboundObject = itinObject.getJSONObject(F_INBOUNDS);
                        inboundFlightArray = inboundObject.getJSONArray(F_FLIGHTS);

                        inboundFlightList = new ArrayList<>();
                        for(int l=0; l<inboundFlightArray.length(); l++){
                            flightObject = inboundFlightArray.getJSONObject(l);
                            flight = createFlight(flightObject);
                            inboundFlightList.add(flight);
                        }
                        model.getItineraries().get(j).setInboundFlightsList(inboundFlightList);
                    }
                }
                modelList.add(model);

            }
            //Δημιουργία ενός thread για την εύρεση της αεροπορικής εταιρείας
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
                        flight.setDestinationAirport(airportsMap.get(flight.getDestinationAirport().getValue()));
                        flight.setAirline(airlinesMap.get(flight.getAirline().getCode()));
                    }

                    if(returnDateExists){
                        for(Flight flight: itin.getInboundFlightsList()){
                            flight.setOriginAirport(airportsMap.get(flight.getOriginAirport().getValue()));
                            flight.setDestinationAirport(airportsMap.get(flight.getDestinationAirport().getValue()));
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

            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            String departure = flightObject.getString(F_DEPS);
            String arrival = flightObject.getString(F_ARRS);
            Calendar departDate = Calendar.getInstance();
            departDate.setTime(dateFormat.parse(departure));
            Calendar arrivalDate = Calendar.getInstance();
            arrivalDate.setTime(dateFormat.parse(arrival));

            JSONObject originObject = flightObject.getJSONObject(F_ORIGIN);
            String originValue = originObject.getString(F_AIRP);
            originValueOnly = new Airport(originValue,null);

            //Δημιουργία ενός thread για την εύρεση του αεροδρομίο αναχώρησης
            if(!airportsCodesList.contains(originValue)){
                airportsCodesList.add(originValue);
                executor.execute(new AirportFinderThread(airportsMap,originValue));
            }


            JSONObject destinationObject = flightObject.getJSONObject(F_DEST);
            String destinationValue = destinationObject.getString(F_AIRP);
            destinValueOnly = new Airport(destinationValue,null);

            //Δημιουργία ενός thread για την εύρεση του αεροδρομίο άφιξης
            if(!airportsCodesList.contains(destinationValue)){
                airportsCodesList.add(destinationValue);
                executor.execute(new AirportFinderThread(airportsMap,destinationValue));
            }

            String airlineCode = flightObject.getString(F_AIRL);
            Airline airlineCodeOnly = new Airline(airlineCode,null);


            if(!airlinesCodes.contains(airlineCode)){
                airlinesCodes+=airlineCode+",";
            }

            //Επιστρέφει μόνο τους κωδικούς των airlines και airports για να μπορέσει μετά όταν θα βρει τα
            //labels να τα τοποθετήσει στις πτήσεις που θα έχουν τον αντίστοιχο κωδικό
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

               // Log.v(LOG_TAG, "Forecast string: " + flightJsonString);
            } catch (FileNotFoundException e){
                return null;
            }
            catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                //Αν δεν πήγε κάτι καλά στην απάντηση δεν υπάρχει λόγος να γίνει το parsing
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

            itineraries = new ArrayList<>();
            TextView textView = (TextView) rootView.findViewById(R.id.resultsSearchInfo);
            TextView locationInfo = (TextView) rootView.findViewById(R.id.resultsLocationInfo);
            TextView dateInfo = (TextView) rootView.findViewById(R.id.resultsDateInfo);
            TextView adultInfo = (TextView) rootView.findViewById(R.id.resultsAdultInfo);
            TextView childrenInfo = (TextView) rootView.findViewById(R.id.resultsChildrenInfo);
            TextView infantInfo = (TextView) rootView.findViewById(R.id.resultsInfantInfo);
            TextView stopsInfo = (TextView) rootView.findViewById(R.id.results_stops);
            
            if(result == null){
                textView.setText("Δεν βρέθηκαν διαθέσιμες πτήσεις για την αναζήτηση σας!");
                locationInfo.setText("");
                dateInfo.setText("");
                adultInfo.setText("");
                childrenInfo.setText("");
                infantInfo.setText("");
                stopsInfo.setText("");
            }
            else{
                for (FlightModel model: result) {
                    for (Itinerary itin: model.getItineraries()) {
                        itineraries.add(itin);
                    }
                }
            }
            //Να σταματήσει η εμφανιση του μήνυματος "Περίμενε" και να εμφανιστούν τα αποτελέσματα στο listview
            progressDialog.dismiss();
            ItineraryAdapter adapter = new ItineraryAdapter(getContext(), R.layout.fragment_main, itineraries,getActivity());
            listView.setAdapter(adapter);

        }
    }

    //Ελέγχει αν έχει σύνδεση στο ίντερνετ
    public boolean isOnline() {
        ConnectivityManager cm =
               (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
       NetworkInfo netInfo = cm.getActiveNetworkInfo();
       return netInfo != null && netInfo.isConnected();
    }


}
