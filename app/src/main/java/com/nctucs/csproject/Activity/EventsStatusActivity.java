package com.nctucs.csproject.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.nctucs.csproject.Adapter.EventsAdapter;
import com.nctucs.csproject.Data.EventsStatusData;
import com.nctucs.csproject.InformationHandler;
import com.nctucs.csproject.Navigation_BaseActivity;
import com.nctucs.csproject.R;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class EventsStatusActivity extends Navigation_BaseActivity {
    Toolbar toolbar;
    EventsAdapter adapter;
    RecyclerView mRecyclerview;
    RecyclerView.LayoutManager mLayoutmanager;
    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_status);
        toolbar = findViewById(R.id.event_toolbar);
        setToolbar(toolbar);
        CurrentMenuItem = 1;
        mDrawerLayout = findViewById(R.id.drawer_layout);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.events_status);


        mRecyclerview = findViewById(R.id.rv_events_status);
        mLayoutmanager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        ArrayList<EventsStatusData> fake_data;
        /*fake_data = new ArrayList<EventsStatusData>(5);
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
        }*/
        adapter = new EventsAdapter(this, InformationHandler.getEventsStatusData());
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(mLayoutmanager);




    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
