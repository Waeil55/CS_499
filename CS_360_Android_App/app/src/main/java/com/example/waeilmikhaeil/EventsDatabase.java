package com.example.waeilmikhaeil;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.util.ArrayList;

public class EventsDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "events.db";
    public static final int VERSION = 1;
    private static final String TAG = "EventsDatabase";

    // Event table columns
    public static final String EVENT_ID = "id";
    public static final String EVENT_NAME = "event";
    public static final String EVENT_TITLE = "title";
    public static final String EVENT_DESCRIPTION = "description";
    public static final String EVENT_DATE_TIME = "date_time";
    public static final String EVENT_LOCATION = "location";

    // User table columns
    public static final String USER_TABLE = "users";
    public static final String USER_ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";

    public EventsDatabase(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Create events table
            db.execSQL("CREATE TABLE " + EVENT_NAME + " (" + EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + EVENT_TITLE + " TEXT, " + EVENT_DESCRIPTION + " TEXT, " + EVENT_DATE_TIME + " DATETIME, "
                    + EVENT_LOCATION + " TEXT)");

            // Create users table
            db.execSQL("CREATE TABLE " + USER_TABLE + " (" + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + USERNAME + " TEXT UNIQUE, " + PASSWORD + " TEXT)");
        } catch (SQLiteException e) {
            throw new RuntimeException("Failed to create database: " + e.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + EVENT_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        onCreate(db);
    }

    // Insert event into database

    // Retrieve events from database
    public ArrayList<Events> viewEvents() throws SQLiteException {
        ArrayList<Events> eventsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + EVENT_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String description = cursor.getString(2);
                String dateTime = cursor.getString(3);
                String location = cursor.getString(4);
                eventsList.add(new Events(id, title, description, dateTime, location));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return eventsList;
    }

    // Insert user into database
    public boolean insertUser(String username, String password) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);

        long result = db.insertWithOnConflict(USER_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
        return result != -1;
    }

    // Validate user login
    public boolean validateUser(String username, String password) throws SQLiteException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE " + USERNAME + " = ? AND " + PASSWORD + " = ?", new String[]{username, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    public boolean deleteEvent(int eventId) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(EVENT_NAME, EVENT_ID + " = ?", new String[]{String.valueOf(eventId)}) > 0;
    }



    public Events getEventById(int eventId) throws SQLiteException {
        SQLiteDatabase db = this.getReadableDatabase();
        Events event = null;

        try {
            Cursor cursor = db.query(EVENT_NAME, null, EVENT_ID + " = ?",
                    new String[]{String.valueOf(eventId)}, null, null, null);

            if (cursor.moveToFirst()) {
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(EVENT_TITLE));
                @SuppressLint("Range") String description = cursor.getString(cursor.getColumnIndex(EVENT_DESCRIPTION));
                @SuppressLint("Range") String dateTime = cursor.getString(cursor.getColumnIndex(EVENT_DATE_TIME));
                @SuppressLint("Range") String location = cursor.getString(cursor.getColumnIndex(EVENT_LOCATION));
                event = new Events(eventId, title, description, dateTime, location);
                Log.d(TAG, "Event found: " + event.getTitle());
            } else {
                Log.e(TAG, "No event found with ID: " + eventId);
            }
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting event by ID: " + eventId, e);
        }

        return event;
    }

    public boolean updateEvent(Events event) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVENT_TITLE, event.getTitle());
        values.put(EVENT_DESCRIPTION, event.getDescription());
        values.put(EVENT_DATE_TIME, event.getDateTime());
        values.put(EVENT_LOCATION, event.getLocation());

        try {
            int rowsAffected = db.update(EVENT_NAME, values, EVENT_ID + " = ?",
                    new String[]{String.valueOf(event.getId())});
            Log.d(TAG, "Rows affected by update: " + rowsAffected);
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating event: " + event.getId(), e);
            return false;
        }
    }
    public boolean insertEvent(Events event) throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(EVENT_TITLE, event.getTitle());
        values.put(EVENT_DESCRIPTION, event.getDescription());
        values.put(EVENT_DATE_TIME, event.getDateTime());
        values.put(EVENT_LOCATION, event.getLocation());

        Log.d(TAG, "Inserting event: " + event.getTitle());

        long result = db.insert(EVENT_NAME, null, values);

        if (result == -1) {
            Log.e(TAG, "Failed to insert event");
        } else {
            Log.d(TAG, "Event inserted successfully with id: " + result);
        }

        return result != -1;
    }





}