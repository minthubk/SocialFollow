package com.cathedrale.database;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class Database {

    public static final String DATABASE_NAME = "cathedraledatabase";
    public static final int DATABASE_VERSION = 1;

    Context context;
    SQLiteDatabase db;
    DBHelper dbHelper;


    private static final String CREATE_TABLE_SCORE = "CREATE TABLE "
            + "scoretable" + " (" + "Id"+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "score" + " TEXT NOT NULL, " + "week" + " TEXT NOT NULL,"
            + "UNIQUE(Id,score,week) ON CONFLICT REPLACE" + ")";

    private static final String CREATE_TABLE_TREATMENT_SCORE = "CREATE TABLE "
            + "TreatmentScoretable" + " (" + "Id"+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "score" + " TEXT NOT NULL, " + "week" + " TEXT NOT NULL,"
            + "UNIQUE(Id,score,week) ON CONFLICT REPLACE" + ")";

    private static final String CREATE_TABLE_TOTAL_SCORE = "CREATE TABLE "
            + "TotalScoretable" + " (" + "Id"+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "score" + " TEXT NOT NULL, " + "week" + " TEXT NOT NULL,"
            + "UNIQUE(Id,score,week) ON CONFLICT REPLACE" + ")";

    private static final String CREATE_TABLE_WEEKLY_ANSWERS = "CREATE TABLE "
            + "WeeklyAnswers" + " (" + "Id"+ " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "answers" + " TEXT NOT NULL, " + "week" + " TEXT NOT NULL,"
            + "UNIQUE(Id,answers,week) ON CONFLICT REPLACE" + ")";

    public Database(Context c) {
        // TODO Auto-generated constructor stub
        this.context = c;
        dbHelper = new DBHelper(context);
    }

    public Database open() {

        db = dbHelper.getWritableDatabase();
        db = dbHelper.getReadableDatabase();

        System.out.println("database opened....");
        return this;
    }

    void close() {
        dbHelper.close();
        System.out.println("database closed......");
    }




    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub

            db.execSQL(CREATE_TABLE_SCORE);
            db.execSQL(CREATE_TABLE_TREATMENT_SCORE);
            db.execSQL(CREATE_TABLE_TOTAL_SCORE);
            db.execSQL(CREATE_TABLE_WEEKLY_ANSWERS);

            System.out.println("table created....");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            // db.execSQL(CREATE_TABLE_TODOO);

            db.execSQL("DROP TABLE IF EXISTS "+CREATE_TABLE_SCORE);
            db.execSQL("DROP TABLE IF EXISTS "+CREATE_TABLE_TREATMENT_SCORE);
            db.execSQL("DROP TABLE IF EXISTS "+CREATE_TABLE_TOTAL_SCORE);
            db.execSQL("DROP TABLE IF EXISTS "+CREATE_TABLE_WEEKLY_ANSWERS);
            onCreate(db);
        }
    }

    public long insertSypmtomScore(int score, String week) {
        // TODO Auto-generated method stub
        ContentValues con = new ContentValues();
        con.put("score", score);
        con.put("week", week);
        return db.insert("scoretable", null, con);
    }

    public ArrayList<HashMap<String, String>> getScore() {

        ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String, String>>();
        String score, week;
        String selectQuery = " SELECT  * from scoretable";
        Database dbh = new Database(context);
        dbh.open();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                score = cursor.getString(1);
                week = cursor.getString(2);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("score", score);
                map.put("week", week);
                values.add(map);
            } while (cursor.moveToNext());
        }
        dbh.close();
        return values;
    }
    public long insertTreatmentScore(int score, String week) {
        ContentValues con = new ContentValues();
        con.put("score", score);
        con.put("week", week);
        return db.insert("TreatmentScoretable", null, con);
    }

    public ArrayList<HashMap<String, String>> getTreatmentScore() {

        ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String, String>>();
        String score, week;
        String selectQuery = " SELECT  * from TreatmentScoretable";
        Database dbh = new Database(context);
        dbh.open();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                score = cursor.getString(1);
                week = cursor.getString(2);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("score", score);
                map.put("week", week);
                values.add(map);
            } while (cursor.moveToNext());
        }
        dbh.close();
        return values;
    }

    public long insertTotalScore(int totalsum, String week) {
        ContentValues con = new ContentValues();
        con.put("score", totalsum);
        con.put("week", week);
        return db.insert("TotalScoretable", null, con);
    }

    public ArrayList<HashMap<String, String>> getTotalScore() {

        ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String, String>>();
        String score, week;
        String selectQuery = " SELECT  * from TotalScoretable";
        Database dbh = new Database(context);
        dbh.open();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                score = cursor.getString(1);
                week = cursor.getString(2);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("score", score);
                map.put("week", week);
                values.add(map);
            } while (cursor.moveToNext());
        }
        dbh.close();
        return values;
    }

    public long insertWeeklyAnswers(String jsonfinalStr, String Week) {
        ContentValues con = new ContentValues();
        con.put("answers", jsonfinalStr);
        con.put("week", Week);
        return db.insert("WeeklyAnswers", null, con);
    }

    public ArrayList<HashMap<String, String>> getWeeklyAnswers() {

        ArrayList<HashMap<String, String>> values = new ArrayList<HashMap<String, String>>();
        String answers,week;
        String selectQuery = " SELECT  * from WeeklyAnswers";
        Database dbh = new Database(context);
        dbh.open();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            do {
                answers = cursor.getString(1);
                week = cursor.getString(2);

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("answers", answers);
                map.put("week", week);
                values.add(map);
            } while (cursor.moveToNext());
        }
        dbh.close();
        return values;
    }
}
