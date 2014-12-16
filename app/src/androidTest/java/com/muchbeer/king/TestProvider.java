package com.muchbeer.king;

/**
 * Created by muchbeer on 11/30/2014.
 */

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.AndroidTestCase;
import android.util.Log;

import com.muchbeer.king.data.WeatherContract2;
import com.muchbeer.king.data.WeatherContract2.LocationEntry;

import java.util.Map;
import java.util.Set;

public class TestProvider extends AndroidTestCase {

    public static final String LOG_TAG = TestProvider.class.getSimpleName();


    //delete
    /*
    public void testDeleteDb() throws Throwable {
        mContext.deleteDatabase(WeatherDBHelper.DATABASE_NAME);
      //    db.close();
    }
    */

    // brings our database to an empty state
    public void testdeleteAllRecords() {
        mContext.getContentResolver().delete(
                WeatherContract2.WeatherEntry.CONTENT_URI,
                null,
                null
        );
        mContext.getContentResolver().delete(
                LocationEntry.CONTENT_URI,
                null,
                null
        );

        Cursor cursor = mContext.getContentResolver().query(
                WeatherContract2.WeatherEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();

        cursor = mContext.getContentResolver().query(
                LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals(0, cursor.getCount());
        cursor.close();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    public void setUp() {
        testdeleteAllRecords();
    }

    static public String TEST_CITY_NAME = "North Pole";
    static final String TEST_LOCATION = "99705";
    static final String TEST_DATE = "20141205";

    ContentValues getLocationContentValues() {

        String testLocationSetting = "99705";
        double testLatitude = 64.772;
        double testLongitude = -147.355;

        //Create dummy data
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, TEST_LOCATION);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);
        return values;

    }

    static public ContentValues getWeatherContentValues(long locationRowId){

        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherContract2.WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherContract2.WeatherEntry.COLUMN_DATETEXT, "20141205");
        weatherValues.put(WeatherContract2.WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherContract2.WeatherEntry.COLUMN_HUMIDITY,1.2);
        weatherValues.put(WeatherContract2.WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherContract2.WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherContract2.WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherContract2.WeatherEntry.COLUMN_SHORT_DESC,"Asteroids");
        weatherValues.put(WeatherContract2.WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherContract2.WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;
    }
    //Make sure that everything in our content matches our insert
    static public void validateCursor(Cursor valueCursor, ContentValues expectedValues) {

        assertTrue(valueCursor.moveToFirst());

        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse(idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals(expectedValue, valueCursor.getString(idx));
        }

    }
    public void testGetType() {
        // content://com.example.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherContract2.WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherContract2.WeatherEntry.CONTENT_TYPE, type);

        String testLocation = "94074";
        // content://com.example.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(
                WeatherContract2.WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.example.android.sunshine.app/weather
        assertEquals(WeatherContract2.WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://com.example.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherContract2.WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.example.android.sunshine.app/weather
        assertEquals(WeatherContract2.WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.example.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.example.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.example.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);
    }

    public void testUpdateLocation() {

        testdeleteAllRecords();
        // Create a new map of values, where column names are the keys
        ContentValues values = getLocationContentValues();



        Uri locationUri = mContext.getContentResolver().
                insert(LocationEntry.CONTENT_URI, values);
        long locationRowId = ContentUris.parseId(locationUri);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        ContentValues values2 = new ContentValues(values);
        values2.put(LocationEntry._ID, locationRowId);
        values2.put(LocationEntry.COLUMN_CITY_NAME, "Santa's Village");

        int count = mContext.getContentResolver().update(
                LocationEntry.CONTENT_URI, values2, LocationEntry._ID + "= ?",
                new String[] { Long.toString(locationRowId)});

        assertEquals(count, 1);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),
                null,
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

       // validateCursor(cursor, values2);
        if (cursor.moveToFirst()) {
            validateCursor(cursor, values2);
        }
        cursor.close();
    }





    public void testInsertReadProvider() {


        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
         //Call the function back
        ContentValues values = getLocationContentValues();


        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, values);
        //verify we a got a row back
        long locationRowId= ContentUris.parseId(locationUri);
       // assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);


        //Specify which columns you want ******Remove1********

        //A cursor in
        Cursor cursor = mContext.getContentResolver().query(LocationEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        if (cursor.moveToFirst()) {

            //Get the value in each column by finding the appropiate
            //*************Remove 2 ******

            validateCursor(cursor, values);
            //Fantastic. Now that we have a location, add some
            //weather, Now that we have a location, add some weather
            //Removeed to a method getWeatherContentValues(long location
            ContentValues weatherValues = getWeatherContentValues(locationRowId);

             Uri insertUri= mContext.getContentResolver()
                    .insert(WeatherContract2.WeatherEntry.CONTENT_URI, weatherValues);
            long weatherRowId = ContentUris.parseId(insertUri);
           // assertTrue(weatherRowId != -1);

            //A cursor is your primary interface to the query //********CUT AND PASTE

            Cursor weatherCursor = mContext.getContentResolver().query(WeatherContract2.WeatherEntry.CONTENT_URI,
                    null,
                    null,
                    null,
                    null
            );
            if (weatherCursor.moveToFirst()) {

                validateCursor(weatherCursor, weatherValues);
                //Get the value in each column by finding the appropiate
                //Remove code


            }
            else {

                fail("No weather data returned!");
            }
            weatherCursor.close();

            //PASTE IT HERE weatherCursor
             weatherCursor = mContext.getContentResolver().query(WeatherContract2.WeatherEntry.buildWeatherLocation(TEST_LOCATION),
                     null,
                     null,
                     null,
                     null
             );

            if(weatherCursor.moveToFirst()) {
                validateCursor(weatherCursor, weatherValues);

            } else {
                fail("Now weather data returned ");
            }

            weatherCursor.close();
            //PASTE IT HERE weatherCursor
            weatherCursor = mContext.getContentResolver().query(WeatherContract2.WeatherEntry.buildWeatherLocationWithStartDate(TEST_LOCATION, TEST_DATE),
                    null,
                    null,
                    null,
                    null
            );

            if(weatherCursor.moveToFirst()) {
                validateCursor(weatherCursor, weatherValues);

            } else {
                fail("Now weather data returned ");
            }
            //Cursor to the third method
            weatherCursor.close();
            //PASTE IT HERE weatherCursor
            weatherCursor = mContext.getContentResolver().query(WeatherContract2.WeatherEntry.buildWeatherLocationWithDate(TEST_LOCATION, TEST_DATE),
                    null,
                    null,
                    null,
                    null
            );

            if(weatherCursor.moveToFirst()) {
                validateCursor(weatherCursor, weatherValues);

            } else {
                fail("Now weather data returned ");
            }
        } else {
            //That's weird, it works on My machine
            fail("No values returned : (");
        }

    }


}
