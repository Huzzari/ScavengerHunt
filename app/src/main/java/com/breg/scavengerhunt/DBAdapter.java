package com.breg.scavengerhunt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.Console;
// ------------------------------------ DBADapter.java ---------------------------------------------


public class DBAdapter {
    private static final String TAG = "DBAdapter";

    // DB Fields
    public static final String KEY_ROWID = "_id";
    public static final int COL_ROWID = 0;
    public static final String KEY_TITLE = "title";
    public static final String KEY_DESC = "desc";
    public static final String KEY_DATE = "date";
    public static final String KEY_ACTION = "action";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_TIME = "time";
    public static final String KEY_SCORE = "score";

    public static final String[] ALL_KEYS = new String[]{KEY_ROWID, KEY_TITLE, KEY_DESC, KEY_DATE, KEY_ACTION,
        KEY_LATITUDE, KEY_LONGITUDE, KEY_TIME, KEY_SCORE};

    // DB info: it's name, and the table we are using (just one).
    public static final String DATABASE_NAME = "Locations Database";
    public static final String DATABASE_TABLE = "locTable";

    // Track DB version if a new version of your app changes the format.
    public static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE_SQL =
            "create table " + DATABASE_TABLE
                    + " (" + KEY_ROWID + " integer primary key autoincrement, "
                    + KEY_TITLE + " text not null, "
                    + KEY_DESC + " integer not null, "
                    + KEY_DATE + " text not null, "
                    + KEY_ACTION + " text not null, "
                    + KEY_LATITUDE + " real not null, "
                    + KEY_LONGITUDE + " real not null, "
                    + KEY_TIME + " text not null, "
                    + KEY_SCORE + " integer not null "
                    + ");";

    // Context of application who uses us.
    private final Context context;

    private DatabaseHelper myDBHelper;
    private SQLiteDatabase db;

    /////////////////////////////////////////////////////////////////////
    //	Public methods:
    /////////////////////////////////////////////////////////////////////

    public DBAdapter(Context ctx) {
        this.context = ctx;
        myDBHelper = new DatabaseHelper(context);
    }

    // Open the database connection.
    public DBAdapter open() {
        db = myDBHelper.getWritableDatabase();
        return this;
    }

    // Close the database connection.
    public void close() {
        myDBHelper.close();
    }

    // Add a new set of values to the database.
    public long insertRow(String title, String desc, String date, String action, double latitude, double longitude, String time, int score) {
        // Create row's data:
        Log.d("Test", "Row Inserted locally.");
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_TITLE, title);
        initialValues.put(KEY_DESC, desc);
        initialValues.put(KEY_DATE, date);
        initialValues.put(KEY_ACTION, action);
        initialValues.put(KEY_LATITUDE, latitude);
        initialValues.put(KEY_LONGITUDE, longitude);
        initialValues.put(KEY_TIME, time);
        initialValues.put(KEY_SCORE, score);

        // Insert it into the database.
        return db.insert(DATABASE_TABLE, null, initialValues);
    }

    // Delete a row from the database, by rowId (primary key)
    public boolean deleteRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        return db.delete(DATABASE_TABLE, where, null) != 0;
    }

    public void deleteAll() {
        Cursor c = getAllRows();
        long rowId = c.getColumnIndexOrThrow(KEY_ROWID);
        if (c.moveToFirst()) {
            do {
                deleteRow(c.getLong((int) rowId));
            } while (c.moveToNext());
        }
        c.close();
    }

    // Return all data in the database.
    public Cursor getAllRows() {
        String where = null;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Get a specific row (by rowId)
    public Cursor getRow(long rowId) {
        String where = KEY_ROWID + "=" + rowId;
        Cursor c = 	db.query(true, DATABASE_TABLE, ALL_KEYS,
                where, null, null, null, null, null);
        if (c != null) {
            c.moveToFirst();
        }
        return c;
    }

    // Change an existing row to be equal to new data.
    public boolean updateRow(long rowId, String title, String desc, String date, String action, double latitude, double longitude, String time, int score) {
        String where = KEY_ROWID + "=" + rowId;

        // Create row's data:
        ContentValues newValues = new ContentValues();
        newValues.put(KEY_TITLE, title);
        newValues.put(KEY_DESC, desc);
        newValues.put(KEY_DATE, date);
        newValues.put(KEY_ACTION, action);
        newValues.put(KEY_LATITUDE, latitude);
        newValues.put(KEY_LONGITUDE, longitude);
        newValues.put(KEY_TIME, time);
        newValues.put(KEY_SCORE, score);

        // Insert it into the database.
        return db.update(DATABASE_TABLE, newValues, where, null) != 0;
    }

    /////////////////////////////////////////////////////////////////////
    //	Private Helper Classes:
    /////////////////////////////////////////////////////////////////////

    /**
     * Private class which handles database creation and upgrading.
     * Used to handle low-level database access.
     */
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase _db) {
            _db.execSQL(DATABASE_CREATE_SQL);
        }

        @Override
        public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading application's database from version " + oldVersion
                    + " to " + newVersion + ", which will destroy all old data!");

            // Destroy old database:
            _db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);

            // Recreate new database:
            onCreate(_db);
        }
    }
}
