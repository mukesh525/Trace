package vmc.in.mrecorder.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Login;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.entity.Model;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.provider.GPSTracker;

public class CallRecorderServiceAll extends Service implements TAG {

    public MediaRecorder recorder;
    public static boolean recording;
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

        return START_STICKY;
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
                            CallApplication.getWritabledatabase().insert(phoneNumber, fileName, "empty", INCOMING);
                            Log.e("answer", "" + "incoming inserted");
                        }
                        if (answered && !ringing) {
                            CallApplication.getWritabledatabase().insert(phoneNumber, fileName, "empty", OUTGOING);
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
            recorder = new MediaRecorder();
            getAudioSettings();
            recorder.prepare();
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
            Log.d(TAG, "Record running " + String.valueOf(recording));
            if (recording == false) {
                Log.d(TAG, "started");
                if (recorder != null) {
                    recorder.start();
                    Log.d(TAG, "recorder is not null");
                } else {
                    Log.d(TAG, "recorder is  null");
                    recorder = new MediaRecorder();
                    recorder.start();
                }

            } else
                Log.d(TAG, "recording");
            recording = true;

            if (phoneNumber != null) {
                if (ringing == true)
                    CallApplication.getWritabledatabase().insert(phoneNumber, fileName, audiofile.getAbsolutePath(), INCOMING);
                else if (outgoing == true)
                    CallApplication.getWritabledatabase().insert(phoneNumber, fileName, audiofile.getAbsolutePath(), OUTGOING);


            }

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
                        //callReceived = false;
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
                                CallApplication.getWritabledatabase().insert(phoneNumber, fileName, DEFAULT, MISSED);
                                Log.d("CONFRENCE", "missed call from  incoming" + phoneNumber);
                            } else {
                                inserted = false;
                                //interupt=false;
                            }

                            // return false;
                        }
                    }
                    Log.d(TAG, "OFFHOOK" + phoneNumber);
                    Log.d("CONFRENCE", "OFFHOOK" + phoneNumber);
                    if (ringing == true || outgoing == true) {
                        //ringing=false;
                        //outgoing=false;
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
                            mGPS = new GPSTracker(getApplicationContext());
                            Log.d(TAG, "Latitude" + mGPS.getLatitude() + "");
                            Log.d(TAG, "Longitude" + mGPS.getLongitude() + "");
                            if (!interupt) {
                                Log.d("CONFRENCE", "Missed call from : " + phoneNumber);
                                CallApplication.getWritabledatabase().insert(phoneNumber, fileName, DEFAULT, MISSED);
                                shown = true;
                            }

                            // interupt = false;

                        }

                    }


                    //deleteing false call

//                    SharedPreferences sharedPrefs = PreferenceManager
//                            .getDefaultSharedPreferences(getApplicationContext());
//
//                    boolean notifyMode = sharedPrefs.getBoolean("prefRecording", false);
//                    if (notifyMode) {
//                        ArrayList<Model> Calllist = CallApplication.getWritabledatabase().getAllOfflineCalls();
//                        Calllist = getSortList(OUTGOING, Calllist);
//                        if (Calllist.size() > 0) {
//                            Collections.sort(Calllist, Collections.reverseOrder());
//                            for (int j = 0; j < Calllist.size(); j++) {
//                                getCallDetails(Calllist.get(j));
//                            }
//
//                        }
//                    }



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

                        recording = false;
                        ring=false;
                        answered = false;
                        outgoing = false;
                        ringing = false;
                        currentnumber = null;
                        phoneNumber = null;
                    }

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
                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                    Log.d(TAG, "AUDIO SOURCE VOICE_CALL");
                    break;
                case 2:
                    recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                    Log.d(TAG, "AUDIO SOURCE DEFAULT");
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
        Cursor managedCursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, null, whereClause,
                null, CallLog.Calls.DATE + " DESC");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        ArrayList<Model> templog = new ArrayList<Model>();

        sb.append("Call Details :");
        while (managedCursor.moveToNext()) {

            Model model = new Model();
            String phNumber = managedCursor.getString(number);
            model.setPhoneNumber(phNumber);
            String callType = managedCursor.getString(type);
            model.setCallType(callType);
            String callDate = managedCursor.getString(date);
            model.setTime(callDate);
            Date callDayTime = new Date(Long.valueOf(callDate));
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
            String exacttime = sdf.format(callDayTime);
            //  Log.d("Numbers", exacttime);
            String callDuration = managedCursor.getString(duration);
            model.setDuration(callDuration);
            templog.add(model);


//            String dir = null;
//            int dircode = Integer.parseInt(callType);
//            switch (dircode) {
//                case CallLog.Calls.OUTGOING_TYPE:
//                    dir = "OUTGOING";
//                    break;
//
//                case CallLog.Calls.INCOMING_TYPE:
//                    dir = "INCOMING";
//                    break;
//
//                case CallLog.Calls.MISSED_TYPE:
//                    dir = "MISSED";
//                    break;
//            }
//            sb.append("\nPhone Number:--- " + phNumber + " \nCall Type:--- "
//                    + dir + " \nCall Date:--- " + callDayTime + " \nCall time:--- " + exacttime
//                    + " \nCall duration in sec :--- " + callDuration);
//            sb.append("\n----------------------------------");
        }
        for (int i = 0; i < templog.size(); i++) {

            Model model = templog.get(i);
            if (model.getPhoneNumber().equals(model1.getPhoneNumber())) {
                //     Log.d("Numbers", "Number equal" + model1.getPhoneNumber());
                Date callDayTime = new Date(Long.valueOf(model.getTime()));
                Date callDayTime1 = new Date(Long.valueOf(model1.getTime()));
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
                String time1 = sdf.format(callDayTime);
                String time2 = sdf.format(callDayTime1);


                if (time1.equals(time2)) {

                    if (Integer.parseInt(model.getDuration()) == 0) {
                        Log.d("Numbers", "Call To be Deleted");
                        Log.d("Numbers", model1.getPhoneNumber() + " " + time2);
                        Log.d("Numbers", "Duration " + model.getDuration());
                        CallApplication.getWritabledatabase().delete(model1.getId());//from db
                        if (new File(model1.getFilePath()).exists()) {
                            new File(model1.getFilePath()).delete();//from internal storage
                            Log.d("Numbers", "FILE DELETED" + ":" + model1.getFile().getName());
                        }
                        Log.d("Numbers", "RECODRD DELETED" + ":" + model1.getFile().getName());
                    }
                }


            }


        }
        managedCursor.close();


        return sb.toString();

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
