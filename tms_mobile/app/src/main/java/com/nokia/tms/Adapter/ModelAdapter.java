package com.nokia.tms.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nokia.tms.DetailActivity;
import com.nokia.tms.Model.OpenModel;
import com.nokia.tms.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ModelAdapter extends RecyclerView.Adapter<ModelAdapter.ViewHolder> {

    ArrayList<OpenModel> models;
    Context context;
    public  ModelAdapter(ArrayList<OpenModel> models, Context context){
        this.models=models;
        this.context=context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item,parent,false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if(models.size()>0) {
            final OpenModel model = models.get(position);
            holder.textId.setText(String.valueOf(model.getTicketId()));
            holder.textStatus.setText(String.valueOf(model.getRemarks()));
            holder.textElapse.setText(model.getDownTime());
            holder.textId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, DetailActivity.class);
                    intent.putExtra("Id", String.valueOf(model.getId()));
                    intent.putExtra("Remarks",model.getRemarks());
                    context.startActivity(intent);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView textId;
        TextView textStatus;
        TextView textElapse;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textId=(TextView) itemView.findViewById(R.id.recycler_ID);
            textStatus=(TextView) itemView.findViewById(R.id.recyclerStatus);
            textElapse=(TextView)itemView.findViewById(R.id.recyclerElapse);
        }
    }
}
