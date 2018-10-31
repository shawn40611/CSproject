package com.nctucs.csproject.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nctucs.csproject.Adapter.EventsAdapter;
import com.nctucs.csproject.Data.EventsStatusData;
import com.nctucs.csproject.Navigation_BaseActivity;
import com.nctucs.csproject.R;

import java.util.ArrayList;
import java.util.Random;

public class EventsStatusActivity extends Navigation_BaseActivity {
    Toolbar toolbar;
    EventsAdapter adapter;
    RecyclerView mRecyclerview;
    RecyclerView.LayoutManager mLayoutmanager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_status);
        toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);
        CurrentMenuItem = 1;
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.events_status);


        mRecyclerview = findViewById(R.id.rv_events_status);
        mLayoutmanager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        ArrayList<EventsStatusData> fake_data;
        fake_data = new ArrayList<EventsStatusData>(5);
        for(int i = 0 ; i < 5 ; i++){
            EventsStatusData tmp = new EventsStatusData();
            tmp.events_name = "Dinner";
            Random random = new Random();
            tmp.reply_status = new int[5];
            tmp.member_list = new ArrayList<String>(5);
            for(int j = 0 ; j < 5 ; j++){
                tmp.member_list.add("Shawn Wu");
                tmp.reply_status[j] = random.nextInt(3);
            }
            fake_data.add(tmp);
        }
        adapter = new EventsAdapter(this,fake_data);
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(mLayoutmanager);




    }
}
