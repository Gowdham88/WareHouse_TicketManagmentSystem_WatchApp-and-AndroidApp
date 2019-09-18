package com.nokia.tms;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
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
import com.nokia.tms.Adapter.ResponceAdapter;
import com.nokia.tms.Model.OpenModel;
import com.nokia.tms.Model.OpenModelResponce;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ResponceOpen extends Activity {

    String URL;
    static final String RESOURCE_ID="Id";
    static final String STATUS="Status";
    static final String REMARKS="Remarks";
    static final String ID="Id";


    String status="";
    TextView remarks;
    TextView statusText;
    ImageView statusImg;

    ProgressDialog progressDialog;
    private ArrayList<OpenModelResponce> models;
    RecyclerView view;
    ResponceAdapter adapter;
    RequestQueue requestQueue;
    RequestQueue queue;

    TextView tot_open;

    boolean isrunning;
    Handler handler;
    boolean stop;

    long elapsedDays,elapsedHours,elapsedMinutes,elapsedSeconds;
    ArrayList<String> elapseTime;
    String openTime="";
    String date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_responce_open);

        status=getIntent().getStringExtra("status");
        date=getIntent().getStringExtra("date");

        statusText=(TextView) findViewById(R.id.status_text);
        statusImg=(ImageView) findViewById(R.id.status_icon);

        view=(RecyclerView) findViewById(R.id.recyclerViewRes);
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
            URL=AppUrls.URLOPEN;
            statusImg.setImageResource(R.drawable.bell_3);
        }else if(status.equals("Close")){
            statusText.setText("Closed");
            URL=AppUrls.URLCLOSE;
            statusImg.setImageResource(R.drawable.smile);
        }else if(status.equals("Ongoing")){
            statusText.setText("Status");
            URL=AppUrls.URLONGOING;
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

        JsonArrayRequest request=new JsonArrayRequest(URL+date, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                int count=0;
                for(int i=0;i<response.length();i++){
                    try {
                            JSONObject object=response.getJSONObject(i);
                            long id=object.getLong(ID);
                            String remarks=object.getString(REMARKS);
                            String time=object.getString("Time");
                            count++;
                            OpenModelResponce openModelResponce=new OpenModelResponce(time,id,remarks);

                            models.add(openModelResponce);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                }
                if(count==0){
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"No Data Available",Toast.LENGTH_SHORT).show();
                }
                loadAdapter();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });

        requestQueue.add(request);
    }
    void loadAdapter(){
        Log.e("Adapter","Loaded");
        if(progressDialog.isShowing())
            progressDialog.dismiss();
        tot_open.setText(""+models.size());
        adapter=new ResponceAdapter(models,ResponceOpen.this);
        view.setAdapter(adapter);
        //Toast.makeText(getApplicationContext(),model1.size(),Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();

       // if(!isrunning)
         //   checkAdapter();
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
