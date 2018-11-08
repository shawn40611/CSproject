package com.nctucs.csproject.Activity;

import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.nctucs.csproject.Data.GroupData;
import com.nctucs.csproject.InformationHandler;
import com.nctucs.csproject.Navigation_BaseActivity;
import com.nctucs.csproject.R;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class GroupsActivity extends Navigation_BaseActivity{
    android.support.v7.widget.Toolbar toolbar;
    private ArrayList<GroupData> data_list;
    private PopupMenu menu;
    private Button btn_list;
    private GroupData now_select;
    private LayoutInflater mInflater;
    private LinearLayout member_list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        CurrentMenuItem = 2;
        toolbar = findViewById(R.id.toolbar);
        setToolbar(toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.groups);
        member_list = findViewById(R.id.member_list);
        btn_list = findViewById(R.id.btn_groups);
        menu = new PopupMenu(this,btn_list);
        mInflater = LayoutInflater.from(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        data_list = InformationHandler.getGroupData();
        if(data_list != null) {
            for (int i = 0; i < data_list.size(); i++) {
                GroupData data = data_list.get(i);
                menu.getMenu().add(0, i, 0, data.group_name);
            }
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    now_select = data_list.get(item.getItemId());
                    ArrayList<String> list = now_select.member_list;
                    for (int i = 0; i < list.size(); i++) {
                        RelativeLayout member = (RelativeLayout) mInflater.inflate(R.layout.list_member, null);
                        TextView tv_member;
                        ImageView iv_status;
                        tv_member = member.findViewById(R.id.tv_member_name);
                        iv_status = member.findViewById(R.id.iv_reply_status);
                        tv_member.setText(list.get(i));
                        iv_status.setVisibility(View.GONE);
                        member_list.addView(member);
                    }
                    return true;
                }
            });
        }


    }
}
