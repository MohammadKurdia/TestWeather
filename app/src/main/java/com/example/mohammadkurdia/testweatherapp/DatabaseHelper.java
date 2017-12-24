package com.example.mohammadkurdia.testweatherapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Mohammad Kurdia on 12/23/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public static String DATABASE_NAME = "City_Database";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_Cities = "Favorite_Cities";
    private static final String KEY_ID = "id";
    private static final String CITY_KEY = "City_Key";


    //Creating a Table and Key Column and City Column

    private static final String CREATE_TABLE_Cities = "CREATE TABLE "
            + TABLE_Cities + "(" + KEY_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT," +CITY_KEY + " TEXT );";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_Cities);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS '" +TABLE_Cities + "'");
        onCreate(db);
    }

    //To add a City to the Table

    public long addFavoriteCity(String cityName) {
        SQLiteDatabase db = this.getWritableDatabase();
        // Creating content values
        ContentValues values = new ContentValues();
        values.put(CITY_KEY,cityName);
        // insert row in cities table
        long insert = db.insert(TABLE_Cities, null, values);

        return insert;
    }

    //To delete a City from the Table
    public void deleteFavoriteCity(String cityName) {
      SQLiteDatabase  db = this.getReadableDatabase();
        String cityNameValue[] = {cityName};
      db.delete(TABLE_Cities,CITY_KEY+"=?",cityNameValue);
    }
    //To Read all the Cities in the Table
    public ArrayList<String> getAllCitiesList() {
        ArrayList<String> citiesArrayList = new ArrayList<String>();
        String city="";
        String selectQuery = "SELECT  * FROM " + TABLE_Cities;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                city = c.getString(c.getColumnIndex(CITY_KEY));
                citiesArrayList.add(city);
            } while (c.moveToNext());
        }
        return citiesArrayList;
    }
}