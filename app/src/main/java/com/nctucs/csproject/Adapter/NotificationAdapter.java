package com.nctucs.csproject.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.nctucs.csproject.Activity.NotificationActivity;
import com.nctucs.csproject.Data.NotificationData;
import com.nctucs.csproject.R;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

    private static NotificationActivity mContext;
    private LayoutInflater mInflater;
    private ArrayList<NotificationData> mData;
    public NotificationAdapter(Context context, ArrayList<NotificationData> data){
        mContext =(NotificationActivity) context;
        mInflater = LayoutInflater.from(mContext);
        mData = data;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.view_notification,null);
        NotificationViewHolder holder = new NotificationViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        if(mData != null){
            NotificationData data = mData.get(position);
            holder.tv_event_name.setText(data.events_name);
            holder.tv_groups.setText(data.event_groups);
            holder.tv_inviter.setText(data.event_inviter);
            holder.tv_events_time.setText(String.format("%s\n%s","2018/10/31","12:00pm-01:00pm"));

            holder.tv_events_description.setText(data.event_description);
        }
    }

    @Override
    public int getItemCount() {
        if(mData == null)
            return 0;
        return mData.size();
    }

    public  class NotificationViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_event_name,tv_groups,tv_inviter,tv_events_time,tv_events_description;
        private Button btn_reply_confirm,btn_reply_refuse;


        public  NotificationViewHolder(View itemView){
            super(itemView);
            tv_event_name = itemView.findViewById(R.id.tv_event_name);
            tv_groups = itemView.findViewById(R.id.tv_groups);
            tv_inviter = itemView.findViewById(R.id.tv_inviter);
            tv_events_time = itemView.findViewById(R.id.tv_events_time);
            tv_events_description = itemView.findViewById(R.id.tv_events_description);

            btn_reply_confirm = itemView.findViewById(R.id.btn_reply_confirm);
            btn_reply_refuse = itemView.findViewById(R.id.btn_reply_refuse);
            btn_reply_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //confirm invite
                }
            });
            btn_reply_refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // refuse
                }
            });
        }

    }
}
