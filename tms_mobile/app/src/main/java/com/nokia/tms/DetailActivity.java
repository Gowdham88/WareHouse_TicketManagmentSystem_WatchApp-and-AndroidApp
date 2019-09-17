package com.nokia.tms;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nokia.tms.Model.DetailModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DetailActivity extends Activity {

    String id;
    static String URL=AppUrls.DETAILS_URL;
    RequestQueue requestQueue;
    TextView ticket_Id,opId,tester_Id,downTime,item,product,line,status,elapse_time;

    ProgressDialog dialog;

    ArrayList<DetailModel> list=new ArrayList<>();

    long elapsedDays,elapsedHours,elapsedMinutes,elapsedSeconds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        requestQueue= Volley.newRequestQueue(this);
        id= getIntent().getStringExtra("Id");
        dialog=new ProgressDialog(this);

        intialize();
        getResponce();
    }

    void intialize(){

        ticket_Id=(TextView) findViewById(R.id.ticket_id);
        tester_Id=(TextView)findViewById(R.id.user_tester);
        opId=(TextView)findViewById(R.id.user_opId);
        downTime=(TextView)findViewById(R.id.user_downtime);
        line=(TextView)findViewById(R.id.user_line);
        status=(TextView)findViewById(R.id.user_status);
        item=(TextView)findViewById(R.id.user_item);
        product=(TextView)findViewById(R.id.user_product);
        elapse_time=findViewById(R.id.user_elapse);

        dialog.setTitle("Loading..");
        dialog.setMessage("Please Wait...");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void getResponce() {
        StringRequest request=new StringRequest(Request.Method.GET, URL + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                for(int i=0;i<10;i++){
                    try {
                        JSONObject object=new JSONObject(response);

                        long ticketId=object.getLong("TicketId");
                        String testerId=object.getString("TesterId");
                        String operationId=object.getString("OperationId");
                        String openTime=object.getString("OpenTime");
                        String ItemCode=object.getString("ItemCode");
                        String ProductCode=object.getString("ProductCode");
                        String Line=object.getString("Line");
                        String Status=object.getString("Status");


                        Calendar cal=Calendar.getInstance();
                        Date currentDate=cal.getTime();
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


                        ticket_Id.setText(":"+String.valueOf(ticketId));
                        tester_Id.setText(":"+testerId);
                        opId.setText(":"+operationId);
                        downTime.setText(":"+openTime);
                        item.setText(":"+ItemCode);
                        product.setText(":"+ProductCode);
                        line.setText(":"+Line);
                        status.setText(":"+Status);
                        elapse_time.setText(":"+(((elapsedDays*24)+elapsedHours)+":"+elapsedMinutes+" min"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                        dialog.dismiss();
                    }
                    dialog.dismiss();
                }
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
