package com.muchbeer.king;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * Created by muchbeer on 12/9/2014.
 */
public class DetailActivity extends ActionBarActivity {

    public static final String DATE_KEY = "forecast_date";
    public static final String TAG = "Check whats wrong";
    private static final String LOCATION_KEY = "location";
    private static final int DETAIL_LOADER = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {

            //Create the detail fragment and add it to the activity
            //using a fragment transaction

            String date = getIntent().getStringExtra(DATE_KEY);

            Bundle arguments = new Bundle();
            arguments.putString(DATE_KEY, date);
            Log.d(TAG, date);

            if(date == null) {
                Toast toast = Toast.makeText(getApplicationContext(), "Check this error" + date, Toast.LENGTH_LONG);
                toast.show();
            }

            else {
                DetailFragment fragment = new DetailFragment();
                fragment.setArguments(arguments);
            }

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.weather_detail_container, new DetailFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}

