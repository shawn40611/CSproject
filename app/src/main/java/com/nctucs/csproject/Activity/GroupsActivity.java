package com.nctucs.csproject.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.nctucs.csproject.Navigation_BaseActivity;
import com.nctucs.csproject.R;

import java.util.concurrent.TimeUnit;

public class GroupsActivity extends Navigation_BaseActivity{
    android.support.v7.widget.Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        CurrentMenuItem = 2;
        toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.groups);
        LinearLayout member_list = findViewById(R.id.member_list);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
