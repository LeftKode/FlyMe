package com.mobiledev.uom.flyme.classes;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//Κατασκευάζει και ελέγχει τη βάση
public class DBHelper extends SQLiteOpenHelper {

    private static final String DBNAME = "mydb.db";
    private static int VERSION = 1;

    private static final String TABLE_NAME = "searchList";
    private static final String ID = "_id";
    private static final String URL = "url";
    private static final String ORIGIN_LOC = "originLocation";
    private static final String DEST_LOC = "destinationLocation";
    private static final String DEP_DATE = "departureDate";
    private static final String ARR_DATE = "returnDate";
    private static final String NON_STOP = "nonStop";
    private static final String ADULT_NO = "adultsNumber";
    private static final String CHILD_NO = "childrenNumber";
    private static final String INFANT_NO = "infantNumber";

    private SQLiteDatabase myDB;

    public DBHelper(Context context) {
        super(context, DBNAME, null, VERSION);
    }

    //Δημιουργία της βάσης
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

    //Μέθοδος για εισαγωγή δεδομένων στη βάση
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

    //Μέθοδος για επιστροφή των δεδομένων στη βάση
    public Cursor getTableData(){
        myDB = this.getWritableDatabase();
        Cursor data = myDB.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + ID + " DESC", null);
        return data;
    }

    //Μέθοδος για διαγραφή της τελευταίας σειράς από τη βάση
    //άρα της πιο παλιάς αναζήτησης που έκανε ο χρήστης
    public long deleteRow(){
        myDB = this.getWritableDatabase();
        String where = ID + " = (SELECT MIN("+ID+") FROM "+TABLE_NAME+")";
        return myDB.delete(TABLE_NAME, where ,null);
    }

    //Μέθοδος για επιστροφή μια συγκεκριμένης σειράς από τη βάση ανάλογα με το id που θα ζητήσουμε
    public Cursor getTableRow(int id){
        myDB = this.getWritableDatabase();
        Cursor data = myDB.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + ID + " = " +id, null);
        return data;

    }

    //Επιστρέφει την τιμή του μεγαλύτερου id που υπάρχει κάποια στιγμή στη βάση
    public Cursor getID(){
        myDB = this.getWritableDatabase();
        Cursor data = myDB.rawQuery("SELECT " +ID+ " FROM " + TABLE_NAME + " WHERE " + ID +
                " = (SELECT MAX("+ID+") FROM "+TABLE_NAME+")",null);
        return data;

    }
}
