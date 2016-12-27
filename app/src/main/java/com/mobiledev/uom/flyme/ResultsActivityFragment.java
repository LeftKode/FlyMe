package com.mobiledev.uom.flyme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class ResultsActivityFragment extends Fragment {

    private String urlText;
    private String databaseUrlText;

    public ResultsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //H SearchActivity καλείται μέσω ενός intent που περιέχει ένα string που είναι το url
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_results, container, false);
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            //Παίρνει το url που του στάλθηκε από κάποια activity
            urlText = intent.getStringExtra(Intent.EXTRA_TEXT);
            Log.v("Test",urlText);
        }

        if (intent != null && intent.hasExtra("databaseUrl")) {
            //Παίρνει το url που του στάλθηκε από κάποια activity
            databaseUrlText = intent.getStringExtra("databaseUrl");
            Log.v("TestUrl",databaseUrlText);
        }

        //TODO Να εμφανίζει ένα textView αν μας έρθει από τον server ότι δεν υπάρχει απάντηση

        //TODO Οι ημερομηνίες μπορεί να είναι λάθος αν στο κινητό έχει ψεύτικη ημερομηνία.
        //Να του εμφανίζεται μήνυμα "Κάτι πήγε στραβά! Μήπως έχετε λάνθασμένη ημερομηνία?"

        return rootView;
    }
}
