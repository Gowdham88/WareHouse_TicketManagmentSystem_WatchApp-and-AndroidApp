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


import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.nokia.tms.Adapter.ModelAdapter;
import com.nokia.tms.Model.OpenModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
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

    TextView tot_open;

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
        tot_open=findViewById(R.id.tot_open);
        models=new ArrayList<>();
        requestQueue= Volley.newRequestQueue(this);
        View v=findViewById(R.id.recycler_parent_view);
        remarks=(TextView) v.findViewById(R.id.recycler_status);
        progressDialog=new ProgressDialog(this);


        if(status.equals("Open")){
            statusText.setText("Open");
            remarks.setText("Remarks");
            statusImg.setImageResource(R.drawable.bell_3);
        }else if(status.equals("Close")){
            statusText.setText("Closed");
            remarks.setText("Status");
            statusImg.setImageResource(R.drawable.smile);
        }else if(status.equals("Ongoing")){
            statusText.setText("Status");
            remarks.setText("Remarks");
            statusImg.setImageResource(R.drawable.tool_1);
        }
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Loading..");
        progressDialog.show();
        getResponce();
    }

    private void getResponce() {
        Log.e("status",status);
        String urlAdd="1000";
        if(status.equals("Close")){
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30"));
            urlAdd=""+cal.get(Calendar.HOUR_OF_DAY);
        }

        JsonArrayRequest request=new JsonArrayRequest(URL+urlAdd, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                for(int i=0;i<response.length();i++){
                    try {
                        JSONObject object=response.getJSONObject(i);
                        Log.e("Remarks",object.getString("Remarks"));
                        if(status.equals(object.getString(STATUS))) {
                            OpenModel model;
                            if(status=="Open"){
                                model=new OpenModel(object.getString(RESOURCE_ID),object.getString(STATUS),object.getLong(ID),object.getString(REMARKS));
                            }
                            else {
                                model=new OpenModel(object.getString(RESOURCE_ID),object.getString(STATUS),object.getLong(ID),object.getString(REMARKS));
                            }

                            models.add(model);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

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
        progressDialog.dismiss();
        tot_open.setText(""+models.size());
        //Toast.makeText(getApplicationContext(),model1.size(),Toast.LENGTH_SHORT).show();
        adapter=new ModelAdapter(models,OpenActivity.this);
        view.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        checkAdapter();
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
                            tot_open.setText(""+models.size());
                        }
                    }
                    catch (IndexOutOfBoundsException e){
                        models=tempModel;
                        adapter=new ModelAdapter(models,OpenActivity.this);
                        adapter.notifyDataSetChanged();
                        tot_open.setText(""+models.size());
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
