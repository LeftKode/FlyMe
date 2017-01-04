package com.mobiledev.uom.flyme;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        // Βάζει ενα listener στο preference για να ελέγχει για αλλαγές
        bindPreferenceSummaryToValue(findPreference(getString(R.string.currency_key)));
    }

    private void bindPreferenceSummaryToValue(Preference preference) {
        //Ελέγχει για αλλαγές στα settings
        preference.setOnPreferenceChangeListener(this);

        //Θετει στον listener την τιμή που υπάρχει στις ρυθμίσεις
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    //Αλλαγή της τιμής στα settings αναλογα με την επιλογή του χρήστη
    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();

        ListPreference listPreference = (ListPreference) preference;
        int prefIndex = listPreference.findIndexOfValue(stringValue);
        if (prefIndex >= 0)
            preference.setSummary(listPreference.getEntries()[prefIndex]);

        return true;
    }
}
