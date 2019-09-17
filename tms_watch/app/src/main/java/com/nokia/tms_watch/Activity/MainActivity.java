package com.nokia.tms_watch.Activity;

import androidx.annotation.NonNull;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.nokia.tms_watch.AppUrls;
import com.nokia.tms_watch.R;
import com.nokia.tms_watch.Service.CheckNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MainActivity extends Activity {

    private static final String STATUS ="Status" ;
    LinearLayout open,ongoing;
    TextView date,shift,tot_open,tot_on;
    LinearLayout backbtn;

    int total_open=0,total_ongoing=0;
    RequestQueue requestQueue;

    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!isMyServiceRunning(CheckNotification.class)){
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
                startForegroundService(new Intent(this, CheckNotification.class));
            else
                startService(new Intent(this, CheckNotification.class));
        }
        requestQueue= Volley.newRequestQueue(this);

        open=findViewById(R.id.open);
        ongoing=findViewById(R.id.ongoing);
        date=findViewById(R.id.date);
        shift=findViewById(R.id.shift);
        tot_open=findViewById(R.id.tot_open);
        tot_on=findViewById(R.id.tot_on);
        backbtn=findViewById(R.id.back_btn);

        showTotal();
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, OpenActivity.class);
                intent.putExtra("status","Open");
                startActivity(intent);

               // showNotification();
            }
        });
        ongoing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this, OpenActivity.class);
                intent.putExtra("status","Ongoing");
                startActivity(intent);
            }
        });
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Quit();
            }
        });
        showTime();
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

                                date.setText(formattedDate + "\n" + localTime);
                                shift.setText("SHIFT:" + shiftTime);
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

    void showTotal(){

        JsonArrayRequest request=new JsonArrayRequest(AppUrls.HOME_URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.v("Length",""+response.length());
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject object=response.getJSONObject(i);
                         if((object.getString(STATUS)).equals("Open"))
                             total_open++;
                         else if((object.getString(STATUS)).equals("Ongoing"))
                             total_ongoing++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                tot_open.setText(""+total_open);
                tot_on.setText(""+total_ongoing);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        requestQueue.add(request);
    }
    void Quit(){
        final AlertDialog alertDialog;
        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage("Do You Want to Exit?");
        dialog.setTitle("Exit");
        dialog.setCancelable(true);
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
                System.exit(0);
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialog=dialog.create();
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Quit();
    }
}
