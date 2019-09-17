package com.nokia.tms_watch.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nokia.tms_watch.Adapter.ModelAdapter;
import com.nokia.tms_watch.AppUrls;
import com.nokia.tms_watch.Model.OpenModel;
import com.nokia.tms_watch.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class OpenActivity extends Activity {

    static final String URL = AppUrls.HOME_URL;
    static final String RESOURCE_ID="Id";
    static final String STATUS="Status";
    static final String REMARKS="Remarks";
    static final String ID="Id";


    String status="";
    String statCheck;
    TextView statusText,openTic,time;
    ImageView statusImg;
    ImageView backbtn;

    ProgressDialog progressDialog;
    private ArrayList<OpenModel> models;
    ModelAdapter adapter;
    GridView view;
    RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open);

        status=getIntent().getStringExtra("status");
        statusImg=findViewById(R.id.status_icon);

        openTic=findViewById(R.id.tot_open);
        view=findViewById(R.id.listView);
        models=new ArrayList<>();
        requestQueue= Volley.newRequestQueue(this);;
        progressDialog=new ProgressDialog(this);
        backbtn=findViewById(R.id.back_btn);
        time=findViewById(R.id.time);


        if(status.equals("Open")){
            statusImg.setImageResource(R.drawable.bell_3);
            statCheck="Open";
        }else if(status.equals("Close")){
            statusImg.setImageResource(R.drawable.smile);
        }else if(status.equals("Ongoing")){
            statusImg.setImageResource(R.drawable.tool_1);
            statCheck="Ongoing";
        }
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading..");
        progressDialog.show();


        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenActivity.super.onBackPressed();
            }
        });
        showTime();
        getResponce();
    }

    private void getResponce() {
        Log.e("status",status);
        JsonArrayRequest request=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.v("Length",""+response.length());
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject object=response.getJSONObject(i);
                        OpenModel model=new OpenModel(object.getString(RESOURCE_ID),object.getString(STATUS),object.getLong(ID),object.getString(REMARKS));
                        //Log.e("Remarks",object.getString("Remarks"));
                        if(status.equals(object.getString(STATUS)))
                                models.add(model);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                }
                Log.v("Length",""+models.size());
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
        progressDialog.dismiss();
        adapter=new ModelAdapter(models,OpenActivity.this);
        view.setAdapter(adapter);
        openTic.setText(""+models.size());
        adapter.notifyDataSetChanged();
        checkAdapter();
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

                                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");

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
    void checkAdapter(){
        final int delay=10000;
        final Handler handler=new Handler();
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
        final ArrayList<OpenModel> tempModel=new ArrayList<>();
        Log.e("Log","checking..");
        JsonArrayRequest request=new JsonArrayRequest(URL, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject object=response.getJSONObject(i);
                        OpenModel model=new OpenModel(object.getString(RESOURCE_ID),object.getString(STATUS),object.getLong(ID),object.getString(REMARKS));
                        if(status.equals(object.getString(STATUS)))
                            tempModel.add(model);


                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                }
                for (int i=0;i<response.length();i++){
                    try{
                        if(models.get(i).getTicketStatus().equals(tempModel.get(i).getTicketStatus())){
                            models=tempModel;
                            adapter=new ModelAdapter(models,OpenActivity.this);
                            adapter.notifyDataSetChanged();
                            openTic.setText(""+models.size());
                        }
                    }
                    catch (IndexOutOfBoundsException e){
                        models=tempModel;
                        adapter=new ModelAdapter(models,OpenActivity.this);
                        adapter.notifyDataSetChanged();
                        openTic.setText(""+models.size());
                    }

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
    @Override
    protected void onRestart() {
        super.onRestart();
        finish();
        startActivity(getIntent());
    }
}
