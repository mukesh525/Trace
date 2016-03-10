package vmc.in.mrecorder.entity;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.datahandler.HelperCallRecordings;

/**
 * Created by gousebabjan on 7/3/16.
 */
public class Util implements TAG {
    private static ArrayList<Model> list;
    private Cursor cursor;

    private String phoneNumber, id;
    private String time;
    private String filePath;
    private String callType;
    private Model model;
    private HelperCallRecordings hcr;
    private Cursor c;


    public ArrayList<Model> getCalls(Context context) {

        hcr = new HelperCallRecordings(context);
        c = hcr.display();
        list = new ArrayList<Model>();
        c.moveToFirst();
        while (c.moveToNext()) {
            model = new Model();
            phoneNumber = c.getString(c.getColumnIndex("Number"));
            time = c.getString(c.getColumnIndex("Time"));
            callType = c.getString(c.getColumnIndex("CallType"));
            filePath = c.getString(c.getColumnIndex("FilePath"));
            id = c.getString(c.getColumnIndex("_id"));

            Log.d("number", "" + phoneNumber);
            if (id != null) {
                model.setId(id);
            }
            if (phoneNumber != null) {
                model.setPhoneNumber(phoneNumber);
            }
            if (time != null) {
                model.setTime(time);
            }
            if (callType != null) {
                model.setCallType(callType);
            }
            if (filePath != null) {
                model.setFilePath(filePath);
                model.setFile(new File(filePath));
            }


            list.add(model);
        }

        return list;
    }



}
