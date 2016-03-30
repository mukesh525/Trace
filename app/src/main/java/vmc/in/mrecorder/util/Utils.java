package vmc.in.mrecorder.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONObject;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Login;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.service.CallRecorderServiceAll;

/**
 * Created by gousebabjan on 17/3/16.
 */
public class Utils implements TAG {

    public static boolean onlineStatus2(Context activityContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {

                            return true;
                        }
                    }
                }
            }
        }
        //  Toast.makeText(mContext,mContext.getString(R.string.please_connect_to_internet),Toast.LENGTH_SHORT).show();
        return false;
    }

    public static boolean isMyServiceRunning(Class<?> serviceClass, Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void makeAcall(String number, final Activity mActivity) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + number));
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(mActivity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            showMessageOKCancel("You need to allow access to Calls",
                    new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mActivity.requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                                    MY_PERMISSIONS_CALL);
                        }
                    }, mActivity);
            return;

        }
        mActivity.startActivity(callIntent);

    }

    private static void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener, Context mActivity) {
        new AlertDialog.Builder(mActivity)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    public static void sendSms(String number, Activity mActivity) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", " ");
        sendIntent.putExtra("address", number);
        sendIntent.setType("vnd.android-dir/mms-sms");
        mActivity.startActivity(sendIntent);

    }

    public static void saveToPrefs(Context context, String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void saveToPrefs(Context context, String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static void isLogout(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().commit();

        CallApplication.getWritabledatabase().DeleteAllData();
        CallApplication.getInstance().stopRecording();
        Intent intent = new Intent(context, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);


    }

    public static boolean contains(JSONObject jsonObject, String key) {
        return jsonObject != null && jsonObject.has(key) && !jsonObject.isNull(key) ? true : false;
    }

    public static String getFromPrefs(Context context, String key, String defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getString(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static Boolean isLogin(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);

        String authKey = sharedPrefs.getString(AUTHKEY, "n");
        String email = sharedPrefs.getString(EMAIL, "n");
        String password = sharedPrefs.getString(PASSWORD, "n");


        return !(authKey.equals("n") && email.equals("n") && password.equals("n"));

    }

    public static boolean isEmpty(String msg) {
        return msg == null
                || msg.trim().equals("")
                || msg.isEmpty();
    }

    public static boolean onlineStatus1(Context activityContext) {
        ConnectivityManager cm = (ConnectivityManager)
                activityContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] activeNetInfo = cm.getAllNetworkInfo();
        boolean isConnected = false;
        for (NetworkInfo i : activeNetInfo) {
            if (i.getState() == NetworkInfo.State.CONNECTED) {
                isConnected = true;
                break;
            }
        }


        return isConnected;
    }


    public static Boolean getFromPrefsBoolean(Context context, String key, Boolean defaultValue) {
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return sharedPrefs.getBoolean(key, defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
            return defaultValue;
        }
    }

    public static void setRecording(Context context) {
        CallApplication.sp = context.getApplicationContext().getSharedPreferences("com.example.call", Context.MODE_PRIVATE);
        CallApplication.e = CallApplication.sp.edit();
        final Dialog dialog = new Dialog(context, R.style.myBackgroundStyle);
        dialog.setContentView(R.layout.layout_dialog);
        // dialog.setTitle("Set Your Record Preference");
        dialog.setTitle(Html.fromHtml("<font color='black'>Set Record Preferences</font>"));
        RadioGroup group = (RadioGroup) dialog.findViewById(R.id.radioGroup1);
        //  final RelativeLayout rl = (RelativeLayout) dialog.findViewById(R.id.ask_layout);
        final TextView tv1 = (TextView) dialog.findViewById(R.id.r0);
        final TextView tv2 = (TextView) dialog.findViewById(R.id.r1);
        switch (CallApplication.sp.getInt("type", 0)) {
            case 0:
                group.check(R.id.radio0);
                break;

            case 1:
                group.check(R.id.radio1);
                break;


            default:
                break;
        }


        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // TODO Auto-generated method stub
                switch (checkedId) {
                    case R.id.radio0:
                        CallApplication.e.putInt("type", 0);
                        // rl.setVisibility(View.GONE);
                        tv1.setVisibility(View.VISIBLE);
                        tv2.setVisibility(View.GONE);
                        break;
                    case R.id.radio1:
                        CallApplication.e.putInt("type", 1);
                        // rl.setVisibility(View.GONE);
                        tv1.setVisibility(View.GONE);
                        tv2.setVisibility(View.VISIBLE);
                        break;


                    default:
                        break;
                }
            }
        });
        Button save = (Button) dialog.findViewById(R.id.button1);
        save.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CallApplication.e.commit();
                CallApplication.getInstance().resetService();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

}
