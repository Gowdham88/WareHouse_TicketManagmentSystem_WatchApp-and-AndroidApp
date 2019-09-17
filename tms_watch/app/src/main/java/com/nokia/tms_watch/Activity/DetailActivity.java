package com.nokia.tms_watch.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nokia.tms_watch.AppUrls;
import com.nokia.tms_watch.Model.DetailModel;
import com.nokia.tms_watch.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DetailActivity extends Activity {

    String id,mStatus,mRemarks;
    static String URL= AppUrls.DETAIL_URL;
    RequestQueue requestQueue;
    TextView status_text,openTic,time;
    ImageView status_img;
    ImageView backbtn;
    Date currentDate;
    TextView ticket_Id,opId,tester_Id,downTime,item,product,line,status;

    ProgressDialog dialog;

    ArrayList<DetailModel> list=new ArrayList<>();

    long elapsedDays,elapsedHours,elapsedMinutes,elapsedSeconds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        status_img=findViewById(R.id.status_icon);
        mStatus=getIntent().getStringExtra("status");
        mRemarks=getIntent().getStringExtra("Remarks");
        openTic=findViewById(R.id.tot_open);
        backbtn=findViewById(R.id.back_btn);

        openTic.setText(getIntent().getStringExtra("openTic"));
        Log.e("Detail",mStatus);

        if(mStatus.equals("Open")){
            status_img.setImageResource(R.drawable.bell_3);
        }
        else {
            status_img.setImageResource(R.drawable.tool_1);
        }

        requestQueue= Volley.newRequestQueue(this);
        id= getIntent().getStringExtra("Id");
        Log.e("Get Id",id);
        dialog=new ProgressDialog(this);

        intialize();
        showTime();
        getResponce();
    }
    void intialize(){

        time=findViewById(R.id.time);

        ticket_Id=findViewById(R.id.ticket_id);
        tester_Id=findViewById(R.id.user_tester);
        opId=findViewById(R.id.user_opId);
        downTime=findViewById(R.id.user_elapse);
        //line=findViewById(R.id.user_line);
        status=findViewById(R.id.user_remarks);
        //item=findViewById(R.id.user_item);
        //product=findViewById(R.id.user_product);

        dialog.setTitle("Loading..");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailActivity.super.onBackPressed();
            }
        });
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
                                Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
                                Date currentLocalTime = cal.getTime();
                                DateFormat dat = new SimpleDateFormat("HH:mm:ss ");
                                dat.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                                String localTime = dat.format(currentLocalTime);


                                time.setText(localTime);
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

    private void getResponce() {
        StringRequest request=new StringRequest(Request.Method.GET, URL + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Detail",response);
                    try {
                        JSONObject object=new JSONObject(response);

                        long ticketId=object.getLong("TicketId");
                        String testerId=object.getString("TesterId");
                        String operationId=object.getString("OperationId");
                        String openTime=object.getString("OpenTime");


                        Calendar cal=Calendar.getInstance();
                        currentDate=cal.getTime();
                        SimpleDateFormat df = new SimpleDateFormat("mm/dd/yyyy hh:mm:ss aa");
                        df.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));
                        String s= df.format(currentDate);
                        String opTime="";
                        try {
                            currentDate = df.parse(s);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat sdf=new SimpleDateFormat("mm/dd/yyyy hh:mm:ss aa");
                        try {
                            Date d = sdf.parse(openTime);
                            Log.i("date",openTime);
                            Log.i("date",""+d);
                            printDifference(d,currentDate);
                        } catch (ParseException ex) {
                            Log.v("Exception", ex.getLocalizedMessage());
                        }

                        // DateTime dt = formatter.parseDateTime(string);
                        ticket_Id.setText(":"+String.valueOf(ticketId));
                        tester_Id.setText(":"+testerId);
                        opId.setText(":"+operationId);
                        downTime.setText(":"+(((elapsedDays*24)+elapsedHours)+":"+elapsedMinutes+":"+elapsedSeconds));
                        //item.setText(":"+ItemCode);
                        //product.setText(":"+ProductCode);
                        //line.setText(":"+Line);
                        status.setText(":"+mRemarks);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                    dialog.dismiss();
                }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.dismiss();
            }
        });

        requestQueue.add(request);
    }
     void printDifference(Date d1,Date d2){

         long secondsInMilli = 1000;
         long minutesInMilli = secondsInMilli * 60;
         long hoursInMilli = minutesInMilli * 60;
         long daysInMilli = hoursInMilli * 24;
        long different = d2.getTime() - d1.getTime();

        elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        elapsedSeconds = different / secondsInMilli;

    }

}
