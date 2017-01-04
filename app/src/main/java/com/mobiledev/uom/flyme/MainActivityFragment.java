package com.mobiledev.uom.flyme;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.mobiledev.uom.flyme.classes.DBHelper;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;
import static android.view.View.GONE;

public class MainActivityFragment extends Fragment{

    DBHelper myDBHelper;

    public MainActivityFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        //Αρχικοποιεί έναν DBHelper με τη δημιουργία του activity
        myDBHelper = new DBHelper(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_main,container, false);
        ListView listView = (ListView)rootView.findViewById(R.id.main_page_flight_list);
        TextView textView = (TextView)rootView.findViewById(R.id.main_page_textview);

        // Ο Cursor δέχεται όλα τα δεδομένα της βάσης για ανάγνωση
        final Cursor data = myDBHelper.getTableData();

        //Ελέγχος αν η βάση έχει εγγραφές ή όχι
        if(data.getCount() == 0)
            //Αν δεν έχει ενημερώνει τον χρήστη ότι δεν υπάρχει κάτι
            textView.setText(getResources().getString(R.string.no_flights_searched) + '\n'+ getResources().getString(R.string.start_searching));
        else {
            //Αν έχεις εγγραφές τις εμφανίζει με την πιο πρόσφατη εγγραφή πρώτη
            textView.setText(getResources().getString(R.string.see_your_flights));
            SQLAdapter adapter = new SQLAdapter(getContext(), data, 0);
            listView.setAdapter(adapter);
        }

        //Αν ο χρήστης πατήσει κάποια από τις εγγραφές στο Listview, δημιουργείται ένα intent
        //με το αντίστοιχο id της εγγραφής που πατήθηκε
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getContext(), ResultsActivity.class);
                intent.putExtra("db_id", data.getInt(data.getColumnIndexOrThrow("_id")));
                startActivity(intent);
            }
        });
        return rootView;
    }

    //Δημιουργία ενός custom adapter για διάβασμα τιμών από τη βάση
    public class SQLAdapter extends CursorAdapter {

        Cursor tableData;
        private LayoutInflater inflater;
        public SQLAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            tableData = c;
            inflater = (LayoutInflater) context.getSystemService(LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.db_listview_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            TextView originView = (TextView) view.findViewById(R.id.originLoc);
            TextView destinationView = (TextView) view.findViewById(R.id.destinationLoc);
            TextView depDateView = (TextView) view.findViewById(R.id.departureDate);
            TextView arrDateView = (TextView) view.findViewById(R.id.arrivalDate);
            TextView adultNo = (TextView)view.findViewById(R.id.adultNoTextView);
            TextView childrenNo = (TextView)view.findViewById(R.id.children_textView);
            TextView infantNo = (TextView) view.findViewById(R.id.infantNo_textView);
            TextView nonStop = (TextView) view.findViewById(R.id.nonStop_textView);

            //Ανάγνωση των τιμών μέσα από τη βάση ανάλογα με τη στήλη στην οποία ανήκουν.
            String originLocation = cursor.getString(cursor.getColumnIndexOrThrow("originLocation"));
            String destLocation = cursor.getString(cursor.getColumnIndexOrThrow("destinationLocation"));
            String departureDate = cursor.getString(cursor.getColumnIndexOrThrow("departureDate"));
            String returnDate = cursor.getString(cursor.getColumnIndexOrThrow("returnDate"));
            int numOfAdults = cursor.getInt(cursor.getColumnIndexOrThrow("adultsNumber"));
            int numOfChildren = cursor.getInt(cursor.getColumnIndexOrThrow("childrenNumber"));
            int numOfInfants = cursor.getInt(cursor.getColumnIndexOrThrow("infantNumber"));
            int stops = cursor.getInt(cursor.getColumnIndexOrThrow("nonStop"));

            originView.setText(getResources().getString(R.string.from) + originLocation);
            destinationView.setText(getResources().getString(R.string.to) + destLocation);
            depDateView.setText(getResources().getString(R.string.transition) + departureDate);
            if(returnDate != null)
                arrDateView.setText(getResources().getString(R.string.return_from)+ returnDate);
            else
                arrDateView.setText(getResources().getString(R.string.return_not_declared));

            if (numOfAdults == 0 )
                adultNo.setVisibility(GONE);
            else
                adultNo.setText(getResources().getString(R.string.adult_number)+Integer.toString(numOfAdults));

            if (numOfChildren == 0)
                childrenNo.setVisibility(GONE);
            else
                childrenNo.setText(getResources().getString(R.string.children_number) +Integer.toString(numOfChildren));

            if (numOfInfants == 0)
                infantNo.setVisibility(GONE);
            else
                infantNo.setText(getResources().getString(R.string.infant_number)+Integer.toString(numOfInfants));

            if(stops == 0)
                nonStop.setText(getResources().getString(R.string.non_direct_flights));
            else
                nonStop.setText(getResources().getString(R.string.direct_flights));

        }
    }
}

