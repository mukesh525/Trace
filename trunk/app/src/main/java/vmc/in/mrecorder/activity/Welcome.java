package vmc.in.mrecorder.activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import vmc.in.mrecorder.R;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.util.Utils;

public class Welcome extends AppCompatActivity implements TAG{
    private static int SPLASH_TIME_OUT = 3000;
    Button btn;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
      //  sendStickyNotification("Loged in");
//        CallApplication.sp.edit().putInt(TYPE, 1).commit();
//        if ( CallApplication.sp.getInt(TYPE, 0) == 0) {
//            startService(CallApplication.all);
//        } else if ( CallApplication.sp.getInt(TYPE, 0) == 1) {
//            stopService(CallApplication.all);
//            //  stopService(opt);
//        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (Utils.isLogin(Welcome.this)) {
                    i = new Intent(Welcome.this, Home.class);
                } else {
                    i = new Intent(Welcome.this, Login.class);
                }
                startActivity(i);

            }
        }, SPLASH_TIME_OUT);

    }
    private void sendStickyNotification(String message) {

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.cube)
                .setContentTitle("title")
                .setAutoCancel(false)
                .setOngoing(true)
                .setSound(defaultSoundUri)
                .setContentText(message)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, Login.class), 0));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
