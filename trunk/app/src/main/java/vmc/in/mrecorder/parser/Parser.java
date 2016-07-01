package vmc.in.mrecorder.parser;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.BarModel;
import vmc.in.mrecorder.entity.CallData;
import vmc.in.mrecorder.entity.LoginData;

import vmc.in.mrecorder.entity.PieModel;

/**
 * Created by gousebabjan on 30/3/16.
 */
public class Parser implements TAG {
  public static  String code;


    public synchronized static LoginData ParseLoginResponse(JSONObject response) throws JSONException {
        LoginData loginData = new LoginData();

        if (response.has(CODE)){
          loginData.setCode(response.getString(CODE));
        }
        if (response.has(MESSAGE)) {
            loginData.setMessage(response.getString(MESSAGE));
        }
        if (response.has(AUTHKEY)) {
            loginData.setAuthcode(response.getString(AUTHKEY));
        }
        if (response.has(NAME)) {
            loginData.setUsername(response.getString(NAME));
        }
        if (response.has(USERTYPE)) {
           loginData.setUsertype(response.getString(USERTYPE));
        }
        if (response.has(RECORDING)) {
            loginData.setRecording(response.getString(RECORDING));
        }
        if (response.has(MCUBECALLS)) {
           loginData.setMcuberecording(response.getString(MCUBECALLS));
        }
        if (response.has(WORKHOUR)) {
           loginData.setWorkhour(response.getString(WORKHOUR));

        }

        return loginData;
    }




//    public synchronized static OTPData ParseOTPResponse(JSONObject response) throws JSONException {
//        OTPData otpData = new OTPData();
//
//        if (response.has(CODE)){
//            otpData.setCode(response.getString(CODE));
//        }
//        if (response.has(MESSAGE)) {
//            otpData.setMessage(response.getString(MESSAGE));
//        }
//        if (response.has(OTP)) {
//            otpData.setOtp(response.getString(OTP));
//        }
//
//        return otpData;
//    }


    public synchronized static  ArrayList<BarModel> ParseEMPResponse(JSONObject response) throws JSONException {

        ArrayList<BarModel>  barModels = new ArrayList<BarModel>();
        BarModel barModel;
        if (response != null) {

            if (response.has(CODE))
                code=response.getString(CODE);

                if (response.getJSONArray(RECORDS).length() > 0) {
                    for (int i = 0; i < response.getJSONArray(RECORDS).length(); i++) {
                        JSONObject jsonobj = response.getJSONArray(RECORDS).getJSONObject(i);
                        barModel = new BarModel();
                        if (jsonobj.has(EMPNAME)) {
                            barModel.setEmpname(jsonobj.getString(EMPNAME));
                        }
                        if (jsonobj.has(Inbound)) {
                            barModel.setInbound(jsonobj.getString(Inbound));
                        }
                        if (jsonobj.has(Outbound)) {
                            barModel.setOutbound(jsonobj.getString(Outbound));
                        }
                        if (jsonobj.has(missed)) {
                            barModel.setMissed(jsonobj.getString(missed));
                        }

                        barModels.add(barModel);
                    }
                }
            }

        return barModels;
    }


    public synchronized static  ArrayList<PieModel> ParseTypeResponse(JSONObject response) throws JSONException {

        ArrayList<PieModel>   pieModels= new ArrayList<>();;
        PieModel pieModel;
        if (response != null)
            if (response.has(CODE))
                code=response.getString(CODE);

            if (response.getJSONArray(RECORDS).length() > 0) {

                    for (int i = 0; i < response.getJSONArray(RECORDS).length(); i++) {
                        JSONObject jsonobj = response.getJSONArray(RECORDS).getJSONObject(i);
                        pieModel = new PieModel();
                        if (jsonobj.has(CALLTYPEE)) {

                            pieModel.setCalltype(jsonobj.getString(CALLTYPEE).equals("0") ? MISSED :
                                    jsonobj.getString(CALLTYPEE).equals("1") ? INCOMING : OUTGOING);
                            Log.d("CALLTYPE", jsonobj.getString(CALLTYPEE) + "");
                        }


                        if (jsonobj.has(COUNT)) {
                            pieModel.setCount(jsonobj.getString(COUNT));
                        }


                        pieModels.add(pieModel);
                    }

                }



        return pieModels;
    }





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
