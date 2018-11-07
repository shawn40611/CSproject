package com.nctucs.csproject.Activity;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.nctucs.csproject.Adapter.NotificationAdapter;
import com.nctucs.csproject.Data.NotificationData;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        CurrentMenuItem = 3;

        mRecyclerview = findViewById(R.id.rv_notification);
        mLayourmanager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerview.setLayoutManager(mLayourmanager);
        ArrayList<NotificationData> fake_data = new ArrayList<NotificationData>();
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            NotificationData tmp = new NotificationData();
            tmp.events_name = "Dinner";
            tmp.event_groups = "專題生";
            tmp.event_inviter = "董則遠";
            tmp.event_description = String.format("%s\n%s", "this is test", "this is test");
            fake_data.add(tmp);
        }
        adapter = new NotificationAdapter(this, fake_data);
        mRecyclerview.setAdapter(adapter);

        toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.notification);
        mReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                adapter.notifyDataSetChanged();
            }
        };
        IntentFilter socketIntentFilter = new IntentFilter();
        socketIntentFilter.addAction(SOCKER_RCV);
        registerReceiver(mReciver,socketIntentFilter);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReciver);
    }
}