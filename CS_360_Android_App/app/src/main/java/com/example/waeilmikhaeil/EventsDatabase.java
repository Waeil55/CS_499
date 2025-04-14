package com.example.waeilmikhaeil;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import java.security.MessageDigest;
import java.util.ArrayList;

public class EventsDatabase extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "events.db";
    public static final int VERSION = 2;
    private static final String TAG = "EventsDatabase";

    // Event table columns
    public static final String EVENT_TABLE = "events";
    public static final String EVENT_ID = "id";
    public static final String EVENT_TITLE = "title";
    public static final String EVENT_DESCRIPTION = "description";
    public static final String EVENT_DATE_TIME = "date_time";
    public static final String EVENT_LOCATION = "location";
    public static final String EVENT_IS_ARCHIVED = "is_archived";

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
        db.execSQL("CREATE TABLE " + EVENT_TABLE + " (" + EVENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EVENT_TITLE + " TEXT, " + EVENT_DESCRIPTION + " TEXT, " + EVENT_DATE_TIME + " DATETIME, "
                + EVENT_LOCATION + " TEXT, " + EVENT_IS_ARCHIVED + " INTEGER DEFAULT 0)");
        db.execSQL("CREATE TABLE " + USER_TABLE + " (" + USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME + " TEXT UNIQUE, " + PASSWORD + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + EVENT_TABLE + " ADD COLUMN " + EVENT_IS_ARCHIVED + " INTEGER DEFAULT 0");
        }
    }

    public boolean insertEvent(Events event) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(EVENT_TITLE, event.getTitle());
            values.put(EVENT_DESCRIPTION, event.getDescription());
            values.put(EVENT_DATE_TIME, event.getDateTime());
            values.put(EVENT_LOCATION, event.getLocation());
            values.put(EVENT_IS_ARCHIVED, 0);
            long result = db.insert(EVENT_TABLE, null, values);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting event", e);
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public ArrayList<Events> viewEvents() {
        ArrayList<Events> eventsList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + EVENT_TABLE + " WHERE " + EVENT_IS_ARCHIVED + " = 0", null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(EVENT_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_DESCRIPTION));
                    String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_DATE_TIME));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_LOCATION));
                    eventsList.add(new Events(id, title, description, dateTime, location));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error viewing events", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return eventsList;
    }

    public ArrayList<Events> viewArchivedEvents() {
        ArrayList<Events> eventsList = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + EVENT_TABLE + " WHERE " + EVENT_IS_ARCHIVED + " = 1", null);
            if (cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(EVENT_ID));
                    String title = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_TITLE));
                    String description = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_DESCRIPTION));
                    String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_DATE_TIME));
                    String location = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_LOCATION));
                    eventsList.add(new Events(id, title, description, dateTime, location));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error viewing archived events", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return eventsList;
    }

    public boolean insertUser(String username, String password) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(USERNAME, username);
            values.put(PASSWORD, hashPassword(password));
            long result = db.insertWithOnConflict(USER_TABLE, null, values, SQLiteDatabase.CONFLICT_IGNORE);
            return result != -1;
        } catch (Exception e) {
            Log.e(TAG, "Error inserting user", e);
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean validateUser(String username, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT " + PASSWORD + " FROM " + USER_TABLE + " WHERE " + USERNAME + " = ?",
                    new String[]{username});
            if (cursor.moveToFirst()) {
                String storedPassword = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD));
                return storedPassword.equals(hashPassword(password));
            }
            return false;
        } catch (Exception e) {
            Log.e(TAG, "Error validating user", e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean deleteEvent(int eventId) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            int rowsAffected = db.delete(EVENT_TABLE, EVENT_ID + " = ?", new String[]{String.valueOf(eventId)});
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting event", e);
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public Events getEventById(int eventId) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.query(EVENT_TABLE, null, EVENT_ID + " = ?",
                    new String[]{String.valueOf(eventId)}, null, null, null);
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(EVENT_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_DESCRIPTION));
                String dateTime = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_DATE_TIME));
                String location = cursor.getString(cursor.getColumnIndexOrThrow(EVENT_LOCATION));
                return new Events(id, title, description, dateTime, location);
            }
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error getting event by ID", e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean updateEvent(Events event) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(EVENT_TITLE, event.getTitle());
            values.put(EVENT_DESCRIPTION, event.getDescription());
            values.put(EVENT_DATE_TIME, event.getDateTime());
            values.put(EVENT_LOCATION, event.getLocation());
            int rowsAffected = db.update(EVENT_TABLE, values, EVENT_ID + " = ?",
                    new String[]{String.valueOf(event.getId())});
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating event", e);
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean archiveEvent(int eventId) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(EVENT_IS_ARCHIVED, 1);
            int rowsAffected = db.update(EVENT_TABLE, values, EVENT_ID + " = ?",
                    new String[]{String.valueOf(eventId)});
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error archiving event", e);
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean userExists(String username) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE " + USERNAME + " = ?",
                    new String[]{username});
            return cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error checking user existence", e);
            return false;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(PASSWORD, hashPassword(newPassword));
            int rowsAffected = db.update(USER_TABLE, values, USERNAME + " = ?",
                    new String[]{username});
            return rowsAffected > 0;
        } catch (Exception e) {
            Log.e(TAG, "Error updating password", e);
            return false;
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            Log.e(TAG, "Error hashing password", e);
            return password; // Fallback (not ideal, but maintains compatibility)
        }
    }
}