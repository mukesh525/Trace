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
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.activity.Login;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.provider.GPSTracker;

public class CallRecorderServiceAll extends Service implements TAG {

    static MediaRecorder recorder;
    public static boolean recording;
    public boolean ringing, answered, outgoing;
    static boolean ring = false;
    String TAG = "SERVICE";
    static boolean callReceived = false;
    static boolean shown = false;

    //Broadcast receiver for calls
    private CallBroadcastReceiver cbr;
    private String phoneNumber, fileName;
    static boolean running = false;
    private File audiofile;


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

        public CallBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            try {
                answered = checkAnswered(arg1);
                String fileName = String.valueOf(System.currentTimeMillis());
                Log.d(TAG, "" + String.valueOf(answered));
                if (answered == true) {
                    SharedPreferences sharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(getApplicationContext());
                    boolean notifyMode = sharedPrefs.getBoolean("prefCallUpdate", false);

                    if (notifyMode) {
                        if (answered && ringing) {
                            CallApplication.getWritabledatabase().insert(phoneNumber, fileName, "empty", INCOMING);
                            Log.e("answer", "" + "incoming inserted");
                        }
                        if (answered && !ringing) {
                            CallApplication.getWritabledatabase().insert(phoneNumber, fileName, "empty", OUTGOING);
                            Log.e("answer", "" + "outgoing inserted");
                        }

                    } else {
                        startRecording();

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
                recorder.start();

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

        public boolean checkAnswered(Intent i) throws Exception {
            String lastKnownPhoneState = null;
            Log.d(TAG, "testing");
            if (i.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
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
                    phoneNumber = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                    return false;
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    //call to be recorded if it was ringing or new outgoing
                    callReceived = true;

                    Log.d(TAG, "OFFHOOK" + phoneNumber);
                    if (ringing == true || outgoing == true) {
                        //ringing=false;
                        //outgoing=false;
                        return true;
                    } else
                        return false;
                } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

                    Log.d(TAG, "IDLE");
                    Log.d("MISSED", "callReceived true");


                    if (ring == true && callReceived == false) {
                        if (!shown) {
                            String fileName = String.valueOf(System.currentTimeMillis());
                            Log.d(TAG, "Missed call from : " + phoneNumber);
                            mGPS = new GPSTracker(getApplicationContext());
                            //  Log.d(TAG, "Latitude" + mGPS.getLatitude() + "");
                            //Log.d(TAG, "Longitude" + mGPS.getLongitude() + "");
                            CallApplication.getWritabledatabase().insert(phoneNumber, fileName, DEFAULT, MISSED);
                            shown = true;
                        }

                    }


                    ringing = false;


                    //Stop recording if it was on
                    if (recording == true) {
                        SharedPreferences sharedPrefs = PreferenceManager
                                .getDefaultSharedPreferences(getApplicationContext());
                        boolean notificationMode = sharedPrefs.getBoolean("prefNotify", false);
                        if (notificationMode)
                            showRecordNotification(phoneNumber);
                        recorder.stop();
                        recorder.release();
                        recording = false;

                        answered = false;
                        outgoing = false;

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
            File sampleDir = Environment.getExternalStorageDirectory();
            File sample = new File(sampleDir.getAbsolutePath() + "/Call Recorder");
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
        if (ringing) {
            from = "from";
        } else {
            from = "to";
        }
        Log.d(TAG, name);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(ContextCompat.getColor(getApplicationContext(), R.color.accent))
                .setContentTitle("MTracker")
                .setAutoCancel(false)
                .setLargeIcon(bm)
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


}
