package com.nokia.tms;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nokia.tms.Model.OpenModel;
import com.nokia.tms.Service.Notify;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends Activity {

    private static final String URL=AppUrls.HOME_URL;
    private static final String CHANNEL_ID = "my_channel";
    private static final int NOTIFICATION_ID = 101;
    private static final String TAG = "TAG";
    private static final String STATUS = "Status";

    TextView mDate;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    private TimePicker timePicker1;
    private TextView time;
    TextView totalTic;
    private Calendar calendar1;
    private String format = "";

    AlertDialog.Builder builder;
    AlertDialog dialog;

    Intent sIntent;
    String shiftTime;
    String shiftTimeResponce="A";
    private Button trend;
    ImageView mainOpen,mainClose,mainOngoing;

    //JSON PARSHING
    private RequestQueue requestQueue;
    OpenModel openModel;
    ArrayList<OpenModel> models;

    // All static variables
    private TextView tot_open,tot_close,tot_on;
    private int total_open,total_ongoing,total_close;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isMyServiceRunning(Notify.class)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                 scheduleJob();
            }
        }

        intialize();
        Methods();
       // getToken();

    }
    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    void showTotal(){
        String urlAdd="100";
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
        urlAdd=""+calendar.get(Calendar.HOUR_OF_DAY);

        JsonArrayRequest request=new JsonArrayRequest(AppUrls.HOME_URL+1000, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int count=0;
                Log.v("Length",""+response.length());
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject object=response.getJSONObject(i);
                        if((object.getString(STATUS)).equals("Open")){
                            total_open++;
                            count++;
                        }
                        else if((object.getString(STATUS)).equals("Ongoing")){
                            total_ongoing++;
                            count++;
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                tot_open.setText(""+total_open);
                tot_on.setText(""+total_ongoing);
                totalTic.setText(""+count);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        JsonArrayRequest request1=new JsonArrayRequest(AppUrls.HOME_URL+urlAdd, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.v("Length",""+response.length());
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject object=response.getJSONObject(i);
                        if((object.getString(STATUS)).equals("Close"))
                            total_close++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                tot_close.setText(""+total_close);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
        requestQueue.add(request1);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void scheduleJob() {
        ComponentName componentName = new ComponentName(this, Notify.class);
        JobInfo info;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            info = new JobInfo.Builder(1234, componentName)
                    .setMinimumLatency(5 * 1000)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .build();
        }else {
                info = new JobInfo.Builder(1234, componentName)
                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                        .setPersisted(true)
                        .setPeriodic(15 *  1000)
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void cancelJob(View v) {
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(123);
        Log.d(TAG, "Job cancelled");
    }

    @Override
    protected void onDestroy() {
        //stopService(sIntent);
        super.onDestroy();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void intialize() {
        requestQueue=Volley.newRequestQueue(this);
        models=new ArrayList<>();
        mainOpen = (ImageView) findViewById(R.id.main_open);
        mainClose=(ImageView)findViewById(R.id.main_close);
        mainOngoing=(ImageView)findViewById(R.id.main_ongoing);
        trend = (Button)findViewById(R.id.btnTrend);
        mDate=(TextView) findViewById(R.id.inputDate);
        time=(TextView)findViewById(R.id.inputShift);
        totalTic=(TextView)findViewById(R.id.totaltic);

        tot_open=findViewById(R.id.tot_open);
        tot_on=findViewById(R.id.tot_on);
        tot_close=findViewById(R.id.tot_close);
        calendar1=Calendar.getInstance();

        showTime();
        showTotal();
    }

    public void setTime() {
        int hour = calendar1.get(Calendar.HOUR_OF_DAY);
        int min = calendar1.get(Calendar.MINUTE);

        TimePickerDialog dialog=new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                int curentHour = hourOfDay;
                if (curentHour >= 6 && curentHour < 14) {
                    shiftTimeResponce = "A";
                } else if (curentHour >= 14 && curentHour < 22) {
                    shiftTimeResponce = "B";
                } else {
                    shiftTimeResponce = "C";
                }
            }
        },hour,min,true);
        dialog.setTitle("Pick Time");
        dialog.show();

    }

    void showTime(){

        Thread t= new Thread(){
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                String shiftTime;
                                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                                Date currentLocalTime = cal.getTime();
                                DateFormat dat = new SimpleDateFormat("HH:mm:ss ");
                                dat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                                String localTime = dat.format(currentLocalTime);

                                int curentHour = cal.get(Calendar.HOUR_OF_DAY);
                                if (curentHour >= 6 && curentHour < 14) {
                                    shiftTime = "A";
                                } else if (curentHour >= 14 && curentHour < 22) {
                                    shiftTime = "B";
                                } else {
                                    shiftTime = "C";
                                }

                                Date currentTime = Calendar.getInstance().getTime();
                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                                String formattedDate = df.format(currentTime);

                                mDate.setText(formattedDate + "\n" + localTime);
                                time.setText("SHIFT:" + shiftTime);
                            }
                        });

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        t.start();
    }



    private void Methods() {
        mainOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity("Open");
            }
        });
        mainClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity("Close");
            }
        });
        mainOngoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextActivity("Ongoing");
            }
        });
        trend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               /* builder=new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Development").setCancelable(false).setMessage("It's under development")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog=builder.create();
                dialog.show(); */
                Intent i = new Intent(MainActivity.this, StatusActivity.class);
                startActivity(i);
            }
        });
        mDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal=Calendar.getInstance();
                int year=cal.get(Calendar.YEAR);
                int month=cal.get(Calendar.MONTH);
                int day=cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog=new DatePickerDialog(MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        mDateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String date=""+year+""+(month<9?("0"+(month+1)):month+1)+""+(dayOfMonth<10?("0"+dayOfMonth):dayOfMonth)+shiftTimeResponce;
                Intent i=new Intent(MainActivity.this,ResponceActivity.class);
                i.putExtra("date",date);
                startActivity(i);
            }
        };
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });
    }

    void nextActivity(String status){
        Intent i=new Intent(MainActivity.this,OpenActivity.class);
        i.putExtra("status",status);
        startActivity(i);
    }
}