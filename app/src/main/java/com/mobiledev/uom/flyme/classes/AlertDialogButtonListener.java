package com.mobiledev.uom.flyme.classes;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.mobiledev.uom.flyme.ResultsActivityFragment;

//Ελέγχει αν υπάρχει σύνδεση στο ίντερνετ
public class AlertDialogButtonListener implements View.OnClickListener {

    private Dialog dialog;
    private ResultsActivityFragment.ShowFlightsTask flightsTask;
    private Activity activity;
    private String urlText;

    public AlertDialogButtonListener(Dialog dialog, ResultsActivityFragment.ShowFlightsTask flightsTask, Activity activity, String urlText) {
        this.dialog = dialog;
        this.flightsTask = flightsTask;
        this.activity = activity;
        this.urlText = urlText;
    }

    //Αν βρεθεί σύνδεση καλείται η flightsTask που βρίσκει τις πτήσεις
    //Αλλιώς το κουμπί μένει σταθερό
    @Override
    public void onClick(View view) {
        if (isOnline()) {
            flightsTask.execute(urlText);
            dialog.dismiss();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
