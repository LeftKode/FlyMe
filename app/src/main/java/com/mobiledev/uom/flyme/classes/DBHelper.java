package com.mobiledev.uom.flyme.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 23/12/2016.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "mydb.db";
    private static int VERSION = 1;

    private static final String TABLE_NAME = "searchList";
    private static final String URL = "url";
    private static final String ORIGIN_LOC = "originLocation";
    private static final String DEST_LOC = "destinationLocation";
    private static final String DEP_DATE = "departureDate";
    private static final String ARR_DATE = "arrivalDate";
    private static final String NON_STOP = "nonStop";
    private static final String ADULT_NO = "adultsNumber";
    private static final String CHILD_NO = "childernNumber";
    private static final String INFANT_NO = "infantNumber";

    private SQLiteDatabase myDB;

    public DBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTableQuery = "CREATE TABLE" + TABLE_NAME +
                ")"+
                URL + "TEXT NOT NULL" +
                ORIGIN_LOC + "TEXT NOT NULL" +
                DEST_LOC + "TEXT NOT NULL" +
                DEP_DATE + "TEXT NOT NULL" +
                ARR_DATE + "TEXT NOT NULL" +
                NON_STOP + "INTEGER NOT NULL" +
                ADULT_NO + "INTEGER NOT NULL" +
                CHILD_NO + "INTEGER NOT NULL" +
                INFANT_NO + "INTEGER NOT NULL" +
                ")";

        sqLiteDatabase.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void openDB(){
        myDB = getWritableDatabase();
    }

    public void closeDB(){
        if(myDB != null && myDB.isOpen()) {
            myDB.close();
        }

    }

    /*

    moveToLast για να παει στο τελος και μετα while("kati".moveToPrevious) για να παω στην αρχη
    "κατι".getString(1) μου δίνει την πρωτη στήλη της σειρας, το 2 τη 2η κτλ
    το getCount μας λεει ποσες υπάρχουν στην βαση

    */
    public long insertData(String url, String originLoc, String destLoc, String depDate, String arrDate,
                             int nonStop, int adultNo, int childNo, int infantNo){

        ContentValues values = new ContentValues();
        values.put(URL, url);
        values.put(ORIGIN_LOC, originLoc);
        values.put(DEST_LOC, destLoc);
        values.put(DEP_DATE, depDate);
        values.put(ARR_DATE, arrDate);
        values.put(NON_STOP, nonStop);
        values.put(ADULT_NO, adultNo);
        values.put(CHILD_NO, childNo);
        values.put(INFANT_NO, infantNo);

        //Με το return επιστρέφεται ο αριθμός της σειράς στην οποία προστέθηκαν τα πεδία
        return myDB.insert(TABLE_NAME, null, values);
    }

    public Cursor getTableData(){
        Cursor data = myDB.rawQuery("SELECT * FROM" + TABLE_NAME, null);
        return data;
    }
}
