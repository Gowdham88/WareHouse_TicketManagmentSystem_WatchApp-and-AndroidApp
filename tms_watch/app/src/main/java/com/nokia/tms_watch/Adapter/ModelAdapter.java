package com.nokia.tms_watch.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nokia.tms_watch.Model.OpenModel;
import com.nokia.tms_watch.Activity.DetailActivity;
import com.nokia.tms_watch.Model.OpenModel;
import com.nokia.tms_watch.R;

import java.util.ArrayList;

public class ModelAdapter extends BaseAdapter {

    ArrayList<OpenModel> models;
    Context context;
    public  ModelAdapter(ArrayList<OpenModel> models, Context context){
        this.models=models;
        this.context=context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public Object getItem(int position) {
        return models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView==null){
            holder=new ViewHolder();
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.list_item,null,true);
            holder.textId=convertView.findViewById(R.id.text_Id);
            convertView.setTag(holder);
        }
        else {
            holder=(ViewHolder)convertView.getTag();
        }
        holder.textId.setText(String.valueOf(models.get(position).getTicketId()));
        holder.textId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, DetailActivity.class);
                intent.putExtra("Id",""+models.get(position).getId());
                intent.putExtra("Remarks", models.get(position).getRemarks());
                intent.putExtra("status",models.get(position).getTicketStatus());
                intent.putExtra("openTic",""+models.size());
                Log.e("Id",""+models.get(position).getId());
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    public class ViewHolder {
        protected  TextView textId;
    }
}
