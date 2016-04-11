package vmc.in.mrecorder.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.CallData;

/**
 * Created by gousebabjan on 30/3/16.
 */
public class Parser implements TAG {


    public static ArrayList<CallData> ParseData(JSONObject response) throws JSONException {
        ArrayList<CallData> CallList = new ArrayList<CallData>();
        JSONArray recordsArray = null;
        SimpleDateFormat sdf = new SimpleDateFormat(DateTimeFormat);
        if (response.has(RECORDS)) {
            Log.d("RESPONSE",response.toString());
            recordsArray = response.getJSONArray(RECORDS);
            for (int i = 0; i < recordsArray.length(); i++) {
                CallData callData = new CallData();
                JSONObject record = (JSONObject) recordsArray.get(i);
                if (record.has(CALLID)) {
                    callData.setCallid(record.getString(CALLID));
                }
                if (record.has(BID)) {
                    callData.setBid(record.getString(BID));
                }
                if (record.has(EID)) {
                    callData.setEid(record.getString(EID));
                }
                if (record.has(CALLFROM)) {
                    callData.setCallfrom(record.getString(CALLFROM));
                }
                if (record.has(CALLTO)) {
                    callData.setCallto(record.getString(CALLTO));
                }
                if (record.has(EMPNAME)) {
                    callData.setEmpname(record.getString(EMPNAME));
                }
                if (record.has(NAME)) {
                    callData.setName(record.getString(NAME));
                }
                if (record.has(CALLTYPEE)) {
                    callData.setCalltype(record.getString(CALLTYPEE));
                }

                if (record.has(STARTTIME)) {
                    callData.setStarttime(record.getString(STARTTIME));
                }
                if (record.has(ENDTIME)) {
                    callData.setEndtime(record.getString(ENDTIME));
                }
                if (record.has(FILENAME)) {
                    callData.setFilename(record.getString(FILENAME));
                    Log.d(TAG,callData.getFilename());
                }


                Date startTime = null;
                Date endTime = null;
                try {
                    startTime = sdf.parse(record.getString(STARTTIME));
                    endTime = sdf.parse(record.getString(ENDTIME));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                callData.setStartTime(startTime);
                callData.setEndTime(endTime);

                CallList.add(callData);

            }
        }
        return CallList;


    }
}
