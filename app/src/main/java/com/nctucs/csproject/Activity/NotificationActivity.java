package com.nctucs.csproject.Activity;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.nctucs.csproject.Adapter.NotificationAdapter;
import com.nctucs.csproject.Data.NotificationData;
import com.nctucs.csproject.InformationHandler;
import com.nctucs.csproject.MyService;
import com.nctucs.csproject.Navigation_BaseActivity;
import com.nctucs.csproject.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class NotificationActivity extends Navigation_BaseActivity {
    private RecyclerView mRecyclerview;
    private NotificationAdapter adapter;
    private RecyclerView.LayoutManager mLayourmanager;
    private Toolbar toolbar;
    private BroadcastReceiver mReciver;
    private DrawerLayout mDrawerLayout;
    public MyService myService;
    public Boolean connected = false;
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
        setContentView(R.layout.activity_notification);
        Intent intent = getIntent();
        CurrentMenuItem = 3;
        mDrawerLayout = findViewById(R.id.drawer_layout);

        mRecyclerview = findViewById(R.id.rv_notification);
        mLayourmanager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(mLayourmanager);
        ArrayList<NotificationData> fake_data = new ArrayList<NotificationData>();
        adapter = new NotificationAdapter(this, InformationHandler.getNotificationData());
        mRecyclerview.setAdapter(adapter);

        toolbar = findViewById(R.id.notification_toolbar);
        setToolbar(toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.notification);
        mReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                setNavNew(R.id.nav_notification,true);
                adapter.notifyDataSetChanged();
            }
        };
        IntentFilter socketIntentFilter = new IntentFilter();
        socketIntentFilter.addAction(SOCKER_RCV);
        registerReceiver(mReciver,socketIntentFilter);
        if(intent.getFlags() == Intent.FLAG_ACTIVITY_NEW_TASK){
            System.out.println("notification");
            adapter.notifyDataSetChanged();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(NotificationActivity.this,MyService.class);
        bindService(serviceIntent,mConnection,Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        unregisterReceiver(mReciver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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