package com.nctucs.csproject.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.res.XmlResourceParser;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nctucs.csproject.Activity.EventsStatusActivity;
import com.nctucs.csproject.Data.EventsStatusData;
import com.nctucs.csproject.R;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;


// Not compelete add and delete Events

public class EventsAdapter extends android.support.v7.widget.RecyclerView.Adapter<EventsAdapter.EventsViewHolder> {

    public static EventsStatusActivity mContext;
    private LayoutInflater mInflater;
    private ArrayList<EventsStatusData> mData;
    public EventsAdapter(Context context,ArrayList<EventsStatusData> data)
    {
        mContext = (EventsStatusActivity)context;
        mInflater = LayoutInflater.from(mContext);
        mData = data;
    }

    @NonNull
    @Override
    public EventsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.view_events_status,parent,false);
        EventsViewHolder viewHolder = new EventsViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull EventsViewHolder holder, int position) {
        if(mData != null){
            EventsStatusData data = mData.get(position);
            holder.tv_events_status.setText(data.events_name);
            ArrayList<String> member_list = data.member_list;
            int [] status = data.reply_status;
            for(int i = 0 ; i < member_list.size() ; i++){
                RelativeLayout tmp;
                tmp = (RelativeLayout)mInflater.inflate(R.layout.list_member,null);
                TextView tv_member_name = tmp.findViewById(R.id.tv_member_name);
                ImageView iv_reply_status = tmp.findViewById(R.id.iv_reply_status);
                tv_member_name.setText(member_list.get(i));
                switch ((status[i])){
                    case 0:
                        iv_reply_status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_refuse));
                        break;
                    case 1:
                        iv_reply_status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_check));
                        break;
                    case 2:
                        iv_reply_status.setImageDrawable(mContext.getResources().getDrawable(R.drawable.icon_waiting));
                        break;
                }
                holder.member_list.addView(tmp);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class EventsViewHolder extends  RecyclerView.ViewHolder{
        private TextView tv_events_status;
        private TextView tv_events_status_time;
        private LinearLayout member_list;
        private RelativeLayout status_info;
        private Button btn_events_ok,btn_events_delete,btn_confirm_add,btn_confirm_delete;
        private Button btn_cancel_add,btn_cancel_delete;
        private Boolean isopen;
        private Dialog dialog_add,dialog_delete;

        public EventsViewHolder(View itemView) {
            super(itemView);
            isopen = false;
            tv_events_status = itemView.findViewById(R.id.tv_events_status);
            tv_events_status_time = itemView.findViewById(R.id.tv_events_status_time);
            member_list = itemView.findViewById(R.id.member_list);
            status_info = itemView.findViewById(R.id.status_info);
            btn_events_ok = itemView.findViewById(R.id.btn_events_ok);
            btn_events_delete = itemView.findViewById(R.id.btn_events_delete);

            dialog_add = new Dialog(mContext);
            dialog_add.setContentView(R.layout.dialog_log_out);
            TextView add_title = dialog_add.findViewById(R.id.tv_dialog_title);
            add_title.setText(R.string.confirm_add);
            btn_confirm_add = dialog_add.findViewById(R.id.btn_confirm);
            btn_cancel_add = dialog_add.findViewById(R.id.btn_cancel);

            dialog_delete = new Dialog(mContext);
            dialog_delete.setContentView(R.layout.dialog_log_out);
            TextView delete_title = dialog_delete.findViewById(R.id.tv_dialog_title);
            delete_title.setText(R.string.confirm_delete);
            btn_confirm_delete = dialog_delete.findViewById(R.id.btn_confirm);
            btn_cancel_delete = dialog_delete.findViewById(R.id.btn_cancel);

            tv_events_status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!isopen){
                        status_info.setVisibility(View.VISIBLE);
                        isopen = true;
                    }
                    else {
                        status_info.setVisibility(View.GONE);
                        isopen = false;
                    }
                }
            });

            btn_events_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // show dialog
                    dialog_add.show();
                }
            });
            btn_events_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //show dialog
                    dialog_delete.show();
                }
            });
            btn_confirm_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //send confirm infomation to server
                    //and update data
                    dialog_add.dismiss();
                }
            });
            btn_cancel_add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_add.dismiss();
                }
            });
            btn_confirm_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //send delete info to server
                    //update data
                    dialog_delete.dismiss();
                }
            });
            btn_cancel_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_delete.dismiss();
                }
            });

        }

   }
}
