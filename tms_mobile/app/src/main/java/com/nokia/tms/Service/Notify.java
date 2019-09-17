package com.nokia.tms.Service;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nokia.tms.AppUrls;
import com.nokia.tms.R;
import com.nokia.tms.SplashActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class Notify extends JobService {
    private JobExecuter executer;

    private static final String TAG = "JobService";
    private boolean jobCancelled = false;

    private String CHANNEL_ID = "11";
    private int NOTIFICATION_ID = 1;
    RequestQueue requestQueue;
    private static final String PREF="Pref";
    private static final String UPDATED_ID="ticketId";
    SharedPreferences sharedPreferences;
    static int lastUpdatedticket = 0;
    @Override
    public boolean onStartJob(final JobParameters params) {
        Log.d(TAG, "Job started");
        requestQueue=Volley.newRequestQueue(this);
        sharedPreferences=getSharedPreferences(PREF, Context.MODE_PRIVATE);
        if(sharedPreferences.getInt(UPDATED_ID,0)!=0)
            lastUpdatedticket=sharedPreferences.getInt(UPDATED_ID,0);
        doBackgroundWork(params);
        Toast.makeText(getApplicationContext(),"running",Toast.LENGTH_SHORT).show();
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                    if (jobCancelled) {
                        return;
                    }
                    try {
                        checkNotification();
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                }

                Log.d(TAG, "Job finished");
                jobFinished(params, false);
                jobRefresh();
            }
        }).start();
    }

    private void jobRefresh() {

        ComponentName componentName = new ComponentName(this, Notify.class);
        JobInfo info;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            info = new JobInfo.Builder(1234, componentName)
                    .setMinimumLatency(60 * 1000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
        }else {
            info = new JobInfo.Builder(1234, componentName)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setPeriodic(60 *  1000)
                    .build();

        }
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if (resultCode == JobScheduler.RESULT_SUCCESS) {
            Log.d(TAG, "Job scheduled");
        } else {
            Log.d(TAG, "Job scheduling failed");
        }

    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "Job cancelled before completion");
        jobCancelled = true;
        executer.cancel(true);
        return true;
    }

    void checkNotification() {
        StringRequest request = new StringRequest(Request.Method.GET, AppUrls.NOTIFICATION_CHECK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("check", "Checking");
               /* String s3="";
                try {
                    JSONObject object=new JSONObject(response);
                    String s=object.getString("fields");
                    JSONObject object1=new JSONObject(s);
                    String s1=object1.getString("val");
                    JSONObject object2=new JSONObject(s1);
                    s3=object2.getString("stringValue");
                } catch (JSONException e) {
                    e.printStackTrace();
                }*/
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
