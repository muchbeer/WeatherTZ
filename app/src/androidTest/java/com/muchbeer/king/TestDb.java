package com.muchbeer.king;

/**
 * Created by muchbeer on 11/30/2014.
 */

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.muchbeer.king.data.WeatherContract2;
import com.muchbeer.king.data.WeatherContract2.LocationEntry;
import com.muchbeer.king.data.WeatherDBHelper;

import java.util.Map;
import java.util.Set;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();
    static final String TEST_LOCATION = "99705";
    static final String TEST_DATE = "20141205";

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDBHelper.DATABASE_NAME);
        SQLiteDatabase db = new WeatherDBHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());
        db.close();
    }

    static public String TEST_CITY_NAME = "North Pole";
    ContentValues getLocationContentValues() {

                String testLocationSetting = "99705";
        double testLatitude = 64.772;
        double testLongitude = -147.355;

        //Create dummy data
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_CITY_NAME, TEST_CITY_NAME);
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
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
    public void testInsertReadDb() {


        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDBHelper dbHelper = new WeatherDBHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Call the function back
        ContentValues values = getLocationContentValues();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, values);
        //verify we a got a row back
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);


        //Specify which columns you want ******Remove1********


        //A cursor in
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,
                null,
                null,
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


        long weatherRowId;
            weatherRowId = db.insert(WeatherContract2.WeatherEntry.TABLE_NAME, null, weatherValues);
            assertTrue(weatherRowId != -1);

            //A cursor is your primary interface to the query
            Cursor weatherCursor = db.query(
                    WeatherContract2.WeatherEntry.TABLE_NAME,
                    null,
                    null,
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

            } else {
            //That's weird, it works on My machine
            fail("No values returned : (");
        }

    }
}
