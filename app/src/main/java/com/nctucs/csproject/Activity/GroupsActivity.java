package com.nctucs.csproject.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.Image;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.nctucs.csproject.Data.GroupData;
import com.nctucs.csproject.InformationHandler;
import com.nctucs.csproject.JSONGenerator;
import com.nctucs.csproject.JSONParser;
import com.nctucs.csproject.MyService;
import com.nctucs.csproject.Navigation_BaseActivity;
import com.nctucs.csproject.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.acl.Group;
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
    private DrawerLayout mDrawerLayout;
    private Button btn_add_groups,btn_add_confirm,btn_cancel,btn_search,btn_add,btn_ok;
    private TextView tv_name,tv_student_id,tv_email;
    private Dialog dialog_add_group;
    private EditText et_group_name,et_search_memeber;
    private Dialog dialog_add_member;
    private BroadcastReceiver mReciver;
    public MyService myService;
    private RelativeLayout found,not_found;
    private int temp_user_id = -1;
    private String temp_user_name = "";
    Boolean connected = false;
    private String temp_group = "";
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
        setContentView(R.layout.activity_groups);
        CurrentMenuItem = 2;
        toolbar = findViewById(R.id.group_toolbar);
        setToolbar(toolbar);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.groups);
        member_list = findViewById(R.id.rv_groups_member);
        btn_list = findViewById(R.id.btn_groups);
        menu = new PopupMenu(this,btn_list);
        mInflater = LayoutInflater.from(this);
        btn_add_groups = findViewById(R.id.btn_add_groups);
        btn_add_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAddGroupDialog();
                dialog_add_group.show();
            }

        });

        mReciver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction() == SOCKER_RCV) {
                    Bundle bag = intent.getExtras();
                    int type = bag.getInt("TYPE");
                    System.out.println("broadcast" + type);
                    if (type == JSONParser.TYPE_REPLY_CREATE) {
                        int group_id = bag.getInt("group_id");
                        System.out.println("create" + group_id);
                        GroupData new_data = new GroupData();
                        new_data.group_id = group_id;
                        new_data.group_name = temp_group;
                        new_data.member_list = new ArrayList<String>();
                        new_data.member_list.add(InformationHandler.getName());
                        InformationHandler.addGroupData(new_data);
                        temp_group = "";
                    } else if (type == JSONParser.TYPE_REPLY_SEARCH) {
                        String str = bag.getString("SearchData");
                        try {
                            JSONArray array = new JSONArray(str);
                            JSONObject object = array.getJSONObject(0);
                            String name, email, id;
                            name = object.getString("name");
                            email = object.getString("email");
                            id = object.getString("student_id");
                            tv_email.setText(email);
                            tv_name.setText(name);
                            tv_student_id.setText(id);
                            temp_user_name = name;
                            temp_user_id = object.getInt("user_id");
                            btn_add.setVisibility(View.VISIBLE);
                            if(now_select.member_list != null) {
                                for (int i = 0; i < now_select.member_list.size(); i++) {
                                    if (tv_name.equals(now_select.member_list.get(i))) {
                                        Toast toast;
                                        toast = Toast.makeText(GroupsActivity.this, "已存在成員!", Toast.LENGTH_LONG);
                                        toast.show();
                                        toast.show();
                                        btn_add.setVisibility(View.INVISIBLE);
                                    }
                                }
                            }
                            found.setVisibility(View.VISIBLE);
                            not_found.setVisibility(View.INVISIBLE);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            found.setVisibility(View.INVISIBLE);
                            not_found.setVisibility(View.VISIBLE);
                        }
                    }
                    else if(type == JSONParser.TYPE_GROUP_LIST){
                        data_list = InformationHandler.getGroupData();
                    }
                }
            }
        };
        IntentFilter socketIntentFilter = new IntentFilter();
        socketIntentFilter.addAction(SOCKER_RCV);
        registerReceiver(mReciver,socketIntentFilter);




    }

    @Override
    protected void onResume() {
        super.onResume();


        Intent serviceIntent = new Intent(GroupsActivity.this,MyService.class);
        bindService(serviceIntent,mConnection, Context.BIND_AUTO_CREATE);

        data_list = InformationHandler.getGroupData();
        if(data_list != null) {
            btn_list.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menu.getMenu().clear();
                    for (int i = 0; i < data_list.size(); i++) {
                        GroupData data = data_list.get(i);
                        menu.getMenu().add(0, i, 0, data.group_name);
                    }
                    menu.show();
                }
            });
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    now_select = data_list.get(item.getItemId());
                    System.out.println(now_select.group_name + " " + item.getItemId());
                    createDialog();
                    btn_list.setText(now_select.group_name);
                    showMember();
                    return true;
                }
            });
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        unregisterReceiver(mReciver);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showMember(){
        member_list.removeAllViews();
        ArrayList<String> list = now_select.member_list;
        if(list != null) {
            System.out.println("list not empty");
            for (int i = 0; i <= list.size(); i++) {
                if (i != list.size()) {
                    RelativeLayout member = (RelativeLayout) mInflater.inflate(R.layout.list_member, member_list,false);
                    TextView tv_member;
                    ImageView iv_status;
                    tv_member = member.findViewById(R.id.tv_member_name);
                    iv_status = member.findViewById(R.id.iv_reply_status);
                    tv_member.setText(list.get(i));
                    iv_status.setVisibility(View.GONE);
                    member_list.addView(member);
                } else {
                    RelativeLayout member = (RelativeLayout) mInflater.inflate(R.layout.list_member, member_list,false);
                    TextView tv_member;
                    ImageView iv_status;
                    tv_member = member.findViewById(R.id.tv_member_name);
                    iv_status = member.findViewById(R.id.iv_reply_status);
                    tv_member.setText("新增成員");
                    iv_status.setVisibility(View.GONE);
                    member.setClickable(true);
                    member.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog_add_member.show();
                        }
                    });
                    member_list.addView(member);
                }
            }
        }
        else{
            RelativeLayout member = (RelativeLayout) mInflater.inflate(R.layout.list_member, member_list,false);
            TextView tv_member;
            ImageView iv_status;
            tv_member = member.findViewById(R.id.tv_member_name);
            iv_status = member.findViewById(R.id.iv_reply_status);
            tv_member.setText("新增成員");
            iv_status.setVisibility(View.GONE);
            member.setClickable(true);
            member.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog_add_member.show();
                }
            });
            member_list.addView(member);
        }
    }
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
    public void createDialog(){
        dialog_add_member = new Dialog(this);
        dialog_add_member.setContentView(R.layout.dialog_add_member);
        found = dialog_add_member.findViewById(R.id.found);
        not_found = dialog_add_member.findViewById(R.id.not_found);
        found.setVisibility(View.INVISIBLE);
        not_found.setVisibility(View.INVISIBLE);
        et_search_memeber = dialog_add_member.findViewById(R.id.et_search_member);
        btn_search = dialog_add_member.findViewById(R.id.btn_search_member);
        tv_student_id = dialog_add_member.findViewById(R.id.tv_member_id);
        tv_name = dialog_add_member.findViewById(R.id.tv_member_name);
        tv_email = dialog_add_member.findViewById(R.id.tv_member_email);
        btn_add = dialog_add_member.findViewById(R.id.btn_add);
        btn_ok = dialog_add_member.findViewById(R.id.btn_ok);
        btn_search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        et_search_memeber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                System.out.println("click search");
                JSONGenerator generator = new JSONGenerator();
                String value = et_search_memeber.getText().toString();
                JSONArray data = generator.search(value);
                temp_user_id = -1;
                temp_user_name = "";
                found.setVisibility(View.INVISIBLE);
                not_found.setVisibility(View.INVISIBLE);
                myService.sendData(data);
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_add_member.dismiss();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(now_select.group_name);
                if(now_select.member_list != null){
                    JSONArray data;
                    JSONGenerator generator = new JSONGenerator();
                    data = generator.addmember(now_select.group_id,temp_user_id);
                    myService.sendData(data);
                    Toast toast;
                    toast = Toast.makeText(GroupsActivity.this,"已新增成員!",Toast.LENGTH_LONG);
                    toast.show();
                    now_select.member_list.add(temp_user_name);
                    showMember();
                    found.setVisibility(View.INVISIBLE);
                    not_found.setVisibility(View.INVISIBLE);

                }
                else{
                    JSONArray data;
                    JSONGenerator generator = new JSONGenerator();
                    data = generator.addmember(now_select.group_id,temp_user_id);
                    myService.sendData(data);
                    Toast toast;
                    toast = Toast.makeText(GroupsActivity.this,"已新增成員!",Toast.LENGTH_LONG);
                    toast.show();
                    now_select.member_list = new ArrayList<String>();
                    now_select.member_list.add(temp_user_name);
                    showMember();
                    found.setVisibility(View.INVISIBLE);
                    not_found.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
    public void createAddGroupDialog(){
        dialog_add_group = new Dialog(this);
        dialog_add_group.setContentView(R.layout.dialog_add_group);
        btn_add_confirm = dialog_add_group.findViewById(R.id.btn_ok);
        btn_cancel = dialog_add_group.findViewById(R.id.btn_cancel);
        btn_add_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONGenerator generator = new JSONGenerator();
                String value = et_group_name.getText().toString();
                temp_group = value;
                JSONArray data = generator.createGroup(value);
                myService.sendData(data);
                dialog_add_group.dismiss();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog_add_group.dismiss();
            }
        });
        et_group_name = dialog_add_group.findViewById(R.id.et_add_group);
    }
}
