package com.nctucs.csproject.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nctucs.csproject.R;

public class ContentAdapter extends android.support.v7.widget.RecyclerView.Adapter<ContentAdapter.ContentViewHolder>{


    Context mContext;
    LayoutInflater mInflater;
    public ContentAdapter(Context context)
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
    public void onBindViewHolder(@NonNull ContentViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static  class ContentViewHolder extends RecyclerView.ViewHolder{

        TextView tv_content;
        public ContentViewHolder(View itemView)
        {
            super(itemView);
            tv_content = itemView.findViewById(R.id.tv_content);

        }

    }
}
