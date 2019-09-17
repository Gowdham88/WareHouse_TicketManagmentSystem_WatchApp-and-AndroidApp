package com.nokia.tms_watch.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.nokia.tms_watch.Service.CheckNotification;

public class Restarter extends BroadcastReceiver {
    String TAG="Receiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Broadcast Listened", "Service tried to stop");
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.e("Broadcast Listened: ...", "restart");
            context.startForegroundService(new Intent(context, CheckNotification.class));
        } else {
            context.startService(new Intent(context, CheckNotification.class));
        }


    }
}
