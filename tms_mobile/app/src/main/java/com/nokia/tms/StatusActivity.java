package com.nokia.tms;

import androidx.appcompat.app.AppCompatActivity;
import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.view.PieChartView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class StatusActivity extends Activity {

    PieChartView pieChartView;
    RequestQueue requestQueue;
    TextView live_open,live_close,live_ongoing;
    View open_view,close_view,ongoing_view;
    static final String URL=AppUrls.TREND_URL;
    ProgressDialog dialog;
    PieChartData pieChartData;
    List<SliceValue> pieData = new ArrayList<>();

    long openTic,closeTic,onTic,ICT,MFT,OTHER;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.string_menu, R.layout.spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        intialize();
        getResponce(0);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getResponce(position);
                Log.e("Position",""+
                        position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void intialize() {
        requestQueue= Volley.newRequestQueue(StatusActivity.this);
        dialog=new ProgressDialog(StatusActivity.this);
        dialog.setTitle("Loading..");
        dialog.setMessage("Please Wait..!");
        dialog.setCancelable(false);

        pieChartView=(PieChartView) findViewById(R.id.chart);
        live_open=findViewById(R.id.live_open_name);
        live_close=findViewById(R.id.live_close_name);
        live_ongoing=findViewById(R.id.live_ongoing_name);
        open_view=findViewById(R.id.view_open);
        close_view=findViewById(R.id.view_close);
        ongoing_view=findViewById(R.id.view_ongoing);

        open_view.setBackgroundColor(Color.RED);
        ongoing_view.setBackgroundColor(Color.YELLOW);
        close_view.setBackgroundColor(Color.GREEN);
        live_open.setTextColor(Color.RED);
        live_ongoing.setTextColor(Color.YELLOW);
        live_close.setTextColor(Color.GREEN);

        pieData.add(new SliceValue(30, Color.RED).setLabel("Open:"+""+openTic));
        pieData.add(new SliceValue(20, Color.YELLOW).setLabel("On going:"+""+onTic));
        pieData.add(new SliceValue(44, Color.GREEN).setLabel("Close:"+""+closeTic));

        pieChartData = new PieChartData(pieData);
        pieChartData.setHasLabels(true).setValueLabelTextSize(14);
        pieChartData.setHasCenterCircle(true);//.setCenterText1("").setCenterText1FontSize(20).setCenterText1Color(Color.parseColor("#0097A7"));
        pieChartView.setPieChartData(pieChartData);

    }

    private void getResponce(final  int position) {
        dialog.show();
        int hour;
        int currentHour = Calendar.getInstance(TimeZone.getTimeZone("GMT+5:30")).get(Calendar.HOUR_OF_DAY);
        if(currentHour>=6)
             hour=currentHour-6;
        else
            hour=currentHour+18;

        String newUrl;
        Log.i("hour",""+hour);
        if(position==0)
            newUrl=URL+hour;
        else
            newUrl=URL+1000;

            JsonArrayRequest request=new JsonArrayRequest(newUrl, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    for(int i=0;i<response.length();i++){
                        try {
                            JSONObject object=response.getJSONObject(i);
                            String status=object.getString("Status");
                            if(status.equals("Close"))
                                closeTic=object.getLong("TOTAL");
                            else if (status.equals("Open")){
                                ICT=object.getLong("ICT");
                                MFT=object.getLong("MFT");
                                OTHER=object.getLong("OTHER");
                                openTic=object.getLong("TOTAL");
                                Log.i("ictmftother",""+ICT+"  "+MFT+"  "+OTHER);
                            }
                            else if (status.equals("Ongoing"))
                                onTic=object.getLong("TOTAL");


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    loadValues(position);
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

    void loadValues(int  pos){
        if(pos==0){
            pieData.clear();
            live_open.setText("OPEN "+openTic);
            live_ongoing.setText("ONGOING "+onTic);
            live_close.setText("CLOSE "+closeTic);
            pieData.add(new SliceValue(openTic, Color.RED).setLabel("Open:"+openTic));
            pieData.add(new SliceValue(onTic, Color.YELLOW).setLabel("Ongoing:"+onTic));
            pieData.add(new SliceValue(closeTic, Color.GREEN).setLabel("Closed:"+closeTic));
            pieChartData.setValues(pieData);
            pieChartView.setPieChartData(pieChartData);

            if(openTic ==0 && closeTic == 0 && onTic==0)
                Toast.makeText(StatusActivity.this,"No data Available",Toast.LENGTH_SHORT).show();
        }
        else if(pos==1){
            pieData.clear();
            live_open.setText("ICT "+ICT);
            live_ongoing.setText("MFT "+MFT);
            live_close.setText("OTHER "+OTHER);
            pieData.add(new SliceValue(ICT, Color.RED).setLabel("ICT:"+ICT));
            pieData.add(new SliceValue(MFT, Color.YELLOW).setLabel("MFT:"+MFT));
            pieData.add(new SliceValue(OTHER, Color.GREEN).setLabel("OTHER:"+OTHER));
            pieChartData.setValues(pieData);
            pieChartView.setPieChartData(pieChartData);
        }
    }
}
