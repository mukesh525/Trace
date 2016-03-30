package vmc.in.mrecorder.myapplication;


import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.UUID;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.datahandler.HelperCallRecordings;
import vmc.in.mrecorder.datahandler.MDatabase;
import vmc.in.mrecorder.service.CallRecorderServiceAll;
import vmc.in.mrecorder.syncadapter.SyncUtils;

public class CallApplication extends Application implements TAG {
    public static CallApplication mApplication;
    public static SharedPreferences sp;//to prevent concurrent creation of shared pref and editor
    public static Editor e;
    public static int opt;


    private static String DeviceID;
  private static HelperCallRecordings mDatabase;
  private static MDatabase mdatabase;
    public Intent all;




    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        SyncUtils.CreateSyncAccount(getBaseContext());
        Log.e("application", "created");
        DeviceID = GetDeviceId();
        mApplication = this;
        //try{
        sp = getApplicationContext().getSharedPreferences("com.example.call", Context.MODE_PRIVATE);

        e = sp.edit();

        try {


            all = new Intent(this, CallRecorderServiceAll.class);
            //  Intent opt = new Intent(this, CallRecorderServiceOptional.class);
            if (sp.getInt(TYPE, 0) == 0) {
                startService(all);
            } else if (sp.getInt(TYPE, 0) == 1) {
                stopService(all);
                //  stopService(opt);
            }

        } catch (Exception e) {
            Log.e("application", "service");
        }

    }


    public synchronized static String getDeviceId() {

        return DeviceID;

    }

    public static Context getAplicationContext() {
        return mApplication.getApplicationContext();
    }

    public synchronized static HelperCallRecordings getWritableDatabase() {
        if (mDatabase == null) {
            mDatabase = new HelperCallRecordings(getAplicationContext());
        }
        return mDatabase;
    }

    public static CallApplication getInstance() {
        return mApplication;
    }



    public void resetService() {
        try {

            Intent all = new Intent(this, CallRecorderServiceAll.class);
            // Intent opt = new Intent(this, CallRecorderServiceOptional.class);
            stopService(all);
            // stopService(opt);
            if (sp.getInt("type", 0) == 0) {
                startService(all);
            } else if (sp.getInt("type", 0) == 1) {
                stopService(all);
                //  stopService(opt);
            } else if (sp.getInt("type", 0) == 2) {
                //  startService(opt);
            }
        } catch (Exception e) {
            Log.e("application", "reset service");
        }
    }

    public synchronized String GetDeviceId() {
        //android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
//        Settings.Secure.ANDROID_ID);
//        Log.d("android_id",android_id);
        final TelephonyManager tm = (TelephonyManager) getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);

        final String tmDevice, tmSerial, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.ANDROID_ID);

        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String deviceId = deviceUuid.toString();
        //  Log.d("android_id", deviceId);
        return deviceId;

    }

    public void startRecording() {
        CallApplication.sp.edit().putInt(TYPE, 0).commit();
        if (CallApplication.sp.getInt(TYPE, 0) == 0) {
            startService(all);
        } else if (CallApplication.sp.getInt(TYPE, 0) == 1) {
            stopService(all);
            //  stopService(opt);
        }
    }

    public void stopRecording() {
        CallApplication.sp.edit().putInt(TYPE, 1).commit();
        if (CallApplication.sp.getInt(TYPE, 0) == 0) {
            startService(all);
        } else if (CallApplication.sp.getInt(TYPE, 0) == 1) {
            stopService(all);
            //  stopService(opt);
        }
    }
    public synchronized static MDatabase getWritabledatabase() {
        if (mdatabase == null) {
            mdatabase = new MDatabase(getAplicationContext());
        }
        return mdatabase;
    }

}
