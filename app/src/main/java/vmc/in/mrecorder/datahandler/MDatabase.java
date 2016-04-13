package vmc.in.mrecorder.datahandler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.entity.Model;

/**
 * Created by gousebabjan on 30/3/16.
 */
public class MDatabase implements TAG {
    private CallHelper mHelper;
    private SQLiteDatabase mDatabase;
    public static final int CALLRECORDS = 4;
    public static final int ALL = 0;
    public static final int INBOUND = 1;
    public static final int OUTBOUND = 2;
    public static final int MISSED = 3;

    public MDatabase(Context context) {
        mHelper = new CallHelper(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void insertCallRecords(int table, ArrayList<CallData> listCalls, boolean clearPrevious) {
        if (clearPrevious) {
            deleteCallRecords(table);
        }
        String sql = "INSERT INTO " + (table == ALL ? CallHelper.TABLE_ALL : table == INBOUND ? CallHelper.TABLE_INBOUND : table == OUTBOUND ? CallHelper.TABLE_OUTBOUND : CallHelper.TABLE_MISSED) + " VALUES (?,?,?,?,?,?,?,?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        for (int i = 0; i < listCalls.size(); i++) {
            CallData calldata = listCalls.get(i);
            statement.clearBindings();
            statement.bindString(2, calldata.getCallid());
            statement.bindString(3, calldata.getBid());
            statement.bindString(4, calldata.getEid());
            statement.bindString(5, calldata.getCallfrom());
            statement.bindString(6, calldata.getCallto());
            statement.bindString(7, calldata.getEmpname());
            statement.bindString(8, calldata.getCalltype());
            statement.bindString(9, calldata.getName());
            statement.bindString(10, calldata.getStarttime());
            statement.bindString(11, calldata.getEndtime());
            statement.bindString(12, calldata.getFilename());
            statement.execute();
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    public void delete(String id) {
        mDatabase.execSQL("delete from " + CallHelper.TABLE_CALLRECORDS + " where " + ID + "='" + id + "';");

    }

    //
//
    public void deleteCallRecords(int table) {
        mDatabase.delete(table == ALL ? CallHelper.TABLE_ALL : table == INBOUND ? CallHelper.TABLE_INBOUND : table == OUTBOUND ? CallHelper.TABLE_OUTBOUND : table == MISSED ? CallHelper.TABLE_MISSED : CallHelper.TABLE_CALLRECORDS, null, null);

    }

    public void insert(String number, String time, String filepath, String calltype) {
        String sql = "INSERT INTO " + CallHelper.TABLE_CALLRECORDS + " VALUES (?,?,?,?,?);";
        SQLiteStatement statement = mDatabase.compileStatement(sql);
        mDatabase.beginTransaction();
        statement.clearBindings();
        statement.bindString(2, number);
        statement.bindString(3, time);
        statement.bindString(4, filepath);
        statement.bindString(5, calltype);
        statement.execute();
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();


    }


    public void DeleteAllData() {
        deleteCallRecords(ALL);
        deleteCallRecords(INBOUND);
        deleteCallRecords(OUTBOUND);
        deleteCallRecords(MISSED);
        deleteCallRecords(CALLRECORDS);

    }

    public ArrayList<Model> getAllOfflineCalls() {
        ArrayList<Model> models = new ArrayList<>();

        String[] columns = {CallHelper.ID, CallHelper.NUMBER, CallHelper.TIME, CallHelper.FILEPATH, CallHelper.CALLTYPE};
        Cursor cursor = mDatabase.query(CallHelper.TABLE_CALLRECORDS, columns, null, null, null, null, null);
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

    public ArrayList<CallData> getAllCalls(int table) {
        ArrayList<CallData> listCalls = new ArrayList<>();

        String[] columns = {CallHelper.COLUMN_UID,
                CallHelper.COLUMN_CALLID,
                CallHelper.COLUMN_BID,
                CallHelper.COLUMN_EID,
                CallHelper.COLUMN_CALLFROM,
                CallHelper.COLUMN_CALLTO,
                CallHelper.COLUMN_EMPNAME,
                CallHelper.COLUMN_CALLTYPEE,
                CallHelper.COLUMN_NAME,
                CallHelper.COLUMN_STARTTIME,
                CallHelper.COLUMN_ENDTIME,
                CallHelper.COLUMN_FILENAME

        };
        Cursor cursor = mDatabase.query((table == ALL ? CallHelper.TABLE_ALL : table == INBOUND ? CallHelper.TABLE_INBOUND : table == OUTBOUND ? CallHelper.TABLE_OUTBOUND : CallHelper.TABLE_MISSED), null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CallData calldata = new CallData();
                calldata.setCallid(cursor.getString(cursor.getColumnIndex(CallHelper.COLUMN_CALLID)));
                calldata.setBid(cursor.getString(cursor.getColumnIndex(CallHelper.COLUMN_BID)));
                calldata.setEid(cursor.getString(cursor.getColumnIndex(CallHelper.COLUMN_EID)));
                calldata.setCallfrom(cursor.getString(cursor.getColumnIndex(CallHelper.COLUMN_CALLFROM)));
                calldata.setCallto(cursor.getString(cursor.getColumnIndex(CallHelper.COLUMN_CALLTO)));
                calldata.setEmpname(cursor.getString(cursor.getColumnIndex(CallHelper.COLUMN_EMPNAME)));
                calldata.setCalltype(cursor.getString(cursor.getColumnIndex(CallHelper.COLUMN_CALLTYPEE)));
                calldata.setName(cursor.getString(cursor.getColumnIndex(CallHelper.COLUMN_NAME)));
                calldata.setStarttime(cursor.getString(cursor.getColumnIndex(CallHelper.COLUMN_STARTTIME)));
                calldata.setEndtime(cursor.getString(cursor.getColumnIndex(CallHelper.COLUMN_ENDTIME)));
                calldata.setFilename(cursor.getString(cursor.getColumnIndex(CallHelper.COLUMN_FILENAME)));

                Date startTime = null;
                Date endTime = null;
                SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
                try {
                    startTime = sdf.parse(calldata.getStarttime());
                    endTime = sdf.parse(calldata.getEndtime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                calldata.setStartTime(startTime);
                calldata.setEndTime(endTime);

                listCalls.add(calldata);
            }
            while (cursor.moveToNext());
        }
        return listCalls;
    }


    private static class CallHelper extends SQLiteOpenHelper implements TAG {
        private static final String DB_NAME = "Database";
        private static final int DB_VERSION = 12;

        public static final String TABLE_CALLRECORDS = "CallRecords";
        public static final String TABLE_ALL = "all_calls";
        public static final String TABLE_INBOUND = "inbound";
        public static final String TABLE_OUTBOUND = "outbound";
        public static final String TABLE_MISSED = "missed";
        public static final String COLUMN_UID = "_id";
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
                COLUMN_BID + " TEXT," +
                COLUMN_EID + " TEXT," +
                COLUMN_CALLFROM + " TEXT," +
                COLUMN_CALLTO + " TEXT," +
                COLUMN_EMPNAME + " TEXT," +
                COLUMN_CALLTYPEE + " TEXT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_STARTTIME + " TEXT," +
                COLUMN_ENDTIME + " TEXT," +
                COLUMN_FILENAME + " TEXT" +
                ");";

        private static final String CREATE_TABLE_INBOUND = "CREATE TABLE " + TABLE_INBOUND + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CALLID + " TEXT," +
                COLUMN_BID + " TEXT," +
                COLUMN_EID + " TEXT," +
                COLUMN_CALLFROM + " TEXT," +
                COLUMN_CALLTO + " TEXT," +
                COLUMN_EMPNAME + " TEXT," +
                COLUMN_CALLTYPEE + " TEXT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_STARTTIME + " TEXT," +
                COLUMN_ENDTIME + " TEXT," +
                COLUMN_FILENAME + " TEXT" +
                ");";

        private static final String CREATE_TABLE_OUTBOUND = "CREATE TABLE " + TABLE_OUTBOUND + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CALLID + " TEXT," +
                COLUMN_BID + " TEXT," +
                COLUMN_EID + " TEXT," +
                COLUMN_CALLFROM + " TEXT," +
                COLUMN_CALLTO + " TEXT," +
                COLUMN_EMPNAME + " TEXT," +
                COLUMN_CALLTYPEE + " TEXT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_STARTTIME + " TEXT," +
                COLUMN_ENDTIME + " TEXT," +
                COLUMN_FILENAME + " TEXT" +
                ");";

        private static final String CREATE_TABLE_MISSED = "CREATE TABLE " + TABLE_MISSED + " (" +
                COLUMN_UID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_CALLID + " TEXT," +
                COLUMN_BID + " TEXT," +
                COLUMN_EID + " TEXT," +
                COLUMN_CALLFROM + " TEXT," +
                COLUMN_CALLTO + " TEXT," +
                COLUMN_EMPNAME + " TEXT," +
                COLUMN_CALLTYPEE + " TEXT," +
                COLUMN_NAME + " TEXT," +
                COLUMN_STARTTIME + " TEXT," +
                COLUMN_ENDTIME + " TEXT," +
                COLUMN_FILENAME + " TEXT" +
                ");";
        private final Context mContext;
        private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_CALLRECORDS + " (" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                NUMBER + " TEXT," +
                TIME + " TEXT," +
                FILEPATH + " TEXT," +
                CALLTYPE + " TEXT" +
                ");";

        public CallHelper(Context context) {
            super(context, DB_NAME, null, DB_VERSION);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            db.execSQL(CREATE_TABLE);
            db.execSQL(CREATE_TABLE_ALL_CALLS);
            db.execSQL(CREATE_TABLE_INBOUND);
            db.execSQL(CREATE_TABLE_OUTBOUND);
            db.execSQL(CREATE_TABLE_MISSED);
            Log.d("database", "onCreate Called");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                db.execSQL(" DROP TABLE IF EXISTS " + TABLE_CALLRECORDS);
                db.execSQL(" DROP TABLE IF EXISTS " + TABLE_ALL);
                db.execSQL(" DROP TABLE IF EXISTS " + TABLE_INBOUND);
                db.execSQL(" DROP TABLE IF EXISTS " + TABLE_OUTBOUND);
                db.execSQL(" DROP TABLE IF EXISTS " + TABLE_MISSED);
                Log.d("database", "table dropped");
                onCreate(db);
            } catch (Exception e) {
                Log.d("database", e.getMessage());
            }
        }


    }


}
