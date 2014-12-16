package com.muchbeer.king;

/**
 * Created by muchbeer on 12/9/2014.
 */

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.muchbeer.king.data.WeatherContract2;
import com.muchbeer.king.sync.SunshineSyncAdapter;

/**
 * A {@link PreferenceActivity} that presents a set of application settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity
        implements Preference.OnPreferenceChangeListener {

    boolean mBindingPreference;
    // since we use the preference change initially to populate the summary
    // field, we'll ignore that change at start of the activity
    // boolean mBindingPreference;

    String tzLocations;
    public static final String PREFS_NAME = "MyLocationName";
    public static final String LocName = "locKey";
    public static final String TAG = "List me error";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        // For all preferences, attach an OnPreferenceChangeListener so the UI summary can be
        // updated when the preference changes.
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_location_key)));
        bindPreferenceSummaryToValue(findPreference(getString(R.string.pref_units_key)));

        SharedPreferences getCity = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

       //getting Location

        SharedPreferences.Editor editor = getCity.edit();
        editor.putString(LocName, tzLocations);
        editor.commit();
       // Reading from SharedPreferences
      //  tzLocations = settingsLocation.getString(LokName, "");
      //  Log.d(TAG, tzLocations);
    }

    /**
     * Attaches a listener so the summary is always updated with the preference value.
     * Also fires the listener once, to initialize the summary (so it shows up before the value
     * is changed.)
     */
    private void bindPreferenceSummaryToValue(Preference preference) {
        mBindingPreference = true;

        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(this);

        // Trigger the listener immediately with the preference's
        // current value.
        onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));

      //  tzLocations = PreferenceManager.getDefaultSharedPreferences(preference.getContext())
        //        .getString(preference.getKey(), "");
        mBindingPreference = false;
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        String stringValue = value.toString();
       // String tzLocations =
        // boolean mBindingPreference = true;

        // are we starting the preference activity?
        // are we starting the preference activity?
        if ( !mBindingPreference ) {
            if (preference.getKey().equals(getString(R.string.pref_location_key))) {
                tzLocations = value.toString();


               //Replace FetchWeatherTask with SyncAdapter
                /*
                FetchWeatherTask weatherTask = new FetchWeatherTask(this);
                String location = value.toString();
                weatherTask.execute(location);
                */
                SunshineSyncAdapter.syncImmediately(this);
            } else {
                // notify code that weather may be impacted
                getContentResolver().notifyChange(WeatherContract2.WeatherEntry.CONTENT_URI, null);
            }
        }

        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public Intent getParentActivityIntent() {
        return super.getParentActivityIntent().addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    }
}