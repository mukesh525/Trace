package vmc.in.mrecorder.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import vmc.in.mrecorder.service.CallRecorderServiceAll;

/**
 * Created by gousebabjan on 9/3/16.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    public BootBroadcastReceiver() {
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, CallRecorderServiceAll.class));
        Log.d("TempLog", "BootBroadcastReceiver");
    }
}
