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
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.datahandler.HelperCallRecordings;
import vmc.in.mrecorder.myapplication.CallApplication;

public class CallRecorderServiceAll extends Service implements TAG {

    static MediaRecorder recorder;
    public static boolean recording;
    public boolean ringing, answered, outgoing;
    static boolean ring = false;
    static boolean callReceived = false;
    static boolean shown = false;

    //Broadcast receiver for calls
    CallBroadcastReceiver cbr;

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

        public CallBroadcastReceiver() {
        }

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            try {
                answered = checkAnswered(arg1);

                Log.e("answer", "" + String.valueOf(answered));
                if (answered == true) {
                    Log.e("answer", "" + String.valueOf(answered));
                    startRecording();
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
            Log.e("AudioSource", manufacturer);
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN_MR1 ||
                    Build.VERSION.SDK_INT == Build.VERSION_CODES.JELLY_BEAN
                    || Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {

                if (Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")) {
                    audiofile = new File(sample.getAbsolutePath() + "/sound" + fileName + ".amr");
                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(audiofile.getAbsolutePath());
                    Log.e("AudioSource", "JELLY_BEAN" + "VOICE_CALL" + " " + "3gp");
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
            Log.e("recorder", "" + String.valueOf(recording));

            if (recording == false) {
                Log.e("recording", "started");
                recorder.start();
            } else
                Log.e("recording ", "recording");

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
            Log.e("call intent", "testing");
            if (i.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                phoneNumber = i.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

                outgoing = true;
                return false;
            } else {
                Bundle b = i.getExtras();
                String state = b.getString(TelephonyManager.EXTRA_STATE);

                Log.d("aa", state);

                if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    //Check to see if call was answered later
                    ringing = true;
                    ring = true;
                    shown = false;
                    callReceived = false;

                    Log.d("MISSED", "Ringing true");

                    phoneNumber = b.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);

                    return false;
                } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    //call to be recorded if it was ringing or new outgoing
                    callReceived = true;
                    //Log.d("MISSED", "callReceived = true");
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
                    //Log.d("MISSED", "callReceived true");
                    //Toast.makeText(getApplicationContext(),"ring ",Toast.LENGTH_SHORT).show();}

                    if (ring == true && callReceived == false) {
                        if (!shown) {
                            String fileName = String.valueOf(System.currentTimeMillis());
                            Log.d("MISSED", "Missed call from : " + phoneNumber);
                            // Toast.makeText(getApplicationContext(),"Missed call from : " + phoneNumber,Toast.LENGTH_SHORT).show();

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
