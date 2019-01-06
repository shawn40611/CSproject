package com.nctucs.csproject.Activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
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
import com.nctucs.csproject.MyService;
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
    public MyService myService;
    Boolean connected;
    private DrawerLayout mDrawerLayout;
    private BroadcastReceiver mReceiver;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder binder = (MyService.MyBinder) service;
            myService = binder.getService();
            if(myService != null) {
                connected = true;
            }


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }


    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_status);
        Intent intent = getIntent();
        toolbar = findViewById(R.id.event_toolbar);
        setToolbar(toolbar);
        CurrentMenuItem = 1;
        mDrawerLayout = findViewById(R.id.drawer_layout);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.events_status);

        mRecyclerview = findViewById(R.id.rv_events_status);
        mLayoutmanager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        adapter = new EventsAdapter(this, InformationHandler.getEventsStatusData());
        mRecyclerview.setAdapter(adapter);
        mRecyclerview.setLayoutManager(mLayoutmanager);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Message message = new Message();
                message.what = 2002;
                callHandler(message);
                adapter.setData(InformationHandler.getEventsStatusData());
                adapter.notifyDataSetChanged();
                System.out.println("status receive");
            }
        };
        IntentFilter socketIntentFilter = new IntentFilter();
        socketIntentFilter.addAction(SOCKER_RCV);
        registerReceiver(mReceiver,socketIntentFilter);
        if(intent.getFlags() == Intent.FLAG_ACTIVITY_NEW_TASK){
            System.out.println("event status ");
            Message message = new Message();
            message.what = 2002;
            callHandler(message);
            adapter.setData(InformationHandler.getEventsStatusData());
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(EventsStatusActivity.this,MyService.class);
        bindService(serviceIntent,mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        unregisterReceiver(mReceiver);
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
