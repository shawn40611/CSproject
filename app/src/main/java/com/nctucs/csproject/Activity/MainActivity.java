package com.nctucs.csproject.Activity;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.services.calendar.model.Event;
import com.nctucs.csproject.InformationHandler;
import com.nctucs.csproject.JSONGenerator;
import com.nctucs.csproject.JSONParser;
import com.nctucs.csproject.MyService;
import com.nctucs.csproject.Navigation_BaseActivity;
import com.nctucs.csproject.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import java.util.List;


import com.nctucs.csproject.Adapter.ContentAdapter;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends Navigation_BaseActivity {



    MaterialCalendarView materialCalendarView;//布局内的控件
    CalendarDay currentDate;//自定义的日期对象
    int mYear, mMonth,mDay;
    private TextView test;
    private RecyclerView mRecyclerview;
    private ContentAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar;


    private Dialog dialog_register;
    private Dialog dialog_add_event;
    private Dialog dialog_choose_time;
    private DrawerLayout mDrawerLayout;
    private int selected_time,selected_preference,selected_group;

    private static  Socket mSocket;
    private Boolean connected = false;
    private OutputStream outputStream;
    private List<Event> events = null;
    private CheckStatus checkstatus;
    private GoogleSignInAccount mAccount;
    private MyService myService;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MyService.MyBinder binder = (MyService.MyBinder) service;
            myService = binder.getService();
            if(myService != null) {
                if(checkstatus != null)
                    checkstatus.complete();
            }


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }


    };
    private BroadcastReceiver mReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);
        mAccount = InformationHandler.getAccount();


        Toolbar toolbar = findViewById(R.id.toolbar);

        NavigationView navigationView = findViewById(R.id.view_nav);

        setToolbar(toolbar);
        TextView title = toolbar.findViewById(R.id.toolbar_title);
        title.setText(R.string.calendar);

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);// 實例化
        materialCalendarView.setBackgroundColor(getResources().getColor(R.color.main_background));
        //编辑日历属性
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)   //设置每周开始的第一天
                .setCalendarDisplayMode(CalendarMode.MONTHS)//设置显示模式，可以显示月的模式，也可以显示周的模式
                .commit();// 返回对象并保存
        //      设置点击日期的监听
        Calendar now_c = Calendar.getInstance();

        //init
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull com.prolificinteractive.materialcalendarview.MaterialCalendarView widget, @NonNull com.prolificinteractive.materialcalendarview.CalendarDay date, boolean selected) {
                currentDate = date;
                setStartDatetime(currentDate.getDate());
                Calendar c = currentDate.getCalendar();
                c.add(Calendar.DAY_OF_MONTH, 1);
                Date nextdate = c.getTime();
                setEndDatetime(nextdate);
                getResultsFromApi();
                setLoadDataComplete(new isLoadDataListener() {
                    @Override
                    public void loadComplete() {
                        events = getData();
                        adapter.setData(events);
                    }
                });
                currentDate.getCalendar().add(Calendar.DAY_OF_MONTH,-1);
            }
        });

        materialCalendarView.setDateSelected(now_c,true);


        mRecyclerview = findViewById(R.id.rv_content);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        adapter = new ContentAdapter(this);
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerview.setAdapter(adapter);

        dialog_register = new Dialog(this);
        dialog_register.setContentView(R.layout.dialog_register);
        dialog_add_event = new Dialog(this);
        dialog_add_event.setContentView(R.layout.dialog_add_event);
        progressBar = findViewById(R.id.progressbar);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int type = intent.getExtras().getInt("TYPE");
                if(type == JSONParser.TYPE_REPLY_REGISTER){
                    progressBar.setVisibility(View.GONE);
                    int r = intent.getExtras().getInt("reply");
                    Toast toast;
                    switch (r){
                        case 0:
                            toast = Toast.makeText(MainActivity.this,"Register Success!",Toast.LENGTH_LONG);
                            toast.show();
                            dialog_register.dismiss();
                            break;
                        case 1:
                            toast = Toast.makeText(MainActivity.this,"This Google account is used!",Toast.LENGTH_LONG);
                            toast.show();
                            break;
                        case 2:
                            toast = Toast.makeText(MainActivity.this,"This student id is used!",Toast.LENGTH_LONG);
                            toast.show();
                            break;
                        case 3:
                            toast = Toast.makeText(MainActivity.this,"Both Google account and id are used!",Toast.LENGTH_LONG);
                            toast.show();
                            break;
                    }
                }
            }
        };
        IntentFilter socketIntentFilter = new IntentFilter();
        socketIntentFilter.addAction(SOCKER_RCV);
        registerReceiver(mReceiver,socketIntentFilter);


    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent serviceIntent = new Intent(MainActivity.this,MyService.class);
        bindService(serviceIntent,mConnection,Context.BIND_AUTO_CREATE);
        setCheckStatus(new CheckStatus() {
            @Override
            public void complete() {
                connected = true;
                if(!InformationHandler.IsRegister()){

                    final EditText et_name,et_student_id;
                    et_name = dialog_register.findViewById(R.id.et_add_name);
                    et_student_id = dialog_register.findViewById(R.id.et_add_ID);
                    dialog_register.setCanceledOnTouchOutside(false);
                    dialog_register.show();
                    Button btn_ok = dialog_register.findViewById(R.id.btn_ok);
                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JSONGenerator generator = new JSONGenerator();
                            JSONArray data ;
                            String id= et_student_id.getText().toString();
                            String name = et_name.getText().toString();
                            data = generator.register(id,name,mAccount.getEmail());
                            myService.sendData(data);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
        });

    }


    public void getTime(View view) {
        if (currentDate != null) {
            int year = currentDate.getYear();
            int month = currentDate.getMonth() + 1; //月份跟系统一样是从0开始的，实际获取时要加1
            int day = currentDate.getDay();
        } else {
            ;
        }


    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showAddEvent(){

        Button btn_ok,btn_cancel;
        final Button btn_add_time,btn_add_group,btn_add_preference;
        final EditText et_add_name,et_add_location,et_add_description;
        final TextView tv_add_time,tv_add_group,tv_add_preference;
        dialog_add_event.setCanceledOnTouchOutside(false);
        btn_ok = dialog_add_event.findViewById(R.id.btn_ok);
        btn_cancel = dialog_add_event.findViewById(R.id.btn_cancel);
        btn_add_group = dialog_add_event.findViewById(R.id.btn_add_group);
        btn_add_time = dialog_add_event.findViewById(R.id.btn_add_time);
        btn_add_preference = dialog_add_event.findViewById(R.id.btn_add_preference);
        et_add_name = dialog_add_event.findViewById(R.id.et_add_name);
        et_add_location = dialog_add_event.findViewById(R.id.et_add_location);
        et_add_description = dialog_add_event.findViewById(R.id.et_add_description);
        tv_add_time = dialog_add_event.findViewById(R.id.tv_add_time);
        tv_add_group = dialog_add_event.findViewById(R.id.tv_add_group);
        tv_add_preference = dialog_add_event.findViewById(R.id.tv_add_preference);
        final PopupMenu menu_time  = new PopupMenu(this,btn_add_time);
        PopupMenu menu_group = new PopupMenu(this,btn_add_group);
        final PopupMenu menu_preference = new PopupMenu(this,btn_add_preference);

        menu_time.getMenuInflater().inflate(R.menu.add_event_time_menu,menu_time.getMenu());
        menu_preference.getMenuInflater().inflate(R.menu.menu_preference,menu_preference.getMenu());
        selected_time  = -1;
        selected_preference = -1;

        menu_time.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.half_hour:
                        tv_add_time.setText(R.string.add_event_half_hr);
                        selected_time = 0;
                        break;
                    case R.id.one_hour:
                        tv_add_time.setText(R.string.add_event_1hr);
                        selected_time = 1;
                        break;
                    case R.id.two_hour:
                        tv_add_time.setText(R.string.add_event_2hr);
                        selected_time = 2;
                        break;
                    case R.id.three_hour:
                        tv_add_time.setText(R.string.add_event_3hr);
                        selected_time = 3;
                        break;
                    case R.id.four_hour:
                        tv_add_time.setText(R.string.add_event_4hr);
                        selected_time = 4;
                        break;
                    case R.id.five_hour:
                        tv_add_time.setText(R.string.add_event_5hr);
                        selected_time = 5;
                        break;
                    case R.id.all_day:
                        tv_add_time.setText(R.string.add_event_all);
                        selected_time = 6;
                        break;
                }
                return true;
            }
        });
        menu_preference.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.morning:
                        tv_add_preference.setText(R.string.morning);
                        selected_preference = 0;
                        break;
                    case R.id.noon:
                        tv_add_preference.setText(R.string.noon);
                        selected_preference = 1;
                        break;
                    case R.id.afternoon:
                        tv_add_preference.setText(R.string.afternoon);
                        selected_preference = 2;
                        break;
                    case R.id.night:
                        tv_add_preference.setText(R.string.night);
                        selected_preference = 3;
                        break;
                }
                return true;
            }
        });
        btn_add_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_time.show();
            }
        });
        btn_add_preference.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_preference.show();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog_add_event.dismiss();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONArray data ;
                JSONGenerator generator = new JSONGenerator();
                String name,location,description;
                name = et_add_name.getText().toString();
                location = et_add_location.getText().toString();
                description = et_add_description.getText().toString();
                data = generator.inviteEvent(name,location,description,selected_preference,selected_time,selected_group);
                mService.sendData(data);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        dialog_add_event.show();
    }

    public void showSelectedTime(){

    }

    private interface  CheckStatus{
        public void complete();
    }

    private void setCheckStatus(CheckStatus check){
        this.checkstatus = check;
    }





}

