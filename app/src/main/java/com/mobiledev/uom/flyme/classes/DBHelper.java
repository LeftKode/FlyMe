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
    private static final String ID = "_id";
    private static final String URL = "url";
    private static final String ORIGIN_LOC = "originLocation";
    private static final String DEST_LOC = "destinationLocation";
    private static final String DEP_DATE = "departureDate";
    private static final String ARR_DATE = "arrivalDate";
    private static final String NON_STOP = "nonStop";
    private static final String ADULT_NO = "adultsNumber";
    private static final String CHILD_NO = "childrenNumber";
    private static final String INFANT_NO = "infantNumber";

    private SQLiteDatabase myDB;

    public DBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createTableQuery = "CREATE TABLE " + TABLE_NAME +
                " ("+
                ID  + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                URL + " TEXT, " +
                ORIGIN_LOC + " TEXT, " +
                DEST_LOC + " TEXT, " +
                DEP_DATE + " TEXT, " +
                ARR_DATE + " TEXT, " +
                ADULT_NO + " INTEGER, " +
                CHILD_NO + " INTEGER, " +
                INFANT_NO + " INTEGER," +
                NON_STOP + " INTEGER" +
                ")";

        sqLiteDatabase.execSQL(createTableQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
    /*

    moveToLast για να παει στο τελος και μετα while("kati".moveToPrevious) για να παω στην αρχη
    "κατι".getString(1) μου δίνει την πρωτη στήλη της σειρας, το 2 τη 2η κτλ
    το getCount μας λεει ποσες υπάρχουν στην βαση

    */
    public boolean insertData(String url, String originLoc, String destLoc, String depDate, String arrDate,
                              int adultNo, int childNo, int infantNo,int nonStop){

        ContentValues values = new ContentValues();
        values.put(URL, url);
        values.put(ORIGIN_LOC, originLoc);
        values.put(DEST_LOC, destLoc);
        values.put(DEP_DATE, depDate);
        values.put(ARR_DATE, arrDate);
        values.put(ADULT_NO, adultNo);
        values.put(CHILD_NO, childNo);
        values.put(INFANT_NO, infantNo);
        values.put(NON_STOP, nonStop);

        //Με το return επιστρέφεται ο αριθμός της σειράς στην οποία προστέθηκαν τα πεδία
        long result = myDB.insert(TABLE_NAME, null, values);

        if (result == -1){
            return false;
        }else {
            return true;
        }
    }

    public Cursor getTableData(){
        myDB = this.getWritableDatabase();
        Cursor data = myDB.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + ID + " DESC", null);
        return data;
    }

    public long deleteRow(){
        myDB = this.getWritableDatabase();
        String where = ID + " = (SELECT MIN("+ID+") FROM "+TABLE_NAME+")";
        return myDB.delete(TABLE_NAME, where ,null);
    }

    public Cursor getTableRow(int id){
        myDB = this.getWritableDatabase();
        Cursor data = myDB.rawQuery("SELECT "+URL+ "," +ADULT_NO+ "," +CHILD_NO+ "," +INFANT_NO+ " FROM " + TABLE_NAME + " WHERE " + ID + " = " +id, null);
        return data;

    }

    public Cursor getID(){
        myDB = this.getWritableDatabase();
        Cursor data = myDB.rawQuery("SELECT " +ID+ " FROM " + TABLE_NAME + " WHERE " + ID + " = (SELECT MAX("+ID+") FROM "+TABLE_NAME+")",null);
        return data;

    }
}
