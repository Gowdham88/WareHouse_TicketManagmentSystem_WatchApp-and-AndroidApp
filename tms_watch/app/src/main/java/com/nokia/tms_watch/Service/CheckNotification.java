package com.nokia.tms_watch.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nokia.tms_watch.AppUrls;
import com.nokia.tms_watch.R;
import com.nokia.tms_watch.Activity.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

public class CheckNotification extends Service {

    private String TAG = "NOTIFICATION Service";
    private String CHANNEL_ID = "11";
    private int NOTIFICATION_ID = 1;
    RequestQueue requestQueue;
    private static final String PREF="Pref";
    private static final String UPDATED_ID="ticketId";
    SharedPreferences sharedPreferences;
    static int lastUpdatedticket = 0;
    int counter;



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startTimer();
        return START_STICKY;
    }

    private Timer timer;
    private TimerTask timerTask;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                checkNotification();
            }
        };
        timer.schedule(timerTask, 10000, 10000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    @Override
    public void onCreate() {
        super.onCreate();

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            CharSequence name = "Background Service";
            String description = "service";
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel("123", name, importance);
            channel.setLightColor(Color.BLUE);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            Notification notification=new Notification.Builder(this,"123")
                    .setContentTitle("Service..")
                    .setContentText("Running")
                    .setOngoing(true)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .setSmallIcon(R.drawable.dwf_png)
                    .build();
            startForeground(2,notification);
        }
        else
            startForeground(3, new Notification());


        sharedPreferences=getSharedPreferences(PREF, Context.MODE_PRIVATE);
        if(sharedPreferences.getInt(UPDATED_ID,0)!=0)
            lastUpdatedticket=sharedPreferences.getInt(UPDATED_ID,0);
        requestQueue =  Volley.newRequestQueue(this);

        // Get the HandlerThread's Looper and use it for our Handler

    }


    @Override
    public void onDestroy() {
        Log.e("service_s","stopped");
        stoptimertask();
        super.onDestroy();
        try {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    void checkNotification() {
        StringRequest request = new StringRequest(Request.Method.GET, AppUrls.NOTIFICATION_CHECK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("check", "Checking");
                int temp = Integer.parseInt(response);
                if (temp != lastUpdatedticket) {
                    if(lastUpdatedticket!=0){
                        getDetails(String.valueOf(temp));
                        lastUpdatedticket = temp;
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putInt(UPDATED_ID,temp);
                        editor.apply();
                    }else {
                        lastUpdatedticket = temp;
                        SharedPreferences.Editor editor=sharedPreferences.edit();
                        editor.putInt(UPDATED_ID,temp);
                        editor.apply();
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);
    }

    private void getDetails(String Id) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        String URL = AppUrls.NOTIFICATION_URL + Id;
        StringRequest request = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("res", response);
                    JSONObject object = new JSONObject(response);

                    long ticketId = object.getLong("TicketId");
                    long openTicket = object.getLong("TotalOpenTickets");
                    String OperationId = object.getString("OperationId");

                    showNotification("" + ticketId, "" + openTicket, OperationId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //dialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //dialog.dismiss();
            }
        });

        requestQueue.add(request);

    }

    void showNotification(String title, String oopen, String opId) {
        createNotificationChannel();

        RemoteViews notificationView = new RemoteViews(getPackageName(),
                R.layout.layout_notification);

        notificationView.setTextViewText(R.id.not_ticketId, "Ticket Id :" + title);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);

        Intent intent = new Intent(this, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setSmallIcon(R.drawable.dwf)
                .setContentTitle(title)
                .setContentInfo("Click to Open")
                .setColor(ContextCompat.getColor(this, R.color.nokia_blue))
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setChannelId(CHANNEL_ID)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setCustomContentView(notificationView);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());

    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "New Channel";
            String description = "Nothing";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
