package com.mobiledev.uom.flyme;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class SearchActivityFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    private TextView departureDateTextView; //TextView που θα δείχνει τον τίτλο: ημερομηνία αναχώρησης
    private TextView returnDateTextView;    //TextView που θα δείχνει την ημερομηνία επιστροφής
    private Button departurePickDateBtn;    //Το κουμπί που θα πατάει ο χρήστης για να εισάγει ημερομηνία αναχώρησης
    private Button returnPickDateBtn;       //Το κουμπί που θα πατάει ο χρήστης για να εισάγει ημερομηνία επιστροφής
    private Calendar departureDate;         //Η ημερομηνία αναχώρησης
    private Calendar returnDate;            //Η ημερομηνία επιστροφής

    private TextView activeDateDisplay;    //Θα δείχνει ποιο κουμπί πατήθηκε για να βάλει την ημερομηνία στο τέλος

    public SearchActivityFragment() {

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Αρχικοποίηση των ημερομηνιών departureDate και returnDate
        departureDate = new GregorianCalendar();
        returnDate = new GregorianCalendar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Δημιουργία του rootView
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);

        //Εύρεση των κουμπιών από το fragment του search
        departurePickDateBtn = (Button) rootView.findViewById(R.id.departureDateBtn);
        returnPickDateBtn = (Button) rootView.findViewById(R.id.returnDateBtn);

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

        //Αρχικοποίηση των text των κουμπιών των ημερομηνιών με τη σημερινή ημερομηνία
        updateDisplay(departurePickDateBtn, Calendar.getInstance());
        updateDisplay(returnPickDateBtn, Calendar.getInstance());

        return rootView;
    }

    @Override
    //Καλείται όταν διαλέξει ο χρήστης ημερομηνια
    public void onDateSet(DatePicker view, int year, int month, int day) {

        //Εισαγωγή της ημερομηνίας που διάλεξε ο χρήστης ανάλογα ποια ημερομηνία ορίζει

        //Αν είναι το κουμπι ημερομηνίας αναχώρησης
        if(activeDateDisplay == departurePickDateBtn){
            departureDate.set(year,month,day);
            updateDisplay(departurePickDateBtn, departureDate);

        }
        //Αλλιώς αν είναι το κουμπι ημερομηνίας επιστροφής
        else if ( activeDateDisplay == returnPickDateBtn){
            returnDate.set(year,month,day);
            updateDisplay(returnPickDateBtn, returnDate);
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
}