package com.mobiledev.uom.flyme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.mobiledev.uom.flyme.classes.DBHelper;
import com.mobiledev.uom.flyme.classes.Flight;
import com.mobiledev.uom.flyme.classes.Itinerary;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class DetailsActivityFragment extends Fragment {

    private static final String LOG_TAG = DetailsActivityFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = "#FlyMe\n";
    private String shareString = ""; //To string που θα εμφανίζεται όταν θα γίνεται το share

    private Itinerary itin; //To itinerary που θα περιέχει το intent

    private TextView departAirportTv, departDateTimeTv, arrivalAirportTv, arrivalDateTimeTv; //ΤV αποχώρησης
    private TextView retDepartAirportTv, retDepartDateTimeTv, retArrivalAirportTv, retArrivalDateTimeTv; //TV επιστροφής
    private TextView adultsPriceTv, childrenPriceTv, infantsPriceTv,totalPriceTv; //ΤV τιμών
    private View rootView, flightItemView; //Το flightItemView θα περιέχει το layout του κάθε flightΙtem
    private  LayoutInflater inflater2;
    private DateFormat dateTimeFormat; //To format της ημερομηνίας
    private int db_id;                 //Το id της εγγραφής από τη βάση με τις αναζητήσεις
    DBHelper myDBHelper;
    //private String urlText;
    private int adultsNo, childrenNo, infantsNo;  //Αριθμός επιβατών
    private char currencySymbol; //Το σύμβολο συναλλαγής
    //Οι πρώτες και τελευταίες πτήσεις αναχώρησης-επιστροφής
    private Flight departFirstFlight, departLastFlight, returnFirstFlight, returnLastFlight;

    public DetailsActivityFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDBHelper = new DBHelper(getActivity());

        //Βρίσκει το αντίστοιχο σύμβολο συναλλαγής, ανάλογα την επιλογή στα settings
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String currencyType = sharedPreferences.getString(
                getActivity().getString(R.string.currency_key),
                getActivity().getString(R.string.currency_eur));

        if(currencyType.equals(getResources().getString(R.string.currency_eur)))
            currencySymbol = '€';
        else if(currencyType.equals(getResources().getString(R.string.currency_usd)))
            currencySymbol = '$';
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        rootView = inflater.inflate(R.layout.fragment_details, container, false);

        //Αρχικοποίηση των textVIews Αναχώρησης και Επιστροφής
        departAirportTv = (TextView) rootView.findViewById(R.id.details_depart_airport);
        departDateTimeTv = (TextView) rootView.findViewById(R.id.details_depart_date_time);
        arrivalAirportTv = (TextView) rootView.findViewById(R.id.details_arrival_airport);
        arrivalDateTimeTv = (TextView) rootView.findViewById(R.id.details_arrival_date_time);

        retDepartAirportTv = (TextView) rootView.findViewById(R.id.details_ret_depart_airport);
        retDepartDateTimeTv = (TextView) rootView.findViewById(R.id.details_ret_date_time);
        retArrivalAirportTv = (TextView) rootView.findViewById(R.id.details_ret_arrival_airport);
        retArrivalDateTimeTv = (TextView) rootView.findViewById(R.id.details_ret_arrival_date_time);

        //To intent που θα λάβει από κάποια activity
        Intent intent = getActivity().getIntent();

        //Bundle bundle = intent.getExtras();

        //Αν το intent έχει extra ένα αντικείμενο Itinerary
        if (intent != null && intent.hasExtra("ItinObj")) {
            //Παίρνει το url που του στάλθηκε από κάποια activity
            //itin = (Itinerary) bundle.getSerializable("ItinObj");

            itin = (Itinerary) intent.getSerializableExtra("ItinObj"); //Να πάρει το extra Itinerary Object

            setDetailsTexts();  //Να εμφανίσει τα απαραίτητα textViews με τις τιμές τους

            if (intent.hasExtra("db_id")){ //Αν έχει και τιμή εγγραφής (που είναι απαραίτητο στην προκειμένη περίπτωση)

                db_id = intent.getExtras().getInt("db_id"); //Να πάρει το extra με το id της εγγραφής

                Cursor data = myDBHelper.getTableRow(db_id);
                data.moveToFirst();

                //Να πάρει το url της εγγραφής
                //urlText = data.getString(data.getColumnIndexOrThrow("url"));

                //Παίρνει τους αριθμούς των επιβατών
                adultsNo = data.getInt(data.getColumnIndexOrThrow("adultsNumber"));
                childrenNo = data.getInt(data.getColumnIndexOrThrow("childrenNumber"));
                infantsNo = data.getInt(data.getColumnIndexOrThrow("infantNumber"));


                setPricesTexts(); //Θέτει τα απαραίτητα textViews με τις τιμές και τις εμφανίζει στον χρήστη
            }
        }




        return rootView;
    }

    private Intent createShareForecastIntent() {



        //To format που θα δείχνει την ημερομηνία στο κείμενο του share
        DateFormat dateFormat  = new SimpleDateFormat("(d MMM yyyy - HH:mm)");

        shareString = FORECAST_SHARE_HASHTAG;

        //Το αεροδρόμιο και η ώρα αναχώρησης της πρώτης πτήσης.
        shareString += getString(R.string.from) + departFirstFlight.getOriginAirport().getValue() + " ";
        shareString += dateFormat.format(departFirstFlight.getDepartureDate().getTime()) + '\n';

        //Το αεροδρόμιο και η ώρα άφιξης της τελευταίας πτήσης.
        shareString += getString(R.string.to) + departLastFlight.getDestinationAirport().getValue()  + " ";
        shareString += dateFormat.format(departLastFlight.getArrivalDate().getTime()) + '\n';

        //Αν υπάρχουν στάσεις στο δρομολόγιο αναχώρησης να τις εμφανίσει
        if(itin.getOutboundFlightsList().size()>1)
            shareString += getString(R.string.stops) + ": " + stopsToString(itin.getOutboundFlightsList()) + "\n\n" ;

        //Αν υπάρχει δρομολόγιο επιστροφής
        if(itin.getInboundFlightsList()!=null){
            shareString+=getString(R.string.return_title)+'\n';
            //Το αεροδρόμιο και η ώρα αναχώρησης της πρώτης πτήσης.
            shareString += getString(R.string.from) + returnFirstFlight.getOriginAirport().getValue() + " ";
            shareString += dateFormat.format(returnFirstFlight.getDepartureDate().getTime()) + '\n';

            //Το αεροδρόμιο και η ώρα άφιξης της τελευταίας πτήσης.
            shareString += getString(R.string.to) + returnLastFlight.getDestinationAirport().getValue() + " ";
            shareString += dateFormat.format(returnLastFlight.getArrivalDate().getTime()) + '\n';

            //Αν υπάρχουν στάσεις στο δρομολόγιο επιστροφής να τις εμφανίσει
            if(itin.getInboundFlightsList().size()>1)
                shareString += getString(R.string.return_stops) + ": "  + stopsToString(itin.getInboundFlightsList())  + "\n\n";
        }

        if(adultsNo>0)
            shareString += getString(R.string.adults_number) + adultsNo +'\n';
        if(childrenNo>0)
            shareString += getString(R.string.children_number)  + childrenNo +'\n';
        if(infantsNo>0)
            shareString += getString(R.string.infants_number) + infantsNo + '\n';

        shareString += getString(R.string.total_price) + " " +itin.getModel().getTotalPrice() + currencySymbol;

        //Να δημιουργήσει το intent που μπορεί να κοινοποιήσει ο χρήστης σε άλλη εφαρμογή το δρομολόγιο που τον ενδιαφέρει
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        return shareIntent;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        inflater.inflate(R.menu.menu_details,menu);

        //Βρίσκει στο Μενού της toolbar το κουμπί share και του θέτει το share intent
        MenuItem menuItem = menu.findItem(R.id.action_share);
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share action provider = null");
        }
    }

    //Βάζει τιμές στα texts του δρομολογίου αναχώρησης και επιστροφής (αν υπάρχει)
    void setDetailsTexts(){
        List<Flight> outboundsList = itin.getOutboundFlightsList();
        List<Flight> inboundsList = itin.getInboundFlightsList();

        departFirstFlight = outboundsList.get(0);
        departLastFlight = outboundsList.get(outboundsList.size() - 1);

        departAirportTv.setText(getResources().getString(R.string.from) + departFirstFlight.getOriginAirport().getLabel());
        arrivalAirportTv.setText(getResources().getString(R.string.to) +departLastFlight.getDestinationAirport().getLabel());

        dateTimeFormat = new SimpleDateFormat("HH:mm \n d MMM yyyy");
        Calendar departDateTime = departFirstFlight.getDepartureDate();
        String departDateTimeStr = dateTimeFormat.format(departDateTime.getTime());
        departDateTimeTv.setText(departDateTimeStr);
        Calendar arrivalDateTime = departLastFlight.getArrivalDate();
        String arrivalDateTimeStr = dateTimeFormat.format(arrivalDateTime.getTime());
        arrivalDateTimeTv.setText(arrivalDateTimeStr);

        inflater2 = (LayoutInflater) getActivity().getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        //Τα linearLayouts των δρομολογίων Αναχώρησης και Επιστροφής
        LinearLayout departFlightsLayout = (LinearLayout) rootView.findViewById(R.id.details_depart_flights_layout);
        LinearLayout returnFlightsLayout = (LinearLayout) rootView.findViewById(R.id.details_return_flights_layout);

        //Κατασκευή του πίνακα Δρομολογίων Αναχώρησης
        createFlightsItems(outboundsList,departFlightsLayout);

        LinearLayout returnLayout = (LinearLayout) rootView.findViewById(R.id.details_return_layout);

        //Αν υπάρχει επιστροφή να ανοίξει το layout τιτλου επιστροφής με τα textviews
        if(inboundsList!=null){
            returnLayout.setVisibility(View.VISIBLE);
            returnFirstFlight = inboundsList.get(0);
            returnLastFlight = inboundsList.get(inboundsList.size() - 1);

            retDepartAirportTv.setText(getResources().getString(R.string.from) + returnFirstFlight.getOriginAirport().getLabel());
            retArrivalAirportTv.setText(getResources().getString(R.string.to) + returnLastFlight.getDestinationAirport().getLabel());

            Calendar retDepartDateTime = returnFirstFlight.getDepartureDate();
            String retDepartDateTimeStr = dateTimeFormat.format(retDepartDateTime.getTime());
            retDepartDateTimeTv.setText(retDepartDateTimeStr);
            Calendar retArrivalDateTime = returnLastFlight.getArrivalDate();
            String retArrivalDateTimeStr = dateTimeFormat.format(retArrivalDateTime.getTime());
            retArrivalDateTimeTv.setText(retArrivalDateTimeStr);

            //Κατασκευή του πίνακα Δρομολογίων Επιστροφής
            createFlightsItems(inboundsList,returnFlightsLayout);
        }
        else{ //Αλλιώς να το εξαφανίσει
            returnLayout.setVisibility(View.GONE);
        }


            //((RadioButton)ll.findViewById(R.id.radioButton)).setText(Integer.toString(i));
            //lin.addView(ll);
            //lin.addView(ll);


    }

    //Θέτει τις τιμές επιβατών στα textviews
    void setPricesTexts(){
        float price,productPrice;
        String productPriceStr;
        DecimalFormat decimalFormat = new DecimalFormat("#,###.00");

        //Αρχικοποίηση των TableRows τιμών και των textviews αυτών
        TableRow adultsRow = (TableRow) rootView.findViewById(R.id.details_row_adults);
        TableRow childrenRow = (TableRow) rootView.findViewById(R.id.details_row_children);
        TableRow infantsRow = (TableRow) rootView.findViewById(R.id.details_row_infants);

        TextView adultsOperationTv = (TextView) rootView.findViewById(R.id.details_adults_operation_tv);
        TextView childrenOperationTv = (TextView) rootView.findViewById(R.id.details_children_operation_tv);
        TextView infantsOperationTv = (TextView) rootView.findViewById(R.id.details_infants_operation_tv);

        //Αν υπάρχουν ενήλικες να γίνουν οι πράξεις και να εμφανίσει τις τιμές τους
        if(adultsNo >0){
            adultsPriceTv = (TextView) rootView.findViewById(R.id.details_adult_price);
            price = itin.getModel().getPricePerAdult();
            productPrice = price * adultsNo;
            productPriceStr = decimalFormat.format(productPrice);
            adultsOperationTv.setText(decimalFormat.format(itin.getModel().getPricePerAdult())+ "  *  "+ adultsNo + "  =");
            adultsPriceTv.setText(productPriceStr + " " + currencySymbol);

            //Αν υπάρχουν βρέφη να γίνουν οι πράξεις και να εμφανίσει τις τιμές τους
            if(infantsNo >0){
                infantsPriceTv = (TextView) rootView.findViewById(R.id.details_infant_price);
                price = itin.getModel().getPricePerInfant();
                productPrice = price * infantsNo;
                productPriceStr = decimalFormat.format(productPrice);
                infantsOperationTv.setText(decimalFormat.format(itin.getModel().getPricePerInfant()) + "  *  " + infantsNo + "  =");
                infantsPriceTv.setText(productPriceStr + " " + currencySymbol);
            } else{
                infantsRow.setVisibility(View.GONE);
            }
        } else{
            adultsRow.setVisibility(View.GONE);
        }

        //Αν υπάρχουν παιδιά να γίνουν οι πράξεις και να εμφανίσει τις τιμές τους
        if(childrenNo>0){
            childrenPriceTv = (TextView) rootView.findViewById(R.id.details_child_price);
            price = itin.getModel().getPricePerChild();
            productPrice = price * childrenNo;
            productPriceStr = decimalFormat.format(productPrice);
            childrenOperationTv.setText(decimalFormat.format(itin.getModel().getPricePerChild()) +"  *  "+ childrenNo +"  =");
            childrenPriceTv.setText(productPriceStr + " " + currencySymbol);
        } else{
            childrenRow.setVisibility(View.GONE);
        }

        //Συνολικό Ποσό
        totalPriceTv = (TextView) rootView.findViewById(R.id.details_total_price);
        price = itin.getModel().getTotalPrice();
        productPriceStr = decimalFormat.format(price);
        totalPriceTv.setText(productPriceStr + " " + currencySymbol);
    }

    //Δημιουργείται πεδίο για κάθε πτήση σε πίνακα Δρομολογίου με τις πληροφορίες της
    void createFlightsItems (List<Flight> flghtsList, LinearLayout lin) {
        Flight flight, nextFlight;
        DateFormat dateFormat  = new SimpleDateFormat("d MMM yyyy - HH:mm");
        String text;
        if(flghtsList.size()>0) nextFlight = flghtsList.get(0);
        else nextFlight=null;

        for(int i = 0; i < flghtsList.size(); i++) {
            flight = nextFlight;
            if(i+1<flghtsList.size()) nextFlight= flghtsList.get(i+1);
            else nextFlight=null;

            //Παίρνει το view του layout flight item
            flightItemView = inflater2.inflate(R.layout.flight_item, null);

            ((TextView) flightItemView.findViewById(R.id.flight_id_tv)).setText(getResources().getString(R.string.flight_no) + Integer.toString(i + 1)); //Νούμερο Πτήσης
            ((TextView) flightItemView.findViewById(R.id.flight_origin_airport_tv)).
                    setText(flight.getOriginAirport().getLabel()); //Αεροδρόμιο Αναχώρησης

            //Εμφανίζει την ημερομηνία Αναχώρησης
            Calendar departDate = flight.getDepartureDate();
            String departDateStr = dateFormat.format(departDate.getTime());
            ((TextView) flightItemView.findViewById(R.id.flight_origin_date_tv)).setText(departDateStr);

            //Εμφανίζει την ημερομηνία Άφιξης
            Calendar arrivalDate = flight.getArrivalDate();
            String arrivalDateStr = dateFormat.format(arrivalDate.getTime());
            ((TextView) flightItemView.findViewById(R.id.flight_destin_date_tv)).setText(arrivalDateStr);

            /*//Εμφανίζει την διάρκεια της πτήσης
            ((TextView) flightItemView.findViewById(R.id.flight_duration_tv)).
                    setText(ItineraryAdapter.getTimeDiffernceString(departDate,arrivalDate));*/

            //Εμφανίζει το αεροδρόμιο άφίξης
            ((TextView) flightItemView.findViewById(R.id.flight_destin_airport_tv)).
                    setText(flight.getDestinationAirport().getLabel());
            //Εμφανίζει την Αεροπορική Εταιρία
            ((TextView) flightItemView.findViewById(R.id.flight_airline_tv)).
                    setText(flight.getAirline().getName() + " - "+flight.getAirline().getCode());

            //Αν υπάρχει επόμενη πτήση να ελέγξει αν υπάρχει διαφορά
            // στα αεροδρόμια αφιξης αυτής - αναχώρησης επομενης για να γράψει και τα δύο
            if(nextFlight!=null){
                String arrValue = flight.getDestinationAirport().getValue();
                String nextOrigValue = nextFlight.getOriginAirport().getValue();
                text = getResources().getString(R.string.stop) + " " + (i+1) + ": " + arrValue;
                if(!arrValue.equals(nextOrigValue))
                    text+=" --> " + nextOrigValue;
                ((TextView) flightItemView.findViewById(R.id.flight_stop_tv)).setText(text);
                /*((TextView) flightItemView.findViewById(R.id.flight_stop_duration_tv)).
                        setText(getResources().getString(R.string.duration) + ItineraryAdapter.getTimeDiffernceString(arrivalDate,nextFlight.getDepartureDate()));*/
                //αλλιως να γράψει την συνολική διάρκεια δρομολογίου στο text των στάσεων και να σβηστεί το text της διάρκειας στάσης
            }else {
                ((TextView) flightItemView.findViewById(R.id.flight_stop_tv)).setVisibility(View.GONE);
                /*((TextView) flightItemView.findViewById(R.id.flight_stop_tv)).
                        setText(getResources().getString(R.string.total_duration) +
                                ItineraryAdapter.getTimeDiffernceString(flghtsList.get(0).getDepartureDate(),arrivalDate));
                ((TextView) flightItemView.findViewById(R.id.flight_stop_duration_tv)).setVisibility(View.GONE);*/
            }

            //createFlightItem(flight,nextFlight,i);

            //Προσθέτει το πεδίο-πτήση στον πίνακα
            lin.addView(flightItemView);
        }
    }

    //Οι κωδικοί στάσεων σε string μιας λίστας πτήσεων
    String stopsToString(List<Flight> flightsList){
        String stopsStr = "";
        String destinStr, nextOriginStr;
        Flight flight, nextFlight;

        nextFlight = flightsList.get(0);
        for(int i=0; i<flightsList.size() - 1; i++){ //Μέχρι -1 γιατί στην τελευταία θα είναι ο τελικός προσδιορισμός
            flight = nextFlight;
            //nextFlight = flightsList.get(i+1);
            nextFlight = flightsList.get(i+1);
            destinStr = flight.getDestinationAirport().getValue();
            nextOriginStr = nextFlight.getOriginAirport().getValue();

            //Προσθέτει το αεροδρόμιο άφιξης και το αεροδρόμιο αναχώρισης της επόμενης πτήσης αν δεν είναι ίδιο
            stopsStr += destinStr;
            if(!destinStr.equals(nextOriginStr))
                stopsStr += "->" + nextOriginStr;

            if(i<flightsList.size()-2) stopsStr += ", "; //Αν υπάρχουν δύο τουλάχιστον πτήσεις ακόμα
                                            // (ώστε να υπάχει κι άλλη στάση) να προσθέσει ένα κόμμα

        }
        return stopsStr;
    }

}
