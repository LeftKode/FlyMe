package com.mobiledev.uom.flyme;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mobiledev.uom.flyme.classes.Airport;
import com.mobiledev.uom.flyme.classes.AirportAutoCompleteAdapter;
import com.mobiledev.uom.flyme.classes.DBHelper;
import com.mobiledev.uom.flyme.classes.SingleToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;


/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private DBHelper myDBHelper;
    private static final int THRESHOLD = 2;
    private static final int maxFlyers = 9;

    private static final int defaultNumOfAdults = 1;
    private static final int defaultNumOfChildren = 0;
    private static final int defaultNumOfInfants = 0;

    private TextView departureDateTextView; //TextView που θα δείχνει τον τίτλο: ημερομηνία αναχώρησης
    private TextView returnDateTextView;    //TextView που θα δείχνει την ημερομηνία επιστροφής
    private Button departurePickDateBtn;    //Το κουμπί που θα πατάει ο χρήστης για να εισάγει ημερομηνία αναχώρησης
    private Button returnPickDateBtn;       //Το κουμπί που θα πατάει ο χρήστης για να εισάγει ημερομηνία επιστροφής
    private Calendar departureDate;         //Η ημερομηνία αναχώρησης
    private Calendar returnDate;            //Η ημερομηνία επιστροφής

    private TextView activeDateDisplay;    //Θα δείχνει ποιο κουμπί πατήθηκε για να βάλει την ημερομηνία στο τέλος

    private DelayAutoCompleteTextView originDelayAutoCompleteTV;        //Το πεδίο που θα επιλέγει ο χρήστης αεροδρόμιο αναχώρησης
    private DelayAutoCompleteTextView destinationDelayAutoCompleteTV;   //Το πεδίο που θα επιλέγει ο χρήστης αεροδρόμιο προορισμού

    private Switch withDestinationSwitch;
    private LinearLayout returnDateLayout;     //Το Layout της ημερομηνίας επιστροφής

    private Button adultsAddBtn, childrenAddBtn, infantsAddBtn;         //Κουμπιά άθροισης επιβατών
    private Button adultsMinusBtn, childrenMinusBtn, infantsMinusBtn;   //Κουμπιά μείωσης επιβατών

    private TextView adultsNumTextView, childrenNumTextView,
            infantsNumTextView;    //TextViews που δείχνουν τους αριθμούς των επιβατών

    private static int numOfAdults, numOfChildren,
            numOfInfants, sumOfFlyers;   //Αριθμοί επιβατών ενηλίκων, παιδιών, βρεφών και το άθροισμα αυτών

    private SingleToast myToast;           //Custom toast
    private CheckBox nonStopCheckBox;      //Το CheckBox που είναι για το αν ο χρήστης θέλει ή όχι
                                                //μόνο απευθείας πτήσεις
    private boolean nonStopValue;  //Η τιμή του "Μόνο απευθείας πτήσεις"

    private Button searchButton;        //Το κουμπί που θα στέλνει το ερώτημα με τα φίλτρα στον server

    private View rootView;

    AirportAutoCompleteAdapter adapter;     //Ο Custom Adapter για την εμφάνιση του πάνελ με τα προτεινόμενα αεροδρόμια
    Airport origin, destination;            //Αεροδρόμια αναχώρησης και προορισμού

    public SearchActivityFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Αρχικοποίηση των ημερομηνιών departureDate και returnDate
        myDBHelper = new DBHelper(getActivity());
        departureDate = new GregorianCalendar();
        returnDate = new GregorianCalendar();
        numOfAdults = defaultNumOfAdults;
        numOfChildren = defaultNumOfChildren;
        numOfInfants = defaultNumOfInfants;
        sumOfFlyers = defaultNumOfAdults + defaultNumOfChildren + defaultNumOfInfants;
        nonStopValue = false;
        myToast = new SingleToast();
        Log.e("test","onCreate");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Δημιουργία του rootView
        rootView = inflater.inflate(R.layout.fragment_search, container, false);

        //Ευρεση των textView από το fragment του search
        departureDateTextView = (TextView) rootView.findViewById(R.id.departureDateTextView);
        returnDateTextView = (TextView) rootView.findViewById(R.id.returnDateTextView);

        //Εύρεση των κουμπιών από το fragment του search για τις ημερομηνίες
        departurePickDateBtn = (Button) rootView.findViewById(R.id.departureDateBtn);
        returnPickDateBtn = (Button) rootView.findViewById(R.id.returnDateBtn);

        //Εύρεση των πεδίων που επιλέγει ο χρήστης αεροδρόμιο
        originDelayAutoCompleteTV = (DelayAutoCompleteTextView) rootView.findViewById(R.id.originDACTV);
        destinationDelayAutoCompleteTV = (DelayAutoCompleteTextView) rootView.findViewById(R.id.destinationDACTV);

        //Εύρεση του switch που ανοίγει το Layout της ημερομηνίας επιστροφής
        withDestinationSwitch = (Switch) rootView.findViewById(R.id.withReturnSwitch);

        //Εύρεση του Layout της ημερομηνίας επιστροφής
        returnDateLayout = (LinearLayout) rootView.findViewById(R.id.returnDateLayout);

        //Εύρεση των κουμπιών άθροισης και μείωσης των αριθμών των επιβατών
        adultsAddBtn = (Button) rootView.findViewById(R.id.adultsAddBtn);
        childrenAddBtn = (Button) rootView.findViewById(R.id.childrenAddBtn);
        infantsAddBtn = (Button) rootView.findViewById(R.id.infantsAddBtn);
        adultsMinusBtn = (Button) rootView.findViewById(R.id.adultsMinusBtn);
        childrenMinusBtn = (Button) rootView.findViewById(R.id.childrenMinusBtn);
        infantsMinusBtn = (Button) rootView.findViewById(R.id.infantsMinusBtn);

        //Εύρεση των TextView των αριθμών των επιβατών
        adultsNumTextView = (TextView) rootView.findViewById(R.id.adultsNumTextView);
        childrenNumTextView = (TextView) rootView.findViewById(R.id.childrenNumTextView);
        infantsNumTextView = (TextView) rootView.findViewById(R.id.infantsNumTextView);

        //Εύρεση του checkbox για το αν θέλει ο χρήστης να του εμφανιστούν μόνο οι Απευθείας Πτήσεις
        nonStopCheckBox = (CheckBox) rootView.findViewById(R.id.non_stop_checkbox);

        //Εύρεση των textviews των αριθμών των επιβατών
        adultsNumTextView.setText(Integer.toString(defaultNumOfAdults));
        childrenNumTextView.setText(Integer.toString(defaultNumOfChildren));
        infantsNumTextView.setText(Integer.toString(defaultNumOfInfants));

        //Εύρεση του κουμπιού αναζήτησης πτήσεων
        searchButton = (Button) rootView.findViewById(R.id.search_flights_btn);

        //Προσθήκη ελάχίστου ορίου απαιτούμενων γραμμάτων από τον χρήστη για να ξεκινήσει η αναζήτηση αεροδρομίων
        originDelayAutoCompleteTV.setThreshold(THRESHOLD);
        destinationDelayAutoCompleteTV.setThreshold(THRESHOLD);


        //Δημιουργία ενός AirportAutoComplete adapter και εισαγωγή του στα δυό πεδία
        adapter = new AirportAutoCompleteAdapter(getActivity());
        adapter.setToast(myToast);
        originDelayAutoCompleteTV.setAdapter(adapter);
        destinationDelayAutoCompleteTV.setAdapter(adapter);

        //originDelayAutoCompleteTV.setValidator(new Validator());
        originDelayAutoCompleteTV.setOnFocusChangeListener(new FocusListener());
        destinationDelayAutoCompleteTV.setOnFocusChangeListener(new FocusListener());

        //Listener που θα εμφανίζει ή όχι το Layout της ημερομηνίας επιστροφής
        withDestinationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if(isChecked){
                    returnDateLayout.setVisibility(View.VISIBLE);
                    if(departureDate.after(returnDate))
                        returnDate = (Calendar) departureDate.clone();
                        updateDisplay(returnPickDateBtn,returnDate);
                }else{
                    returnDateLayout.setVisibility(View.GONE);
                }
            }
        });

        //Προσθήκη κινούμενου εφέ αναμονής
        originDelayAutoCompleteTV.setLoadingIndicator(
                (android.widget.ProgressBar) rootView.findViewById(R.id.pb_loading_indicator));
        destinationDelayAutoCompleteTV.setLoadingIndicator(
                (android.widget.ProgressBar) rootView.findViewById(R.id.pb_loading_indicator2));



        //Προσθήκη λειτουργίας όταν ο χρήστης θα πατάει ένα απ' τα διαθέσιμα αεροδρόμια
        originDelayAutoCompleteTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                origin = (Airport) adapterView.getItemAtPosition(position);
                originDelayAutoCompleteTV.setText(origin.getLabel());
            }
        });
        destinationDelayAutoCompleteTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                destination = (Airport) adapterView.getItemAtPosition(position);
                destinationDelayAutoCompleteTV.setText(destination.getLabel());
            }
        });

        //Προσθήκη Listener στο κουμπί departurePickDateBtn
        departurePickDateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //showDateDialog(departurePickDateBtn, departureDate);
                activeDateDisplay = departurePickDateBtn;
                ((SearchActivity) getActivity()).showDatePickerDialog(v);
            }
        });

        //Προσθήκη Listener στο κουμπί returnPickDateBtn
        returnPickDateBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                activeDateDisplay = returnPickDateBtn;
                ((SearchActivity) getActivity()).showDatePickerDialog(v);
            }
        });

        //Προσθήκη Listener στα κουμπιά αύξησης επιβατών
        adultsAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numOfAdults = increaseFlyers(adultsNumTextView,numOfAdults);
            }
        });
        childrenAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numOfChildren = increaseFlyers(childrenNumTextView,numOfChildren);
            }
        });
        infantsAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numOfInfants<numOfAdults)
                    numOfInfants = increaseFlyers(infantsNumTextView,numOfInfants);
                else
                    myToast.show(getActivity(),getResources().getString(R.string.infants_higher_than_max_message).toString(),Toast.LENGTH_SHORT);
            }
        });

        //Προσθήκη Listener στα κουμπιά μείωσης επιβατών
        adultsMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(numOfAdults>numOfInfants)
                    numOfAdults = decreaseFlyers(adultsNumTextView,numOfAdults);
                else if(numOfAdults!=0)
                    myToast.show(getActivity(),getResources().getString(R.string.infants_higher_than_max_message).toString(),Toast.LENGTH_SHORT);
            }
        });
        childrenMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numOfChildren = decreaseFlyers(childrenNumTextView,numOfChildren);
            }
        });
        infantsMinusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numOfInfants = decreaseFlyers(infantsNumTextView,numOfInfants);
            }
        });

        //Προσθήκη Listener στο checkbox "Μόνο απευθείας στάση"
        nonStopCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                nonStopValue = isChecked;
                Log.v("test",String.valueOf(isChecked));
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Αν δεν έχει επιλέξει αεροδρόμιο αναχώρησης του εμφανίζει κατάλληλο μήνυμα
                if(origin==null){
                    myToast.show(getActivity(),getResources().getString(R.string.origin_is_missing_message),Toast.LENGTH_SHORT);
                }
                //Αλλιώς αν δεν έχει επιλέξει αεροδρόμιο προορισμού του εμφανίζει κατάλληλο μήνυμα
                else if(destination == null){
                    myToast.show(getActivity(),getResources().getString(R.string.destination_is_missing_message),Toast.LENGTH_SHORT);
                }
                //Αλλιώς στέλνει το url στην SearchActivity
                else{
                    String urlText = createSearchFlightsUrl();
                    int entry_position;
                    Cursor data = myDBHelper.getID();
                    data.moveToFirst();
                    entry_position = data.getInt(data.getColumnIndexOrThrow("_id"));
                    Intent intent = new Intent(getActivity(),ResultsActivity.class).putExtra("db_id",entry_position);
                    startActivity(intent);
                }

            }
        });

        /*//Προσθήκη Listener στο κουμπί searchButton
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.v("test","ds");
                //String urlText = createSearchFlightsUrl();
                //Intent intent = new Intent(getActivity(),ResultsActivity.class).putExtra(Intent.EXTRA_TEXT,urlText);
                //startActivity(intent);
                Log.d("TE","TRRR");
                Log.v("Test",createSearchFlightsUrl());
            }
        });*/

        //Αρχικοποίηση των text των κουμπιών των ημερομηνιών με τη σημερινή ημερομηνία
        updateDisplay(departurePickDateBtn, Calendar.getInstance());
        updateDisplay(returnPickDateBtn, Calendar.getInstance());



        return rootView;
    }

    //Δημιουργεί το string του url που θα χρειαστεί για την αναζήτηση πτήσεων
    String createSearchFlightsUrl(){
        final String BASE_URL = "https://api.sandbox.amadeus.com/v1.2/flights/low-fare-search?";    //Το βασικό url
        final String API_KEY = "apikey";                        //To κλειδί που μας δίνεται*
        final String ORIGIN = "origin";                         //Αεροδρόμιο Αναχώρησης*
        final String DESTINATION = "destination";               //Αεροδρόμιο Προορισμού*
        final String DEPART_DATE = "departure_date";            //Ημέρα Αναχώρησης*
        final String RETURN_DATE = "return_date";               //Ημέρα Επιστροφής
        final String ARRIVE_BY = "arrive_by";                   //Ημέρα και ώρα που θα φτάσει στον προορισμό
        final String RETURN_BY = "return_by";                   //Ημέρα και ώρα που θα φτάσει στην επιστροφή
        final String ADULTS = "adults";                         //Αριθμός Ενηλίκων (default = 1)
        final String CHILDREN = "children";                     //Αριθμός Παιδιών έως 12 ετών (default = 0)
        final String INFANTS = "infants";                       //Αριθμός Βρεφών έως 2 ετών (default = 0)
        final String INCLUDE_AIRLINES = "include_airlines";     //Οι περιλαμβανόμενες Αεροπορικές Εταιρείες
        final String EXCLUDE_AIRLINES = "exclude_airlines";     //Οι εξερούμενες Αεροπορικές Εταιρείες
        final String NONSTOP = "nonstop";                       //Απευθείας πτήσεις (default = false)
        final String MAX_PRICE = "max_price";                   //Μέγιστη Τιμή
        final String CURRENCY = "currency";                     //Νόμισμα Συναλλαγής (default = USD)
        final String TRAVEL_CLASS = "travel_class";             //Κατηγορία Θέσεων
        final String NO_OF_RESULTS = "number_of_results";       //Αριθμός Αποτελεσμάτων


        //Αρχικοποίηση πεδίων για εισαγωγή στη βάση
        String url;
        String originLoc;
        String destinationLoc;
        String departDate;
        String retDate = null;
        int adultsNo = defaultNumOfAdults;
        int childrenNo = defaultNumOfChildren;
        int infantNo = defaultNumOfChildren;
        int nonStop = 0;

        //Φορμάτ Ημερομηνιών
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        fmt.setCalendar(departureDate);

        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(API_KEY, BuildConfig.AMADEUS_KEY)
                .appendQueryParameter(ORIGIN, origin.getValue())
                .appendQueryParameter(DESTINATION, destination.getValue())
                .appendQueryParameter(DEPART_DATE, String.format(fmt.format(departureDate.getTime()))).build();

        originLoc = origin.getLabel();
        destinationLoc = destination.getLabel();
        departDate = String.format(fmt.format(departureDate.getTime()));

        //Αν έχει επιλέξει ημερομηνία επιστροφής να την προσθέσει
        if(withDestinationSwitch.isChecked()){
            builtUri = builtUri.buildUpon().appendQueryParameter(RETURN_DATE, String.format(fmt.format(returnDate.getTime()))).build();
            retDate = String.format(fmt.format(returnDate.getTime()));
        }

        //Αν ο αριθμός των επιβατών είναι διαφορετικοί σε κάποια κατηγορία να την προσθέτει στο uri
        if(numOfAdults!=defaultNumOfAdults){
            builtUri = builtUri.buildUpon().appendQueryParameter(ADULTS, Integer.toString(numOfAdults)).build();
            adultsNo = numOfAdults;
        }

        if(numOfChildren!=defaultNumOfChildren){
            builtUri = builtUri.buildUpon().appendQueryParameter(CHILDREN, Integer.toString(numOfChildren)).build();
            childrenNo = numOfChildren;
        }

        if(numOfInfants!=defaultNumOfInfants){
            builtUri = builtUri.buildUpon().appendQueryParameter(INFANTS,Integer.toString(numOfInfants)).build();
            infantNo = numOfInfants;
        }

        //Εάν είναι τσεκαρισμένη η επιλογή "Μόνο Απευθείας Πτήσεις" να προσθέσει το αντίστοιχο ερώτημα στο uri
        if(nonStopValue){
            builtUri = builtUri.buildUpon().appendQueryParameter(NONSTOP,"true").build();
            nonStop = 1;
        }

        //Επιστρέφει το νόμισμα που επέλεξε ο χρήστης στην αναζήτησ και ανάλογα την επιλογή στέλνει στη διεύθυνση το αντίστοιχο νόμισμα
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String currencyType = sharedPreferences.getString(
                            getString(R.string.currency_key),
                            getString(R.string.currency_eur));

        if(currencyType.equals(getString(R.string.currency_eur)))
            builtUri = builtUri.buildUpon().appendQueryParameter(CURRENCY, "EUR").build();
        else
            builtUri = builtUri.buildUpon().appendQueryParameter(CURRENCY, "USD").build();

        //Το νόμισμα συναλλαγής αν είναι διαφορετικό του USD να προσθέτει το αντίστοιχο ερώτημα στο uri
        //builtUri = builtUri.buildUpon().appendQueryParameter(CURRENCY, "EUR").build();
                //.appendQueryParameter(NO_OF_RESULTS, "10")
                //.build();

        url = builtUri.toString();

        String db_url = url.substring(0, url.lastIndexOf('=')+1);
        Cursor data = myDBHelper.getTableData();

        if(data.getCount() > 9){
            myDBHelper.deleteRow();
            myDBHelper.insertData(db_url,originLoc,destinationLoc,departDate,retDate, adultsNo, childrenNo, infantNo, nonStop);
        }
        else
            myDBHelper.insertData(url,originLoc,destinationLoc,departDate,retDate, adultsNo, childrenNo, infantNo, nonStop);


        return builtUri.toString();
    }

    //Μέθοδος που αυξάνει τους επιβάτες
    int increaseFlyers(TextView textView, int numOfFlyers){
        //Αν το άθροισμά των επιβατών είναι μικρότερο από το μέγιστο αριθμό να προσθέσει επιβάτη
        if(sumOfFlyers < maxFlyers){
            sumOfFlyers++;
            numOfFlyers++;
            textView.setText(Integer.toString(numOfFlyers));
        }else{
            //Αλλιώς να βγάλει κατάλληλο μήνυμα
            myToast.show(getActivity(),getResources().getString(R.string.flyers_higher_than_max_message).toString(),Toast.LENGTH_SHORT);
        }
        return numOfFlyers;
    }

    //Μέθοδος που μειώνει τους επιβάτες
    int decreaseFlyers(TextView textView, int numOfFlyers){
        //Δεν γίνεται το άθροισμα των επιβατών να γίνει μηδέν κι έτσι εκτυπώνει κατάλληλο μήνυμα

        if(sumOfFlyers==1){
            myToast.show(getActivity(),getResources().getString(R.string.flyers_zero_num_message).toString(),Toast.LENGTH_SHORT);
        }
        //Αλλιώς το άθροισμά των επιβατών στη συγκεκριμένη ομάδα πρεπει να είναι τουλάχιστον 1 για να εκτελεστεί
        else if(numOfFlyers > 0){
            sumOfFlyers--;
            numOfFlyers--;
            textView.setText(Integer.toString(numOfFlyers));

        }
        return numOfFlyers;
    }

    @Override
    //Καλείται όταν διαλέξει ο χρήστης ημερομηνια
    public void onDateSet(DatePicker view, int year, int month, int day) {

        //Εισαγωγή της ημερομηνίας που διάλεξε ο χρήστης ανάλογα ποια ημερομηνία ορίζει

        //Αν είναι το κουμπι ημερομηνίας αναχώρησης
        if(activeDateDisplay == departurePickDateBtn){
            departureDate.set(year,month,day);
            updateDisplay(departurePickDateBtn, departureDate);
            if(withDestinationSwitch.isChecked() && departureDate.after(returnDate)){
                returnDate.set(year,month,day);
                updateDisplay(returnPickDateBtn, returnDate);
            }

        }
        //Αλλιώς αν είναι το κουμπι ημερομηνίας επιστροφής
        else if ( activeDateDisplay == returnPickDateBtn){
            returnDate.set(year,month,day);
            updateDisplay(returnPickDateBtn, returnDate);
            if(departureDate.after(returnDate)){
                returnDate = (Calendar) departureDate.clone();
                updateDisplay(returnPickDateBtn, returnDate);
                myToast.show(getActivity(),getResources().getString(R.string.destin_date_after_origin_message),Toast.LENGTH_SHORT);
            }
        }

    }

    //Εμφανίζει με κατάλληλο format την ημερομηνία στο κατάλληλο κουμπί
    private void updateDisplay(TextView dateDisplay, Calendar date) {
        //TODO: Να βάλουμε και αν έχει επιλεγμένη την αγγλική γλώσσα να του βγάλει ανάποδα τους μήνες

        dateDisplay.setText(
                new StringBuilder()
                        .append(date.get(Calendar.DAY_OF_MONTH)).append("/")
                        .append(date.get(Calendar.MONTH) + 1).append("/") // Ο Μήνας ξεκινάει από 0 οπότε προσθέτουμε 1
                        .append(date.get(Calendar.YEAR)).append(""));

    }

    //Ο Listener που ενεργοποιείται όταν το αλλάζει το focus από ένα view
    class FocusListener implements View.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            Log.v("Test", "Focus changed");
            //Εάν το πεδίο που έχασε το focus είναι το πεδίο αεροδρομίο αναχώρησης
            if (v.getId() == R.id.originDACTV && !hasFocus) {
                //Εάν είναι άδειο το πεδίο να κάνει null το αεροδρόμιο origin
                 if(originDelayAutoCompleteTV.getText().length() == 0)
                    origin = null;
                 //Αλλιώς αν το origin δεν είναι null και το κείμενο δεν είναι ίδιο με αυτό του label του origin, να
                 //τοποθετήσει στο κείμενο το label αυτό.
                 else if(origin !=null && !originDelayAutoCompleteTV.getText().equals(origin.getLabel()) )
                     originDelayAutoCompleteTV.setText(origin.getLabel());
                 //Αλλιώς να διαγράψει το text.
                 else
                     originDelayAutoCompleteTV.setText("");
            }
            //Εάν το πεδίο που έχασε το focus είναι το πεδίο αεροδρομίο προορισμού
            else if(v.getId() == R.id.destinationDACTV && !hasFocus){
                //Εάν είναι άδειο το πεδίο να κάνει null το αεροδρόμιο destination
                if(destinationDelayAutoCompleteTV.getText().length() == 0)
                    destination = null;
                //Αλλιώς αν το destination δεν είναι null και το κείμενο δεν είναι ίδιο με αυτό του label του destination, να
                //τοποθετήσει στο κείμενο το label αυτό.
                else if(destination !=null && !destinationDelayAutoCompleteTV.getText().equals(destination.getLabel()) )
                    destinationDelayAutoCompleteTV.setText(destination.getLabel());
                //Αλλιώς να διαγράψει το text.
                else
                    destinationDelayAutoCompleteTV.setText("");
            }
        }
    }

}
