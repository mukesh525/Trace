package vmc.in.mrecorder.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.activity.Login;
import vmc.in.mrecorder.callbacks.Constants;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.Model;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.provider.GPSTracker;
import vmc.in.mrecorder.util.Utils;

public class CallRecorderServiceAll extends Service implements TAG {

    public MediaRecorder recorder;
    public static boolean recording;
    public static boolean IS_SERVICE_RUNNING = false;
    public boolean ringing, answered, outgoing, interupt;
    static boolean ring = false;
    String TAG = "SERVICE";
    static boolean callReceived = false;
    static boolean shown = false;

    //Broadcast receiver for calls
    private CallBroadcastReceiver cbr;
    private String phoneNumber, fileName;
    private String currentnumber;
    static boolean running = false;
    private File audiofile;
    private boolean inserted = false;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("permanent service", "created");
        boolean sync = ContentResolver.getMasterSyncAutomatically();
        if(!sync)
        ContentResolver.setMasterSyncAutomatically(true);




        try {
            cbr = new CallBroadcastReceiver();
            IntentFilter ifl = new IntentFilter();
            ifl.addAction("android.intent.action.PHONE_STATE");
            ifl.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
            registerReceiver(cbr, ifl);
            if (running == false) {
                Intent i = new Intent(CallRecorderServiceAll.this, CallRecorderServiceAll.class);
                startService(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent!=null &&intent.getAction() != null) {
            if (intent.getAction().equals(Constants.ACTION.STARTFOREGROUND_ACTION)) {
                Log.i(TAG, "Received Start Foreground Intent ");
                showRecordNotificationService(getApplicationContext());
                // Toast.makeText(this, "Service Started!", Toast.LENGTH_SHORT).show();
            } else if (intent.getAction().equals(
                    Constants.ACTION.STOPFOREGROUND_ACTION)) {
                Log.i(TAG, "Received Stop Foreground Intent");
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

    public void showRecordNotificationService(Context context) {
        Intent notificationIntent = new Intent(getApplicationContext(),Home.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(context, R.color.accent))
                .setContentTitle("MTracker")
                .setAutoCancel(false)
                .setLargeIcon(bm)
                .setOngoing(true)
                .setContentIntent(contentIntent)
                .setWhen(0)
                .setPriority(Notification.PRIORITY_MIN)
                .setContentText("Running");
        mBuilder.build().flags|=Notification.FLAG_AUTO_CANCEL;
        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                mBuilder.build());
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            running = false;
            unregisterReceiver(cbr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class CallBroadcastReceiver extends BroadcastReceiver {

        private GPSTracker mGPS;
        private boolean skip;

        public CallBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            try {

                mGPS = new GPSTracker(getApplicationContext());
                Log.d(TAG, "Latitude" + mGPS.getLatitude() + "," + mGPS.getLongitude());

                answered = checkAnswered(arg1);
                String fileName = String.valueOf(System.currentTimeMillis());
                Log.d(TAG, "" + String.valueOf(answered));
                if (answered == true && !interupt) {

                    SharedPreferences sharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());

                    boolean notifyMode = sharedPrefs.getBoolean("prefRecording", false);
                    Log.d(TAG, "Calls No Recording" + notifyMode);
                    if (!notifyMode) {
                        if (answered && ringing) {
                            CallApplication.getWritabledatabase().insert(phoneNumber, fileName, "empty", INCOMING, mGPS.getLatitude() + "," + mGPS.getLongitude());
                            Log.e("answer", "" + "incoming inserted");
                        }
                        if (answered && !ringing) {
                            CallApplication.getWritabledatabase().insert(phoneNumber, fileName, "empty", OUTGOING, mGPS.getLatitude() + "," + mGPS.getLongitude());
                            Log.e("answer", "" + "outgoing inserted");
                        }

                    } else {
                        try {
                            startRecording();
                        } catch (Exception e) {
                            Log.e("exp", " startRecording Method exp " + e);
                            for (int i = 0; i < 2; i++) {
                                Toast.makeText(getApplicationContext(), "Unable to record try to change audio source in MTracker settings", Toast.LENGTH_LONG).show();
                            }
                            e.printStackTrace();
                        }

                    }
                    ringing = false;
                    outgoing = false;
                }
            } catch (Exception e) {
                Log.e("exp", "exp " + e);
                e.printStackTrace();
            }
        }

        //Controls recording
        public void startRecording() throws Exception {
            if (recorder != null) {
                recorder.release();
            }
            initRecorder();
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
            Log.d(TAG, "Record running " + String.valueOf(recording));
            if (recording == false) {
                Log.d(TAG, "started");
                if (recorder != null) {
                    Log.d(TAG, "recorder is not null");
                    recorder.start();
                } else {
                    Log.d(TAG, "recorder is  null");
                    initRecorder();
                    recorder.start();
                }

            } else
                Log.d(TAG, "recording");
               recording = true;

            if (phoneNumber != null) {
                Log.d("FILE", audiofile.getAbsolutePath());
                if (ringing == true)
                    CallApplication.getWritabledatabase().insert(phoneNumber, fileName, audiofile.getAbsolutePath(), INCOMING, mGPS.getLatitude() + "," + mGPS.getLongitude());
                else if (outgoing == true)
                    CallApplication.getWritabledatabase().insert(phoneNumber, fileName, audiofile.getAbsolutePath(), OUTGOING, mGPS.getLatitude() + "," + mGPS.getLongitude());


            }

        }

        private void initRecorder() throws IOException {
            recorder = new MediaRecorder();
            getAudioSettings();
            recorder.prepare();
        }

        public boolean checkAnswered(Intent i) {

            Log.d(TAG, "testing");
            if (i.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                Log.d("CONFRENCE", "testing");

                phoneNumber = i.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

                outgoing = true;
                return false;
            } else {
                Bundle b = i.getExtras();
                String state = b.getString(TelephonyManager.EXTRA_STATE);

                Log.d(TAG, state);

                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {

                    //Check to see if call was answered later
                    ringing = true;
                    ring = true;
                    shown = false;
                    callReceived = false;
                    Log.d(TAG, "Ringing true");
                    Log.d("CONFRENCE", "Ringinig");

                    phoneNumber = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);


                    return false;
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    //call to be recorded if it was ringing or new outgoing
                    callReceived = true;
                    if (recording == true) {
                        skip = true;
                    } else {
                        currentnumber = phoneNumber;
                        callReceived = true;
                        skip = false;
                    }

                    if (!currentnumber.equals(phoneNumber)) {

                        if (recording && skip) {
                            if (!inserted) {
                                Log.d("CONFRENCE", "numbers are different" + currentnumber);
                                String fileName = String.valueOf(System.currentTimeMillis());
                                inserted = true;
                                interupt = true;
                                CallApplication.getWritabledatabase().insert(phoneNumber, fileName, DEFAULT, MISSED, mGPS.getLatitude() + "," + mGPS.getLongitude());
                                Log.d("CONFRENCE", "missed call from  incoming" + phoneNumber);
                            } else {
                                inserted = false;

                            }
                        }
                    }
                    Log.d(TAG, "OFFHOOK" + phoneNumber);
                    Log.d("CONFRENCE", "OFFHOOK" + phoneNumber);
                    if (ringing == true || outgoing == true) {
                        return true;
                    } else
                        return false;
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                    Log.d(TAG, "IDLE");
                    Log.d("MISSED", "callReceived true");
                    Log.d("CONFRENCE", "IDLE");
                    if (ring == true && callReceived == false) {
                        if (!shown) {
                            String fileName = String.valueOf(System.currentTimeMillis());
                            Log.d(TAG, "Missed call from : " + phoneNumber);

                            if (!interupt) {
                                Log.d("CONFRENCE", "Missed call from : " + phoneNumber);
                                CallApplication.getWritabledatabase().insert(phoneNumber, fileName, DEFAULT, MISSED, mGPS.getLatitude() + "," + mGPS.getLongitude());
                                shown = true;
                            }
                        }

                    }

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //   readCallLogs();
                            //deleteing false call
                            ArrayList<Model> Calllist = CallApplication.getWritabledatabase().getAllOfflineCalls();
                            Calllist = getSortList(OUTGOING, Calllist);
                            if (Calllist.size() > 0) {
                                Collections.sort(Calllist, Collections.reverseOrder());
                                for (int j = 0; j < Calllist.size(); j++) {
                                    try {
                                        getCallDetails(Calllist.get(j));

                                    } catch (Exception e) {
                                    }
                                }
                            }


                            ArrayList<Model> Calllist1 = getSortList(INCOMING, Calllist);
                            if (Calllist1.size() > 0) {
                                Collections.sort(Calllist1, Collections.reverseOrder());
                                for (int j = 0; j < Calllist1.size(); j++) {
                                    int duration;
                                    try {
                                        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), Uri.fromFile(Calllist1.get(j).getFile()));
                                        duration = mp.getDuration();
                                    } catch (Exception e) {
                                        duration = 0;
                                    }
                                    if (new File(Calllist1.get(j).getFilePath()).exists() && duration == 0) {
                                        new File(Calllist1.get(j).getFilePath()).delete();
                                        Log.d("Numbers", "FILE DELETED" + "INCOMING:" + Calllist1.get(j).getFilePath());
                                    }
                                }
                            }

                        }
                    }, 1000);

                    interupt = false;

                    //Stop recording if it was on
                    if (recording == true) {
                        SharedPreferences sharedPrefs = PreferenceManager
                                .getDefaultSharedPreferences(getApplicationContext());

                        boolean notificationMode = sharedPrefs.getBoolean("prefNotify", false);
                        if (notificationMode)
                            showRecordNotification(currentnumber != null ? currentnumber : phoneNumber);
                        recorder.stop();
                        recorder.release();
                    }
                    recording = false;
                    ring = false;
                    answered = false;
                    outgoing = false;
                    ringing = false;
                    currentnumber = null;
                    phoneNumber = null;


                    return false;
                } else
                    return false;
            }

        }


        public void getAudioSettings() {
            audiofile = null;
            SharedPreferences sharedPrefs = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            int selection = Integer.parseInt(sharedPrefs.getString("audioformat", "1"));
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


            Log.d("DIRECTORY", sample.toString());


            fileName = String.valueOf(System.currentTimeMillis());
            setRecordingsource(sharedPrefs);

            switch (selection) {
                case 1:
                    audiofile = new File(sample.getAbsolutePath() + "/sound" + fileName + ".3gp");
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(audiofile.getAbsolutePath());
                    Log.d(TAG, "AUDIO FORMAT 3GP");
                    break;
                case 2:
                    audiofile = new File(sample.getAbsolutePath() + "/sound" + fileName + ".amr");
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(audiofile.getAbsolutePath());
                    Log.d(TAG, "AUDIO FORMAT AMR");
                    break;
                case 3:
                    audiofile = new File(sample.getAbsolutePath() + "/sound" + fileName + ".mp4");
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(audiofile.getAbsolutePath());
                    Log.d(TAG, "AUDIO FORMAT MP4");
                    break;
                default:
                    audiofile = new File(sample.getAbsolutePath() + "/sound" + fileName + ".3gp");
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(audiofile.getAbsolutePath());
                    Log.d(TAG, "AUDIO FORMAT 3GP");
                    break;

            }


        }


        public void setRecordingsource(SharedPreferences sharedPrefs) {
            int selection = Integer.parseInt(sharedPrefs.getString("audiosource", "1"));
            switch (selection) {
                case 1:
                    recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                    Log.d(TAG, "AUDIO SOURCE DEFAULT");
                    break;
                case 2:
                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                    Log.d(TAG, "AUDIO SOURCE VOICE CALL");
                    break;
                case 3:
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    Log.d(TAG, "AUDIO SOURCE MIC");
                    break;
                case 4:
                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                    Log.d(TAG, "AUDIO SOURCE VOICE_COMMUNICATION");
                    break;
                case 5:
                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
                    Log.d(TAG, "AUDIO SOURCE VOICE_RECOGNITION");
                    break;
                case 6:
                    recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                    Log.d(TAG, "AUDIO SOURCE CAMCORDER");
                    break;
                default:
                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                    Log.d(TAG, "AUDIO SOURCE VOICE_CALL");
                    break;


            }
        }
    }

    private void showRecordNotification(String number) {
        String name = getContactName(number) != null ? getContactName(number) : number;
        String from = "";
        if (ring) {
            from = "from";
        } else {
            from = "to";
        }
        Log.d(TAG, name);
        NotificationCompat.BigTextStyle s = new NotificationCompat.BigTextStyle();
        s.setBigContentTitle("MTracker");
        s.bigText("Last call " + from + " " + name + " " + "is recorded successfully.");
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.accent))
                .setContentTitle("MTracker")
                .setAutoCancel(false)
                .setLargeIcon(bm)
                .setStyle(s)
                .setContentText("Last call " + from + " " + name + " " + "is recorded successfully.");
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }

    public String getContactName(String snumber) {
        ContentResolver cr = getApplicationContext().getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(snumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        String contactName = null;
        if (cursor == null) {
            return null;
        } else if (cursor.moveToFirst()) {

            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

    //outgoing
    public synchronized String getCallDetails(Model model1) {
        String whereClause = CallLog.Calls.NUMBER + " = " + model1.getPhoneNumber() + " AND " + CallLog.Calls.TYPE + "=" + CallLog.Calls.OUTGOING_TYPE;
        StringBuffer sb = new StringBuffer();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
        }
        Cursor managedCursor = null;
        managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, whereClause,
                null, CallLog.Calls.DATE + " DESC");

//        if (model1.getCallType().equals(MISSED)) {
//            managedCursor = getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI, null, null, null, android.provider.CallLog.Calls.DEFAULT_SORT_ORDER);
//        } else {
//            managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, whereClause,
//                    null, CallLog.Calls.DATE + " DESC");
//
//        }

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        ArrayList<Model> templog = new ArrayList<Model>();

        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {

            Model model = new Model();
            String phNumber1 = managedCursor.getString(number);
            model.setPhoneNumber(phNumber1);
            String callType = managedCursor.getString(type);
            model.setCallType(callType);
            String callDate = managedCursor.getString(date);
            model.setTime(callDate);
            Date callDayTime = new Date(Long.valueOf(callDate));
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
            String exacttime = sdf.format(callDayTime);
            // Log.d("Numbers", exacttime);
            int callType1 = managedCursor.getInt(type);
            String callDuration = managedCursor.getString(duration);
            model.setDuration(callDuration);

            templog.add(model);


        }
        if (model1.getCallType().equals(MISSED)) {
            validatedMissed(templog, model1);
        } else {
            validatedOut(templog, model1);
        }

        managedCursor.close();

        return sb.toString();

    }


    private void validatedOut(ArrayList<Model> templog, Model model1) {
        for (int i = 0; i < templog.size(); i++) {
            Model model = templog.get(i);
            if (model.getPhoneNumber().equals(model1.getPhoneNumber())) {
                //     Log.d("Numbers", "Number equal" + model1.getPhoneNumber());
                Date callDayTime = new Date(Long.valueOf(model.getTime()));
                Date callDayTime1 = new Date(Long.valueOf(model1.getTime()));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
                String time1 = sdf.format(callDayTime);
                String time2 = sdf.format(callDayTime1);
                long seconds = (callDayTime1.getTime() - callDayTime.getTime()) / 1000;
                if (seconds < 5) {
                    model1.setDuration(model.getDuration());
                    if (Integer.parseInt(model.getDuration()) == 0) {
                        if (new File(model1.getFilePath()).exists()) {
                            Log.d("Numbers", "Call To be Deleted");
                            Log.d("Numbers", model1.getPhoneNumber() + " " + "Actual Time" + time2 + " Log Time " + time1 + " Diffrence in Sec " + seconds);
                            Log.d("Numbers", "Duration " + model.getDuration());
                            //if (new File(model1.getFilePath()).exists()) {
                            new File(model1.getFilePath()).delete();//from internal storage
                            Log.d("Numbers", "FILE DELETED" + ":" + model1.getFile().getName());
                        }
                    }
                }

            }
        }
    }

    private void validatedMissed(ArrayList<Model> templog, Model model1) {
        Boolean found = false;
        for (int i = 0; i < templog.size(); i++) {
            Model model = templog.get(i);
            if (model.getPhoneNumber().equals(model1.getPhoneNumber())) {
                Date callDayTime = new Date(Long.valueOf(model.getTime()));
                Date callDayTime1 = new Date(Long.valueOf(model1.getTime()));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
                String time1 = sdf.format(callDayTime);
                String time2 = sdf.format(callDayTime1);
                if ((model.getCallType().equals("3") || model.getCallType().equals("5") || (model.getCallType().equals("1") && Integer.parseInt(model.getDuration()) == 0)) && time1.equals(time2)) {
                    Log.d("Numbers1", "Matches " + time1 + " " + time2);
                    found = true;
                    break;

                } else {
                    Log.d("Numbers1", "MisMatches" + time1 + " " + time2);
                    found = false;
                    Log.d("Numbers", "Missed not found" + model1.getPhoneNumber() + "  Missed Call " + model.getDuration() + " " + callDayTime);
                }


            } else {
                found = false;
            }
        }
        if (!found) {
            Log.d("Numbers1", "Missed Call not fond ");
            CallApplication.getWritabledatabase().delete(model1.getId());//from db

        }

    }


    public ArrayList<Model> getSortList(String name, ArrayList<Model> list) {
        ArrayList<Model> temp = new ArrayList<Model>();
        for (Model model : list) {
            if (model.getCallType().equals(name)) {
                temp.add(model);
            }
        }
        return temp;
    }


}
