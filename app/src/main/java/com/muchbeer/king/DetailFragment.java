package com.muchbeer.king;

/**
 * Created by muchbeer on 12/9/2014.
 */

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.muchbeer.king.data.WeatherContract2;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

  //  private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    //  public static final String DATE_KEY = "forecast_date";
    private static final String LOCATION_KEY = "location";
    private static final int DETAIL_LOADER = 0;



    private static final String LOG_TAG = DetailFragment.class.getSimpleName();
    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    public static final String DATE_KEY = "forecast_date";

    private ShareActionProvider mShareActionProvider;
    private String mLocation;
    private String mForecast;
    private String mDateStr;

   private static final String[] columns = {
            //id needs to be fully qualified with a table name
            //the content provider joins the location and weather table
            //on the one hand, that's annoying. On the other you can search weather
            WeatherContract2.WeatherEntry.TABLE_NAME + "." + WeatherContract2.WeatherEntry._ID,
            WeatherContract2.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract2.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract2.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract2.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract2.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract2.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract2.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract2.WeatherEntry.COLUMN_DEGREES,
            WeatherContract2.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract2.LocationEntry.COLUMN_LOCATION_SETTING

    };

    private ImageView mIconView;
    private TextView mFriendlyDateView;
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTempView;
    private TextView mLowTempView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;


    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    //On device Rotate changes everything
    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putString(LOCATION_KEY, mLocation);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(DATE_KEY)) {
            mDateStr = arguments.getString(DATE_KEY);
        }

        if (savedInstanceState != null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mIconView = (ImageView) rootView.findViewById(R.id.detail_icon);
        mDateView = (TextView) rootView.findViewById(R.id.detail_date_textview);
        mFriendlyDateView = (TextView) rootView.findViewById(R.id.detail_day_textview);
        mDescriptionView = (TextView) rootView.findViewById(R.id.detail_forecast_textview);
        mHighTempView = (TextView) rootView.findViewById(R.id.detail_high_textview);
        mLowTempView = (TextView) rootView.findViewById(R.id.detail_low_textview);
        mHumidityView = (TextView) rootView.findViewById(R.id.detail_humidity_textview);
        mWindView = (TextView) rootView.findViewById(R.id.detail_wind_textview);
        mPressureView = (TextView) rootView.findViewById(R.id.detail_pressure_textview);
        return rootView;
        // The detail Activity called via intent.  Inspect the intent for forecast data.

    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState !=null) {
            mLocation = savedInstanceState.getString(LOCATION_KEY);

        }

        Bundle arguments = getArguments();
        if(arguments !=null && arguments.containsKey(DetailActivity.DATE_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        }
        //replace by Bundle arguments
      /*
    Intent intent = getActivity().getIntent();
        if(intent !=null && intent.hasExtra(DetailActivity.DATE_KEY)) {
              getLoaderManager().initLoader(DETAIL_LOADER, null, this);

        }

        */
    }

    @Override
    public void onResume() {
        super.onResume();

        //  Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();

        if ( arguments !=null &&
                arguments.containsKey(DATE_KEY) &&
                mLocation !=null &&
                !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }

    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.v(LOG_TAG, "in onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null");
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String dateString=getActivity().getIntent().getStringExtra(DATE_KEY);

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherContract2.WeatherEntry.COLUMN_DATETEXT + " ASC";

        //tHIS IS A called when new Loader needs to be created
        //For the forecast view we're showing only a small subset of store data
        //specify the column we need.



        String dateStr = getArguments().getString(DetailActivity.DATE_KEY);


        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherContract2.WeatherEntry.buildWeatherLocationWithDate(mLocation, dateStr);
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                columns,
                null,
                null,
                sortOrder
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if ( data != null && data.moveToFirst()) {

            int weatherId = data.getInt(data.getColumnIndex(
                    WeatherContract2.WeatherEntry.COLUMN_WEATHER_ID
            ));

            // Use placeholder Image
            mIconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

            // Read date from cursor and update views for day of week and date
            String date = data.getString(data.getColumnIndex(WeatherContract2.WeatherEntry.COLUMN_DATETEXT));
            String friendlyDateText = Utility.getDayName(getActivity(), date);
            String dateText = Utility.getFormattedMonthDay(getActivity(), date);
            mFriendlyDateView.setText(friendlyDateText);
            mDateView.setText(dateText);

            // Read description from cursor and update view
            String description = data.getString(data.getColumnIndex(
                    WeatherContract2.WeatherEntry.COLUMN_SHORT_DESC));
            mDescriptionView.setText(description);

            // Read high temperature from cursor and update view
            boolean isMetric = Utility.isMetric(getActivity());

            double high = data.getDouble(data.getColumnIndex(WeatherContract2.WeatherEntry.COLUMN_MAX_TEMP));
            String highString = Utility.formatTemperature(getActivity(), high);
            mHighTempView.setText(highString);

            // Read low temperature from cursor and update view
            double low = data.getDouble(data.getColumnIndex(WeatherContract2.WeatherEntry.COLUMN_MIN_TEMP));
            String lowString = Utility.formatTemperature(getActivity(), low);
            mLowTempView.setText(lowString);

            // Read humidity from cursor and update view
            float humidity = data.getFloat(data.getColumnIndex(WeatherContract2.WeatherEntry.COLUMN_HUMIDITY));
            mHumidityView.setText(getActivity().getString(R.string.format_humidity, humidity));

            // Read wind speed and direction from cursor and update view
            float windSpeedStr = data.getFloat(data.getColumnIndex(WeatherContract2.WeatherEntry.COLUMN_WIND_SPEED));
            float windDirStr = data.getFloat(data.getColumnIndex(WeatherContract2.WeatherEntry.COLUMN_DEGREES));
            mWindView.setText(Utility.getFormattedWind(getActivity(), windSpeedStr, windDirStr));

            // Read pressure from cursor and update view
            float pressure = data.getFloat(data.getColumnIndex(WeatherContract2.WeatherEntry.COLUMN_PRESSURE));
            mPressureView.setText(getActivity().getString(R.string.format_pressure, pressure));

            // We still need this for the share intent
            mForecast = String.format("%s - %s - %s/%s", dateText, description, high, low);

            Log.v(LOG_TAG, "Forecast String: " + mForecast);

            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareForecastIntent());
            }

        }




    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}