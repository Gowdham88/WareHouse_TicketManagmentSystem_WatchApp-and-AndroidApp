package com.nokia.tms_watch.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nokia.tms_watch.Model.OpenModel;
import com.nokia.tms_watch.Activity.MainActivity;
import com.nokia.tms_watch.Model.OpenModel;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class DetailAdapter extends RecyclerView.Adapter<DetailAdapter.ViewHolder> {

    ArrayList<OpenModel> models;
    Context context;
    public  DetailAdapter(ArrayList<OpenModel> models, Context context){
        this.models=models;
        this.context=context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       // View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final OpenModel model=models.get(position);
        holder.textId.setText(String.valueOf(model.getTicketId()));
        holder.textStatus.setText(String.valueOf(model.getTicketStatus()));
        if(model.getTicketStatus().equals("Close")||model.getTicketStatus().equals("Ongoing") ){
            holder.textStatus.setTextColor(Color.GREEN);
        }
        holder.textId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, MainActivity.class);
                intent.putExtra("Id",String.valueOf(model.getId()));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textId;
        TextView textStatus;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
           // textId=itemView.findViewById(R.id.recycler_ID);
            //textStatus=itemView.findViewById(R.id.recyclerStatus);
        }
    }
}
