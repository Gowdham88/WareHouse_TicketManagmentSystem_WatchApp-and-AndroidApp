package com.nokia.tms;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.content.Intent;


import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

public class ResponceActivity extends Activity {

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
    private Button trend;
    ImageView mainOpen,mainClose,mainOngoing;

    //JSON PARSHING
    private RequestQueue requestQueue;
    OpenModel openModel;
    ArrayList<OpenModel> models;

    // All static variables
    private TextView tot_open,tot_close,tot_on;
    private int total_open,total_ongoing,total_close,total;

    String date;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responce);


        date=getIntent().getStringExtra("date");
        Toast.makeText(getApplicationContext(),date,Toast.LENGTH_SHORT).show();
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
        String url;
        final int[] tickets=new int[3];
        final int tot=0;
        for(int i=0;i<3;i++){
            if(i==0)  url=AppUrls.URLOPEN;
            else if(i==1) url=AppUrls.URLONGOING;
            else url=AppUrls.URLCLOSE;
            final int finalI = i;
            Log.e("URL",url+date);
            JsonArrayRequest request=new JsonArrayRequest(url+date, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    total=total+response.length();
                    if(finalI ==0)  {
                        total_open=response.length();
                        tot_open.setText(String.valueOf(total_open));
                    }
                    else if(finalI ==1) {
                        total_ongoing=response.length();
                        tot_on.setText(String.valueOf(total_ongoing));
                    }
                    else{
                        total_close=response.length();
                        tot_close.setText(String.valueOf(total_close));
                    }
                    totalTic.setText(String.valueOf(total));
                    Toast.makeText(getApplicationContext(),""+response.length(),Toast.LENGTH_SHORT).show();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            requestQueue.add(request);
        }

    }
    @Override
    protected void onDestroy() {
        //stopService(sIntent);
        super.onDestroy();
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

   /* public void setTime() {
        int hour = calendar1.get(Calendar.HOUR_OF_DAY);
        int min = calendar1.get(Calendar.MINUTE);

        TimePickerDialog dialog=new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                showTime();
            }
        },hour,min,false);
        dialog.setTitle("Pick Time");
        dialog.show();

    } */

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
    }

    void nextActivity(String status){
        Intent i=new Intent(ResponceActivity.this,ResponceOpen.class);
        i.putExtra("status",status);
        i.putExtra("date",date);
        startActivity(i);
    }
}