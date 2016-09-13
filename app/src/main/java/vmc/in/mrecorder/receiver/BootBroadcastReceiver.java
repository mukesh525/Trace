package vmc.in.mrecorder.receiver;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

import vmc.in.mrecorder.activity.Home;
import vmc.in.mrecorder.activity.Login;
import vmc.in.mrecorder.callbacks.TAG;
import vmc.in.mrecorder.myapplication.CallApplication;
import vmc.in.mrecorder.service.CallRecorderServiceAll;
import vmc.in.mrecorder.util.Utils;

/**
 * Created by gousebabjan on 9/3/16.
 */
public class BootBroadcastReceiver extends BroadcastReceiver implements vmc.in.mrecorder.callbacks.TAG {
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    public BootBroadcastReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {

        Intent alarmIntent = new Intent(context, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(context, 1, alarmIntent, 0);
        manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int interval = 10;
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        if(CallApplication.getInstance().getDeviceId().equals(Utils.getFromPrefs(context,DEVICE_ID,UNKNOWN))) {
            if (Utils.isLogin(context)) {
                CallApplication.getInstance().startRecording();
               Log.d("SyncAdapter", "BootBroadcastReceiver");
            } else {
          CallApplication.getInstance().stopRecording();
            }
        }else {
            Utils.isLogoutBackground(context,"Login to MTracker.");

        }

    }
}
