package com.nctucs.csproject.Activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.nctucs.csproject.Navigation_BaseActivity;
import com.nctucs.csproject.R;

public class GroupsActivity extends Navigation_BaseActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
    }
}
