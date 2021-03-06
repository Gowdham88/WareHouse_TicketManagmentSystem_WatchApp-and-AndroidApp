package com.nokia.tms;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nokia.tms.Adapter.ModelAdapter;
import com.nokia.tms.Model.OpenModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

public class OpenActivity extends Activity {

    static final String URL =AppUrls.HOME_URL;
    static final String RESOURCE_ID="Id";
    static final String STATUS="Status";
    static final String REMARKS="Remarks";
    static final String ID="Id";


    String status="";
    TextView remarks;
    TextView statusText;
    ImageView statusImg;

    ProgressDialog progressDialog;
    private ArrayList<OpenModel> models;
    RecyclerView view;
    ModelAdapter adapter;
    RequestQueue requestQueue;
    RequestQueue queue;

    TextView tot_open;

    boolean isrunning;
     Handler handler;
     boolean stop;

    long elapsedDays,elapsedHours,elapsedMinutes,elapsedSeconds;
    ArrayList<String> elapseTime;
    String openTime="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        status=getIntent().getStringExtra("status");
        statusText=(TextView) findViewById(R.id.status_text);
        statusImg=(ImageView) findViewById(R.id.status_icon);

        view=(RecyclerView) findViewById(R.id.recyclerView);
        view.setLayoutManager(new LinearLayoutManager(this));
        view.setHasFixedSize(true);
        models=new ArrayList<>();
        tot_open=findViewById(R.id.tot_open);
        requestQueue= Volley.newRequestQueue(this);
        queue=Volley.newRequestQueue(this);
        handler=new Handler();

        //View v=findViewById(R.id.recycler_parent_view);
        // remarks=(TextView) v.findViewById(R.id.recycler_status);
        progressDialog=new ProgressDialog(this);
        elapseTime=new ArrayList<>();


        if(status.equals("Open")){
            statusText.setText("Open");
            // remarks.setText("Remarks");
            statusImg.setImageResource(R.drawable.bell_3);
        }else if(status.equals("Close")){
            statusText.setText("Closed");
            // remarks.setText("Status");
            statusImg.setImageResource(R.drawable.smile);
        }else if(status.equals("Ongoing")){
            statusText.setText("Status");
            // remarks.setText("Remarks");
            statusImg.setImageResource(R.drawable.tool_1);
        }
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading..");
        //
        progressDialog.show();
        getResponce();
    }

    private void getResponce() {
        Log.e("status",status);
        String urlAdd="1000";
        if(status.equals("Close")){
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
            urlAdd=""+cal.get(Calendar.HOUR_OF_DAY);
            Log.i("TAG",urlAdd);
        }

        JsonArrayRequest request=new JsonArrayRequest(URL+urlAdd, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int count=0;
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject object=response.getJSONObject(i);
                        if(status.equals(object.getString(STATUS))) {
                            String resId=object.getString(RESOURCE_ID);
                            String status=object.getString(STATUS);
                            long id=object.getLong(ID);
                            String remarks=object.getString(REMARKS);
                            loadDowntime(resId,status,id,remarks,i);

                            count++;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                }
                if(count==0){

                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"No Data Available",Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });

        requestQueue.add(request);
    }

    private void loadDowntime(final String resId, final String status, final long id, final String remarks, final int i) {
        StringRequest request=new StringRequest(Request.Method.GET, AppUrls.DETAILS_URL + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object=new JSONObject(response);
                    openTime=object.getString("OpenTime");

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
                        printDifference(d,currentDate);
                    } catch (ParseException ex) {
                        Log.v("Exception", ex.getLocalizedMessage());
                    }

                    elapseTime.add (":"+(((elapsedDays*24)+elapsedHours)+":"+elapsedMinutes+" min"));

                    OpenModel model;
                    model=new OpenModel(resId,status,id,remarks,""+(((elapsedDays)+":"+elapsedHours)+":"+elapsedMinutes));

                    Log.e("Responce",resId+status+id+remarks+
                            (""+(((elapsedDays*24)+elapsedHours)+":"+elapsedMinutes)));
                    models.add(model);
                    // elapse_time.setText(":"+(((elapsedDays*24)+elapsedHours)+":"+elapsedMinutes+" min"));

                } catch (JSONException e) {
                    e.printStackTrace();

                }
                loadAdapter();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        requestQueue.add(request);

    }

    void printDifference(Date d1, Date d2){

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

        // return (":"+(((elapsedDays*24)+elapsedHours)+":"+elapsedMinutes+" min"));
    }

    void loadAdapter(){
        Log.e("Adapter","Loaded");
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        tot_open.setText(""+models.size());
        Collections.sort(models, new Comparator<OpenModel>() {
            public int compare(OpenModel o1, OpenModel o2) {
                return String.valueOf(o1.id).compareTo(String.valueOf(o2.id));
            }
        });
        adapter=new ModelAdapter(models,OpenActivity.this);
        view.setAdapter(adapter);
        //Toast.makeText(getApplicationContext(),model1.size(),Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();

        if(!isrunning)
            checkAdapter();
    }
    void checkAdapter(){
        isrunning=true;
        final int delay=10000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e("Log","checked");
                checkResponce();
                handler.postDelayed(this,delay);
            }
        },delay);
    }
    void checkResponce(){
        Log.e("Log","checking..");
        Log.e("status",status);
        String urlAdd="1000";
        if(status.equals("Close")){
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
            urlAdd=""+cal.get(Calendar.HOUR_OF_DAY)+1;
            Log.e("URL",urlAdd);
        }
        JsonArrayRequest request=new JsonArrayRequest(URL+urlAdd, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int total=0;
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject object=response.getJSONObject(i);
                        if(status.equals(object.getString(STATUS)))
                            total++;

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if(models.size()!=total){
                    finish();
                    startActivity(getIntent());
                    Log.i("tottallll",""+total);
                }
                Log.i("tottallll---2",""+total);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });
        requestQueue.add(request);
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
