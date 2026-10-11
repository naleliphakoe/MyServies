package com.example.myservies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class RequestDB  extends SQLiteOpenHelper {
        private static final String DATABASE_NAME = "myservices.db";
        private static final int DATABASE_VERSION = 1;

        public RequestDB(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE requests (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            "reference TEXT UNIQUE, " +
                            "category TEXT, " +
                            "description TEXT, " +
                            "location TEXT, " +
                            "status TEXT, " +
                            "createdAt TEXT)"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db,
                              int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS requests");
            onCreate(db);
        }

        public String addRequest(String category,
                                 String description,
                                 String location) {

            SQLiteDatabase db = getWritableDatabase();

            String reference = "MS-" +
                    UUID.randomUUID().toString()
                            .substring(0, 8).toUpperCase(Locale.ROOT);

            String date = new SimpleDateFormat(
                    "yyyy-MM-dd HH:mm",
                    Locale.getDefault()
            ).format(new Date());

            ContentValues values = new ContentValues();
            values.put("reference", reference);
            values.put("category", category);
            values.put("description", description);
            values.put("location", location);
            values.put("status", "Submitted");
            values.put("createdAt", date);

            long result = db.insert("requests", null, values);

            if (result == -1) {
                return null;
            }

            return reference;
        }

        public Cursor getAllRequests() {
            SQLiteDatabase db = getReadableDatabase();

            return db.rawQuery(
                    "SELECT * FROM requests ORDER BY id DESC",
                    null
            );
        }

        public Cursor findByReference(String reference) {
            SQLiteDatabase db = getReadableDatabase();

            return db.rawQuery(
                    "SELECT * FROM requests WHERE reference = ?",
                    new String[]{reference.trim().toUpperCase(Locale.ROOT)}
            );
        }
    }


