package com.nctucs.csproject.Adapter;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.nctucs.csproject.Activity.MainActivity;
import com.nctucs.csproject.MyService;
import com.nctucs.csproject.R;

import java.util.Date;
import java.util.List;

public class ContentAdapter extends android.support.v7.widget.RecyclerView.Adapter<ContentAdapter.ContentViewHolder>{


    public static MainActivity mContext;
    private LayoutInflater mInflater;
    private List<Event> mData;
    private Boolean no_event = false;
    public ContentAdapter(MainActivity context)
    {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ContentAdapter.ContentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.view_calendar_content,parent,false);
        ContentViewHolder viewHolder = new ContentViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ContentViewHolder holder, int position) {
        RecyclerView.ViewHolder viewHolder = (ContentViewHolder)holder;
        if(mData != null && position != mData.size()){
            holder.tv_content.setClickable(true);
            Event event = mData.get(position);
            holder.tv_content.setText(event.getSummary());
            Date start = new Date(event.getStart().getDateTime().getValue());
            Date end = new Date(event.getEnd().getDateTime().getValue());
            holder.tv_content_time.setVisibility(View.VISIBLE);
             String s = (start.getHours() >= 10 ? start.getHours() : "0" + start.getHours())
                     + ":" + (start.getMinutes() >= 10 ? start.getMinutes() : "0" + start.getMinutes())
                     +"-"+ (end.getHours() >= 10 ? end.getHours() : "0" + end.getHours())
                     + ":" + (end.getMinutes() >= 10 ? end.getMinutes() : "0" + end.getMinutes());
            holder.tv_content_time.setText(s);
            holder.tv_event_title.setText(event.getSummary());
            holder.tv_event_time.setText(s);
            if(event.getDescription() != null){
                holder.tv_event_description.setText(event.getDescription());
            }else holder.tv_event_description.setText("No Description !");

            if(event.getLocation()!=null){
                holder.tv_location.setText(event.getLocation());
            }else holder.tv_location.setText("No Location !");

            holder.tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.dialog.show();
                }
            });
        }
        else {
            holder.tv_content.setClickable(true);
            holder.tv_content.setText("ADD EVENT!");
            holder.tv_content_time.setVisibility(View.GONE);
            holder.tv_content.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if(mData == null) {
            no_event = true;
            return 1;
        }
        no_event = false;
        return mData.size()+1;
    }

    public void setData(List<Event> data){
        mData = data;
        notifyDataSetChanged();
    }

    public static  class ContentViewHolder extends RecyclerView.ViewHolder{

        public  TextView tv_content;
        public TextView tv_content_time;
        public View highlight;
        public Dialog dialog;
        TextView tv_event_title,tv_event_time,tv_event_description,tv_location;
        Button btn_ok;

        public ContentViewHolder(View itemView)
        {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_content_time = itemView.findViewById(R.id.tv_content_time);
            highlight = itemView.findViewById(R.id.highlight);
            dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_calendar_content);
            tv_event_title = dialog.findViewById(R.id.tv_event_title);
            tv_event_time = dialog.findViewById(R.id.tv_event_time);
            tv_event_description = dialog.findViewById(R.id.tv_event_description);
            tv_location = dialog.findViewById(R.id.tv_location);
            btn_ok = dialog.findViewById(R.id.btn_ok);


            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }

    }
}
