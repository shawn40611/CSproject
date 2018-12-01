package com.nctucs.csproject.Adapter;

import android.app.Dialog;
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
import com.nctucs.csproject.InformationHandler;
import com.nctucs.csproject.JSONGenerator;
import com.nctucs.csproject.R;

import org.json.JSONArray;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{

    private static NotificationActivity mContext;
    private LayoutInflater mInflater;
    private ArrayList<NotificationData> mData;
    public NotificationAdapter(Context context, ArrayList<NotificationData> data){
        mContext =(NotificationActivity) context;
        mInflater = LayoutInflater.from(mContext);
        mData = data;
        System.out.println("DataSize = " + mData.size());
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.view_notification,null);
        NotificationViewHolder holder = new NotificationViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, final int position) {
        if(mData != null){
            final Dialog dialog_confirm,dialog_refuse;
            dialog_confirm = new Dialog(mContext);
            dialog_refuse = new Dialog(mContext);
            dialog_confirm.setContentView(R.layout.dialog_log_out);
            dialog_refuse.setContentView(R.layout.dialog_log_out);
            final NotificationData data = mData.get(position);
            final int type = data.type;
            holder.tv_event_name.setText(type == 0?data.group_name:data.events_name);
            holder.tv_inviter.setText(type == 0? data.group_inviter:data.event_inviter);
            if(type == 1) {
                Date start,end;
                start = data.event_start_time;
                end = data.event_end_time;
                String startday,endday,starttime,endtime;
                System.out.println("Noti" + start.toString());
                startday = String.format("%d/%d/%d",start.getYear()+1900,start.getMonth()+1,start.getDate());
                starttime = (start.getHours() >= 10 ? start.getHours() : "0" + start.getHours())
                        + ":" + (start.getMinutes() >= 10 ? start.getMinutes() : "0" + start.getMinutes());
                endtime = (end.getHours() >= 10 ? end.getHours() : "0" + end.getHours())
                        + ":" + (end.getMinutes() >= 10 ? end.getMinutes() : "0" + end.getMinutes());
                endday = String.format("%d/%d/%d",start.getYear()+1900,start.getMonth()+1,start.getDate());
                holder.tv_events_time.setText(String.format("%s %s\n%s %s", startday, starttime,endday,endtime));

                holder.tv_groups.setText(data.event_groups);
                holder.tv_events_description.setText(data.event_description);
            }
            Button btn_confirm,btn_cancel,btn_refuse,btn_cancel1;
            btn_confirm = dialog_confirm.findViewById(R.id.btn_confirm);
            btn_refuse = dialog_refuse.findViewById(R.id.btn_confirm);
            btn_cancel = dialog_confirm.findViewById(R.id.btn_cancel);
            btn_cancel1 = dialog_refuse.findViewById(R.id.btn_cancel);
            final JSONGenerator generator = new JSONGenerator();
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_confirm.dismiss();
                }
            });
            btn_cancel1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_refuse.dismiss();
                }
            });
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONArray senddata;
                    String datatype = type == 0? "Group":"Event";
                    int idtype = type == 0? data.group_id:data.event_id;
                    senddata = generator.reply(datatype,idtype,1);
                    mContext.myService.sendData(senddata);
                    mData.remove(position);
                    InformationHandler.setNotificationData(mData);
                    notifyDataSetChanged();
                    dialog_confirm.dismiss();
                }
            });
            btn_refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JSONArray senddata;
                    String datatype = type == 0? "Group":"Event";
                    int idtype = type == 0? data.group_id:data.event_id;
                    senddata = generator.reply(datatype,idtype,0);
                    mContext.myService.sendData(senddata);
                    mData.remove(position);
                    InformationHandler.setNotificationData(mData);
                    notifyDataSetChanged();
                    dialog_refuse.dismiss();
                }
            });
            holder.btn_reply_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tmp = dialog_confirm.findViewById(R.id.tv_dialog_title);
                    tmp.setText("確定要成立嗎?");
                    dialog_confirm.show();
                }
            });
            holder.btn_reply_refuse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TextView tmp = dialog_refuse.findViewById(R.id.tv_dialog_title);
                    tmp.setText("確定要拒絕嗎?");
                    dialog_refuse.show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if(mData == null) {
            mContext.setNavNew(R.id.nav_notification,false);
            return 0;
        }
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
        }

    }
}
