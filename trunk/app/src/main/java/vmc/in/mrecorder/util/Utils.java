package vmc.in.mrecorder.util;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.activity.Login;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.service.CallRecorderServiceAll;

/**
 * Created by gousebabjan on 17/3/16.
 */
public class Utils implements TAG {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return toolbarHeight;
    }

    public static int getTabsHeight(Context context) {
        return (int) context.getResources().getDimension(R.dimen.tabsHeight);
    }


    public static boolean hasWIFIConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
                return true;
               // Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
                // Toast.makeText(context, activeNetwork.getTypeName(), Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            // not connected to the internet
            return false;
        }
        return false;

    }

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
    public static double tabletSize(Context context) {

        double size = 0;
        try {

            // Compute screen size

            DisplayMetrics dm = context.getResources().getDisplayMetrics();

            float screenWidth = dm.widthPixels / dm.xdpi;

            float screenHeight = dm.heightPixels / dm.ydpi;

            size = Math.sqrt(Math.pow(screenWidth, 2) +

                    Math.pow(screenHeight, 2));

        } catch (Throwable t) {

        }

        return size;

    }

    public static void sendSms(String number, Activity mActivity) {
        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.putExtra("sms_body", " ");
        sendIntent.putExtra("address", number);
        sendIntent.setType("vnd.android-dir/mms-sms");
        mActivity.startActivity(sendIntent);

    }

    public static void sendSms(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
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
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        File sampleDir;
        File sample;
        String selectedFolder = sharedPrefs.getString("store_path", "null");
        if (selectedFolder.equals("null")) {
            sampleDir = Environment.getExternalStorageDirectory();
            sample = new File(sampleDir.getAbsolutePath() + "/data/.tracker");
            if (!sample.exists()) sample.mkdirs();

        } else {
            sampleDir = new File(selectedFolder);
            sample = new File(sampleDir.getAbsolutePath() + "/.tracker");
            if (!sample.exists()) sample.mkdirs();
        }
        sharedPrefs.edit().clear().commit();
        List<File> files = getListFiles(sample);
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
        CallApplication.getWritabledatabase().DeleteAllData();
        CallApplication.getInstance().stopRecording();
        Intent intent = new Intent(context, Login.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_CLEAR_TASK |
                Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Log.d("Logout", "User Logout ");



    }
    public static void isSimLogout(Context context) {
        SharedPreferences sharedPrefs = PreferenceManager
                .getDefaultSharedPreferences(context);

        File sampleDir;
        File sample;
        String selectedFolder = sharedPrefs.getString("store_path", "null");
        if (selectedFolder.equals("null")) {
            sampleDir = Environment.getExternalStorageDirectory();
            sample = new File(sampleDir.getAbsolutePath() + "/data/.tracker");
            if (!sample.exists()) sample.mkdirs();

        } else {
            sampleDir = new File(selectedFolder);
            sample = new File(sampleDir.getAbsolutePath() + "/.tracker");
            if (!sample.exists()) sample.mkdirs();
        }
        sharedPrefs.edit().clear().commit();
        List<File> files = getListFiles(sample);
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
        CallApplication.getWritabledatabase().DeleteAllData();
       // CallApplication.getInstance().stopRecording();
        Log.d("Logout", "Logout onSim Changed");


    }


    public static void isLogoutBackground(Context context,String msg) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().clear().commit();
        File sampleDir = Environment.getExternalStorageDirectory();
        List<File> files = getListFiles(new File(sampleDir.getAbsolutePath() + "/Call Recorder"));
        for (int i = 0; i < files.size(); i++) {
            files.get(i).delete();
        }
        CallApplication.getWritabledatabase().DeleteAllData();
        CallApplication.getInstance().stopRecording();
        Log.d("Logout", "Background Logout");
        // Log.d("Logout", "Logout on Utils");
        showRecordNotification(context,msg);

    }

//    public static void cancelNotification(Context ctx, int notifyId) {
//        String ns = Context.NOTIFICATION_SERVICE;
//        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
//        nMgr.cancel(notifyId);
//    }



    public static void showRecordNotificationService(Context context) {
        Intent intent = new Intent(context, Home.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(context, R.color.accent))
                .setContentTitle("MTracker")
                .setAutoCancel(false)
                .setLargeIcon(bm)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setContentText("MTracker Running");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, mBuilder.build());
    }

    public static void showRecordNotification(Context context,String msg) {
        Intent intent = new Intent(context, Login.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.BigTextStyle s = new NotificationCompat.BigTextStyle();
        s.setBigContentTitle("MTracker");
      //  s.bigText("You have been logout by Admin.");
        s.bigText(msg);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(context, R.color.accent))
                .setContentTitle("MTracker")
                .setAutoCancel(false)
                .setLargeIcon(bm)
                .setStyle(s)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setContentText(msg);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public static List<File> getListFiles(File parentDir) {
        ArrayList<File> inFiles = new ArrayList<File>();

        File[] files = parentDir.listFiles();
        if (files != null && files.length > 0)
            for (File file : files) {
                if (file.isDirectory()) {
                    inFiles.addAll(getListFiles(file));
                } else {
                    if (file.getName().endsWith(".3gp") || file.getName().endsWith(".amr")) {
                        inFiles.add(file);
                    }
                }
            }
        return inFiles;
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
                CallApplication.getInstance().resetServicee();
                dialog.dismiss();
            }
        });
        dialog.show();
    }




    public static boolean checkAndRequestPermissions(Context context) {
        int readConractsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS);
        int readSmsConractsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_SMS);
        int recordAudioPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO);
        int readPhoneStatePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        int writeExternalPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int locationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (readConractsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (readSmsConractsPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }


        if (recordAudioPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);


        }if (readPhoneStatePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_PHONE_STATE);
        }

        if (writeExternalPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public static boolean isLocationEnabled(Context context) {
        return getLocationMode(context) != android.provider.Settings.Secure.LOCATION_MODE_OFF;
    }

    private static int getLocationMode(Context context) {
        return android.provider.Settings.Secure.getInt(context.getContentResolver(), android.provider.Settings.Secure.LOCATION_MODE, android.provider.Settings.Secure.LOCATION_MODE_OFF);
    }
}
