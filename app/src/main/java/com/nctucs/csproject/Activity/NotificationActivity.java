package com.nctucs.csproject.Activity;

import android.app.Notification;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.nctucs.csproject.Adapter.NotificationAdapter;
import com.nctucs.csproject.Data.NotificationData;
import com.nctucs.csproject.Navigation_BaseActivity;
import com.nctucs.csproject.R;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NotificationActivity extends Navigation_BaseActivity {
    private RecyclerView mRecyclerview;
    private NotificationAdapter adapter;
    private RecyclerView.LayoutManager mLayourmanager;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        mRecyclerview = findViewById(R.id.rv_notification);
        mLayourmanager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerview.setLayoutManager(mLayourmanager);
        ArrayList<NotificationData> fake_data = new ArrayList<NotificationData>();
        Random random = new Random();
        for(int i = 0 ; i < 10; i++){
            NotificationData tmp = new NotificationData();
            tmp.events_name = "Dinner";
            tmp.groups ="專題生";
            tmp.inviter = "董則遠";
            tmp.description = String.format("%s\n%s","this is test","this is test");
            fake_data.add(tmp);
        }
        adapter = new NotificationAdapter(this,fake_data);
        mRecyclerview.setAdapter(adapter);

        toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);

    }
}
