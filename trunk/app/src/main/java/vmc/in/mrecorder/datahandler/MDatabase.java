package vmc.in.mrecorder.datahandler;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.CallData;

/**
 * Created by gousebabjan on 30/3/16.
 */
public class MDatabase implements TAG {
    private HelperCallRecordings mHelper;
    private SQLiteDatabase mDatabase;
    public static final int ALL = 0;
    public static final int INBOUND = 1;
    public static final int OUTBOUND = 2;
    public static final int MISSED = 3;

    public MDatabase(Context context) {
        mHelper = new HelperCallRecordings(context);
        mDatabase = mHelper.getWritableDatabase();
    }

    public void insertCallRecords(int table, ArrayList<CallData> listCalls, boolean clearPrevious) {
        if (clearPrevious) {
            deleteCallRecords(table);
        }
        String sql = "INSERT INTO " + (table == ALL ? HelperCallRecordings.TABLE_ALL : table == INBOUND ? HelperCallRecordings.TABLE_INBOUND : table == OUTBOUND ? HelperCallRecordings.TABLE_OUTBOUND : HelperCallRecordings.TABLE_MISSED) + " VALUES (?,?,?,?,?,?,?,?,?,?,?);";
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
            statement.execute();
        }
        mDatabase.setTransactionSuccessful();
        mDatabase.endTransaction();
    }

    //
//
    public void deleteCallRecords(int table) {
        mDatabase.delete(table == ALL ? HelperCallRecordings.TABLE_ALL : table == INBOUND ? HelperCallRecordings.TABLE_INBOUND : table == OUTBOUND ? HelperCallRecordings.TABLE_OUTBOUND : HelperCallRecordings.TABLE_MISSED, null, null);

    }

    public void DeleteAllData() {
        deleteCallRecords(ALL);
        deleteCallRecords(INBOUND);
        deleteCallRecords(OUTBOUND);
        deleteCallRecords(MISSED);
    }

    public ArrayList<CallData> getAllCalls(int table) {
        ArrayList<CallData> listCalls = new ArrayList<>();

        String[] columns = {HelperCallRecordings.COLUMN_UID,
                HelperCallRecordings.COLUMN_CALLID,
                HelperCallRecordings.COLUMN_BID,
                HelperCallRecordings.COLUMN_EID,
                HelperCallRecordings.COLUMN_CALLFROM,
                HelperCallRecordings.COLUMN_CALLTO,
                HelperCallRecordings.COLUMN_EMPNAME,
                HelperCallRecordings.COLUMN_CALLTYPEE,
                HelperCallRecordings.COLUMN_NAME,
                HelperCallRecordings.COLUMN_STARTTIME,
                HelperCallRecordings.COLUMN_ENDTIME

        };
        Cursor cursor = mDatabase.query((table == ALL ? HelperCallRecordings.TABLE_ALL : table == INBOUND ? HelperCallRecordings.TABLE_INBOUND : table == OUTBOUND ? HelperCallRecordings.TABLE_OUTBOUND : HelperCallRecordings.TABLE_MISSED), null, null, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                CallData calldata = new CallData();
                calldata.setCallid(cursor.getString(cursor.getColumnIndex(HelperCallRecordings.COLUMN_CALLID)));
                calldata.setBid(cursor.getString(cursor.getColumnIndex(HelperCallRecordings.COLUMN_BID)));
                calldata.setEid(cursor.getString(cursor.getColumnIndex(HelperCallRecordings.COLUMN_EID)));
                calldata.setCallfrom(cursor.getString(cursor.getColumnIndex(HelperCallRecordings.COLUMN_CALLFROM)));
                calldata.setCallto(cursor.getString(cursor.getColumnIndex(HelperCallRecordings.COLUMN_CALLTO)));
                calldata.setEmpname(cursor.getString(cursor.getColumnIndex(HelperCallRecordings.COLUMN_EMPNAME)));
                calldata.setCalltype(cursor.getString(cursor.getColumnIndex(HelperCallRecordings.COLUMN_CALLTYPEE)));
                calldata.setName(cursor.getString(cursor.getColumnIndex(HelperCallRecordings.COLUMN_NAME)));
                calldata.setStarttime(cursor.getString(cursor.getColumnIndex(HelperCallRecordings.COLUMN_STARTTIME)));
                calldata.setEndtime(cursor.getString(cursor.getColumnIndex(HelperCallRecordings.COLUMN_ENDTIME)));

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

}
