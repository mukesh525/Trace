package vmc.in.mrecorder.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;

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

    //Phone number
    String phoneNumber;

    //Database class
    // HelperCallRecordings hcr;

    //To check service running or not
    static boolean running = false;


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("permanent service", "created");
        try {
            //To avoid running of service again and again
            //Toast.makeText(getApplicationContext(), "service started", 2000).show();
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

        try {
            //hcr = new HelperCallRecordings(this);

            cbr = new CallBroadcastReceiver();
            IntentFilter ifl = new IntentFilter();
            ifl.addAction("android.intent.action.PHONE_STATE");
            ifl.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
            registerReceiver(cbr, ifl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        try {
            running = false;

            unregisterReceiver(cbr);

            //  CallApplication.getWritableDatabase().closeDatabase();
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
                    Log.e("answer", "" + String.valueOf(Build.MANUFACTURER));
                    Log.e("answer", "" + String.valueOf(answered));
                    Log.e("answer", "" + String.valueOf(ringing));
                    Log.e("answer", "" + String.valueOf(phoneNumber));
                    if (Build.MANUFACTURER.equalsIgnoreCase("motorola")) {
                        if (answered && ringing) {
                            CallApplication.getWritableDatabase().insert(phoneNumber, fileName, "empty", INCOMING);
                            Log.e("answer", "" + "incoming inserted");
                        }
                        if (answered && !ringing) {
                            CallApplication.getWritableDatabase().insert(phoneNumber, fileName, "empty", OUTGOING);
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

            File sampleDir = Environment.getExternalStorageDirectory();
            File sample = new File(sampleDir.getAbsolutePath() + "/Call Recorder");
            sample.mkdirs();

            String fileName = String.valueOf(System.currentTimeMillis());
            // String fileName = DateFormat.getDateTimeInstance().format(new Date());
            File audiofile;
            String manufacturer = Build.MANUFACTURER;
            Log.e(TAG, manufacturer);
            if (Build.MANUFACTURER.equals("motorola")){
                recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                audiofile = new File(sample.getAbsolutePath() + "/sound" + fileName + ".wav");

                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setAudioSamplingRate(48000);
                recorder.setOutputFile(audiofile.getAbsolutePath());
            }
           else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1 ||
                    Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN
                    || Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

                if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
                    audiofile = new File(sample.getAbsolutePath() + "/sound" + fileName + ".amr");
                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(audiofile.getAbsolutePath());
                    Log.e(TAG, "JELLY_BEAN" + "VOICE_CALL" + " " + "3gp");
                } else {
                    recorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
                    audiofile = new File(sample.getAbsolutePath() + "/sound" + fileName + ".3gp");
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(audiofile.getAbsolutePath());
                }
            } else {
                audiofile = new File(sample.getAbsolutePath() + "/sound" + fileName + ".amr");
                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                recorder.setOutputFile(audiofile.getAbsolutePath());
            }

            recorder.prepare();
            AudioManager audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
            int deviceCallVol = audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL);
            audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
            Log.d(TAG, " " + String.valueOf(recording));

            if (recording == false) {
                Log.d(TAG, "started");
                recorder.start();
            } else
                Log.d(TAG, "recording");

            recording = true;

            if (phoneNumber != null) {
                if (ringing == true)
                    CallApplication.getWritableDatabase().insert(phoneNumber, fileName, audiofile.getAbsolutePath(), INCOMING);
                else if (outgoing == true)
                    //hcr.insert(phoneNumber, fileName, audiofile.getAbsolutePath(), OUTGOING);
                    CallApplication.getWritableDatabase().insert(phoneNumber, fileName, audiofile.getAbsolutePath(), OUTGOING);

            }

        }

        public boolean checkAnswered(Intent i) throws Exception {
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
                    //if(ring){
                    // Log.d("MISSED", "ring true");
                    // Toast.makeText(getApplicationContext(),"ring true",Toast.LENGTH_SHORT).show();}
                    //if(callReceived){
                    Log.d(TAG, "IDLE");
                    Log.d("MISSED", "callReceived true");
                    //Toast.makeText(getApplicationContext(),"ring ",Toast.LENGTH_SHORT).show();}

                    if (ring == true && callReceived == false) {
                        if (!shown) {
                            String fileName = String.valueOf(System.currentTimeMillis());
                            Log.d(TAG, "Missed call from : " + phoneNumber);
                            mGPS = new GPSTracker(getApplicationContext());
                            Log.d(TAG, "Latitude" + mGPS.getLatitude() + "");
                            Log.d(TAG, "Longitude" + mGPS.getLongitude() + "");
                            CallApplication.getWritableDatabase().insert(phoneNumber, fileName, DEFAULT, MISSED);
                            shown = true;
                        }

                    }


                    ringing = false;


                    //Stop recording if it was on
                    if (recording == true) {
                        SharedPreferences sp = getSharedPreferences("vmc.in.callrecorder", Context.MODE_PRIVATE);
                        String notificationMode = sp.getString("notificationmode", "false");

                        //Generate notificaton only if shared preferences is true for notification
                        if (notificationMode.equals("true"))
                            // generateNotification();
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
    }


}
