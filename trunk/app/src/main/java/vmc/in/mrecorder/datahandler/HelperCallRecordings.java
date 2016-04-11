package vmc.in.mrecorder.datahandler;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.Model;

public class HelperCallRecordings extends SQLiteOpenHelper implements TAG {

    static String tbname = "CallRecords";
    static String dbname = "Database";
    static int ver = 6;
    SQLiteDatabase sld;
    Context c;
    public static final String TABLE_ALL ="all_calls";
    public static final String TABLE_INBOUND ="inbound";
    public static final String TABLE_OUTBOUND ="outbound";
    public static final String TABLE_MISSED ="missed";
    public static final String COLUMN_UID ="_id";
    public static final String COLUMN_CALLID = CALLID;
    public static final String COLUMN_BID = BID;
    public static final String COLUMN_EID = EID;
    public static final String COLUMN_CALLFROM = CALLFROM;
    public static final String COLUMN_CALLTO = CALLTO;
    public static final String COLUMN_EMPNAME = EMPNAME;
    public static final String COLUMN_CALLTYPEE = CALLTYPEE;
    public static final String COLUMN_NAME = NAME;
    public static final String COLUMN_STARTTIME = STARTTIME;
    public static final String COLUMN_ENDTIME = ENDTIME;
    public static final String COLUMN_FILENAME = FILENAME;


    private static final String CREATE_TABLE_ALL_CALLS = "CREATE TABLE " + TABLE_ALL + " (" +
            COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CALLID + " TEXT," +
            COLUMN_BID+ " TEXT," +
            COLUMN_EID + " TEXT," +
            COLUMN_CALLFROM + " TEXT," +
            COLUMN_CALLTO + " TEXT," +
            COLUMN_EMPNAME + " TEXT," +
            COLUMN_CALLTYPEE + " TEXT," +
            COLUMN_NAME+ " TEXT," +
            COLUMN_STARTTIME + " TEXT," +
            COLUMN_ENDTIME + " TEXT," +
            COLUMN_FILENAME + " TEXT" +
            ");";

     private static final String CREATE_TABLE_INBOUND = "CREATE TABLE " + TABLE_INBOUND + " (" +
            COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CALLID + " TEXT," +
            COLUMN_BID+ " TEXT," +
            COLUMN_EID + " TEXT," +
            COLUMN_CALLFROM + " TEXT," +
            COLUMN_CALLTO + " TEXT," +
            COLUMN_EMPNAME + " TEXT," +
            COLUMN_CALLTYPEE + " TEXT," +
            COLUMN_NAME+ " TEXT," +
            COLUMN_STARTTIME + " TEXT," +
            COLUMN_ENDTIME + " TEXT," +
            COLUMN_FILENAME + " TEXT" +
            ");";

     private static final String CREATE_TABLE_OUTBOUND = "CREATE TABLE " + TABLE_OUTBOUND + " (" +
            COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CALLID + " TEXT," +
            COLUMN_BID+ " TEXT," +
            COLUMN_EID + " TEXT," +
            COLUMN_CALLFROM + " TEXT," +
            COLUMN_CALLTO + " TEXT," +
            COLUMN_EMPNAME + " TEXT," +
            COLUMN_CALLTYPEE + " TEXT," +
            COLUMN_NAME+ " TEXT," +
            COLUMN_STARTTIME + " TEXT," +
            COLUMN_ENDTIME + " TEXT," +
            COLUMN_FILENAME + " TEXT" +
            ");";

     private static final String CREATE_TABLE_MISSED = "CREATE TABLE " + TABLE_MISSED + " (" +
            COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_CALLID + " TEXT," +
            COLUMN_BID+ " TEXT," +
            COLUMN_EID + " TEXT," +
            COLUMN_CALLFROM + " TEXT," +
            COLUMN_CALLTO + " TEXT," +
            COLUMN_EMPNAME + " TEXT," +
            COLUMN_CALLTYPEE + " TEXT," +
            COLUMN_NAME+ " TEXT," +
            COLUMN_STARTTIME + " TEXT," +
            COLUMN_ENDTIME + " TEXT," +
            COLUMN_FILENAME + " TEXT" +
            ");";







    private String[] columns = {ID, NUMBER, TIME, FILEPATH, CALLTYPE};
    private static final String CREATE_TABLE = "CREATE TABLE " + tbname + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            NUMBER + " TEXT," +
            TIME + " TEXT," +
            FILEPATH + " TEXT," +
            CALLTYPE + " TEXT" +
            ");";

    static int counter = 0;
    String cols[] = {ID, NUMBER, TIME, FILEPATH, CALLTYPE};

    public HelperCallRecordings(Context context) {
        super(context, dbname, null, ver);
        c = context;
        sld = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        if (counter == 0) {
            db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_TABLE_ALL_CALLS);
            db.execSQL(CREATE_TABLE_INBOUND);
            db.execSQL(CREATE_TABLE_OUTBOUND);
            db.execSQL(CREATE_TABLE_MISSED);
            Log.d("TABLE", "onCreate Called");
        }
        counter++;

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            db.execSQL(" DROP TABLE IF EXISTS" + tbname );
            db.execSQL(" DROP TABLE IF EXISTS" + TABLE_ALL );
            db.execSQL(" DROP TABLE IF EXISTS" + TABLE_INBOUND );
            db.execSQL(" DROP TABLE IF EXISTS" + TABLE_OUTBOUND );
            db.execSQL(" DROP TABLE IF EXISTS" + TABLE_MISSED );

//            db.execSQL(" DROP TABLE " + TABLE_ALL + " IF EXISTS;");
//            db.execSQL(" DROP TABLE " + TABLE_INBOUND + " IF EXISTS;");
//            db.execSQL(" DROP TABLE " + TABLE_OUTBOUND + " IF EXISTS;");
//            db.execSQL(" DROP TABLE " + TABLE_MISSED+ " IF EXISTS;");
        } catch (Exception e) {
            Log.d("database", e.getMessage());
        }
    }

    public void insert(String number, String time, String filepath, String calltype) {
        String sql = "INSERT INTO " + tbname + " VALUES (?,?,?,?,?);";
        SQLiteStatement statement = sld.compileStatement(sql);
        sld.beginTransaction();
        statement.clearBindings();
        statement.bindString(2, number);
        statement.bindString(3, time);
        statement.bindString(4, filepath);
        statement.bindString(5, calltype);
        statement.execute();
        sld.setTransactionSuccessful();
        sld.endTransaction();


    }

    public Cursor display() {
        Cursor c = sld.query(tbname, cols, null, null, null, null, "Time DESC");
        return c;
    }

    public ArrayList<Model> GetAllCalls() {
        ArrayList<Model> models = new ArrayList<>();
        Cursor cursor = sld.query(tbname, columns, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {

            do {
                Model model = new Model();
                model.setId(cursor.getString(cursor.getColumnIndex(ID)));
                model.setPhoneNumber(cursor.getString(cursor.getColumnIndex(NUMBER)));
                model.setTime(cursor.getString(cursor.getColumnIndex(TIME)));
                model.setFilePath(cursor.getString(cursor.getColumnIndex(FILEPATH)));
                model.setFile(new File(model.getFilePath()));
                model.setCallType(cursor.getString(cursor.getColumnIndex(CALLTYPE)));
                models.add(model);
            }
            while (cursor.moveToNext());
        }
        return models;
    }


    public Cursor refineDisplay(String calltype) {
        Cursor c = sld.query(tbname, cols, "CallType='" + calltype + "'", null, null, null, "Time DESC");
        return c;
    }

    public void clearAll() {
        sld.execSQL("delete from " + tbname + ";");
    }

    public void clearData(String s) {
        sld.execSQL("delete from " + tbname + " where Time='" + s + "';");

    }

    public void delete(String id) {
        sld.execSQL("delete from " + tbname + " where " + ID + "='" + id + "';");

    }

//    public void closeDatabase() {
//        sld.close();
//    }
}
