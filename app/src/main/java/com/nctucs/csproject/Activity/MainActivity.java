package com.nctucs.csproject.Activity;


import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.services.calendar.model.Event;
import com.nctucs.csproject.Data.GroupData;
import com.nctucs.csproject.Data.SelectedTimeData;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import java.util.List;


import com.nctucs.csproject.Adapter.ContentAdapter;

import org.json.JSONArray;
import org.json.JSONObject;
import org.mortbay.servlet.ProxyServlet;


public class MainActivity extends Navigation_BaseActivity implements View.OnFocusChangeListener{



    MaterialCalendarView materialCalendarView;//布局内的控件
    CalendarDay currentDate;//自定义的日期对象
    private long mNowSelectedDate;
    private TextView test;
    private RecyclerView mRecyclerview;
    private ContentAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ProgressBar progressBar,progressBarInAdd;
    private PopupMenu menu;
    private Comparator<SelectedTimeData> comparator = new Comparator<SelectedTimeData>() {
        @Override
        public int compare(SelectedTimeData o1, SelectedTimeData o2) {
            return (o1.attendnumber > o2.attendnumber)? -1: (o1.attendnumber < o2.attendnumber)? 1 : 0;
        }
    };



    private Dialog dialog_register;
    private Dialog dialog_add_event;
    private Dialog dialog_selected_time;
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
    private Calendar now_c;
    private Date now_day;


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

        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);
        materialCalendarView.setBackgroundColor(getResources().getColor(R.color.main_background));
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();
        now_c = Calendar.getInstance();
        now_day = new Date();
        now_day.setYear(now_c.get(Calendar.YEAR)-1900);
        now_day.setMonth(now_c.get(Calendar.MONTH));
        now_day.setDate(now_c.get(Calendar.DAY_OF_MONTH));
        now_day.setHours(0);
        now_day.setMinutes(0);
        now_day.setSeconds(0);
        mNowSelectedDate = now_day.getTime();

        //init
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull com.prolificinteractive.materialcalendarview.MaterialCalendarView widget, @NonNull com.prolificinteractive.materialcalendarview.CalendarDay date, boolean selected) {
                currentDate = date;
                mNowSelectedDate = currentDate.getDate().getTime();
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
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                setStartDatetime(now_day);
                now_c.add(Calendar.DAY_OF_MONTH, 1);
                Date nextday = new Date();
                nextday.setYear(now_c.get(Calendar.YEAR)-1900);
                nextday.setMonth(now_c.get(Calendar.MONTH));
                nextday.setDate(now_c.get(Calendar.DAY_OF_MONTH));
                nextday.setHours(0);
                nextday.setMinutes(0);
                nextday.setSeconds(0);
                setEndDatetime(nextday);
                getResultsFromApi();
                setLoadDataComplete(new isLoadDataListener() {
                    @Override
                    public void loadComplete() {
                        events = getData();
                        adapter.setData(events);
                    }
                });
                now_c.add(Calendar.DAY_OF_MONTH,-1);
            }
        });
        t.start();


        materialCalendarView.setDateSelected(now_c,true);


        mRecyclerview = findViewById(R.id.rv_content);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        adapter = new ContentAdapter(this);
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerview.setAdapter(adapter);

        dialog_register = new Dialog(this);
        dialog_register.setContentView(R.layout.dialog_register);
        dialog_register.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        progressBar = findViewById(R.id.main_progressbar);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int type = intent.getExtras().getInt("TYPE");
                progressBar.setVisibility(View.GONE);
                if(type == JSONParser.TYPE_REPLY_REGISTER){
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
                else if(type == JSONParser.TYPE_REPLY_ADD_EVENT){
                    showSelectedTime(InformationHandler.getSelectedTimeData());
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

                    et_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                hideKeyboard(v);
                            }
                        }
                    });
                    et_student_id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                        @Override
                        public void onFocusChange(View v, boolean hasFocus) {
                            if (!hasFocus) {
                                hideKeyboard(v);
                            }
                        }
                    });

                    btn_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressBar.setVisibility(View.VISIBLE);
                            JSONGenerator generator = new JSONGenerator();
                            JSONArray data ;
                            String id= et_student_id.getText().toString();
                            String name = et_name.getText().toString();
                            InformationHandler.setName(name);
                            data = generator.register(id,name,mAccount.getEmail());
                            myService.sendData(data);
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService(mConnection);
        unregisterReceiver(mReceiver);
        if(dialog_selected_time != null)
            dialog_selected_time.dismiss();
        if(dialog_choose_time != null)
            dialog_choose_time.dismiss();
        if(dialog_add_event != null)
            dialog_add_event.dismiss();
        if(dialog_register != null)
            dialog_register.dismiss();
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

        dialog_add_event = new Dialog(this);
        dialog_add_event.setContentView(R.layout.dialog_add_event);
        dialog_add_event.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
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
        final PopupMenu menu_group = new PopupMenu(this,btn_add_group);
        final PopupMenu menu_preference = new PopupMenu(this,btn_add_preference);
        Toast toast;

        et_add_description.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        et_add_location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });
        et_add_name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });


        menu_time.getMenuInflater().inflate(R.menu.add_event_time_menu,menu_time.getMenu());
        menu_preference.getMenuInflater().inflate(R.menu.menu_preference,menu_preference.getMenu());
        selected_time  = -1;
        selected_preference = -1;
        selected_group = -1;
        final ArrayList<GroupData> data_list = InformationHandler.getGroupData();
        if(data_list != null) {
            for (int i = 0; i < data_list.size(); i++) {
                GroupData data = data_list.get(i);
                menu_group.getMenu().add(0, i, 0, data.group_name);
            }
            menu_group.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    final int itemid = item.getItemId();
                    selected_group = data_list.get(itemid).group_id;
                    tv_add_group.setText(data_list.get(itemid).group_name);
                    return true;
                }
            });

            menu_time.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
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
                    switch (item.getItemId()) {
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
                    hideKeyboard(v);
                    menu_time.show();
                }
            });
            btn_add_group.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard(v);
                    menu_group.show();
                }
            });
            btn_add_preference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideKeyboard(v);
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
                    progressBar.setVisibility(View.VISIBLE);
                    JSONArray data;
                    Toast toast;
                    Boolean error_detect = false;
                    JSONGenerator generator = new JSONGenerator();
                    String name, location, description;
                    name = et_add_name.getText().toString();
                    if(name.equals("")){
                        toast = Toast.makeText(MainActivity.this,"請輸入事件名稱",Toast.LENGTH_SHORT);
                        toast.show();
                        error_detect = true;
                    }
                    location = et_add_location.getText().toString();
                    description = et_add_description.getText().toString();
                    if(selected_time < 0 && !error_detect){
                        toast = Toast.makeText(MainActivity.this,"請選擇時間",Toast.LENGTH_SHORT);
                        toast.show();
                        error_detect = true;
                    }
                    if(selected_group < 0 && !error_detect){
                        toast = Toast.makeText(MainActivity.this,"請選擇群組",Toast.LENGTH_SHORT);
                        toast.show();
                        error_detect = true;
                    }
                    if(selected_preference < 0 && !error_detect){
                        toast = Toast.makeText(MainActivity.this,"請選擇偏好",Toast.LENGTH_SHORT);
                        toast.show();
                        error_detect = true;
                    }
                    if(!error_detect) {
                        data = generator.
                                inviteEvent(name, location, description,
                                        selected_preference, selected_time, selected_group, (mNowSelectedDate / 1000));
                        myService.sendData(data);
                        dialog_add_event.dismiss();
                    }

                }
            });
            dialog_add_event.show();
        }else {
            toast = Toast.makeText(this,"Join a group first!",Toast.LENGTH_LONG);
            toast.show();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            hideKeyboard(v);
        }
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void showSelectedTime(final ArrayList<SelectedTimeData> datalist){
        dialog_selected_time = new Dialog(this);
        dialog_selected_time.setContentView(R.layout.dialog_add_event_timelist);
        dialog_selected_time.setCanceledOnTouchOutside(false);
        LinearLayout time_list;
        RelativeLayout [] content = new RelativeLayout[10];
        LayoutInflater inflater = LayoutInflater.from(this);
        time_list = dialog_selected_time.findViewById(R.id.time_list);
        Collections.sort(datalist,comparator);

        for(int i = 0 ; i < (datalist.size() < 10 ? datalist.size() : 10)  ; i++){
            final SelectedTimeData tmp = datalist.get(i);
            content[i] = (RelativeLayout)inflater.inflate(R.layout.select_list,time_list,false);
            TextView tv_add_event_time = content[i].findViewById(R.id.tv_timecode);
            TextView tv_member_num  = content[i].findViewById(R.id.tv_member);
            Date start,end;
            System.out.println("num = " + datalist.get(i).attendnumber);
            start = new Date(datalist.get(i).start*1000);
            end = new Date(datalist.get(i).end*1000);
            String str = (start.getHours() >= 10 ? start.getHours() : "0" + start.getHours())
                    + ":" + (start.getMinutes() >= 10 ? start.getMinutes() : "0" + start.getMinutes())
                    +"-"+ (end.getHours() >= 10 ? end.getHours() : "0" + end.getHours())
                    + ":" + (end.getMinutes() >= 10 ? end.getMinutes() : "0" + end.getMinutes());
            tv_add_event_time.setText(str);
            tv_member_num.setText("人數:"+datalist.get(i).attendnumber);
            final Dialog dialog_confirm = new Dialog(this);
            dialog_confirm.setContentView(R.layout.dialog_log_out);
            dialog_confirm.setCanceledOnTouchOutside(false);
            TextView tv_title;
            tv_title = dialog_confirm.findViewById(R.id.tv_dialog_title);
            tv_title.setText("確定要這個時間嗎?");
            Button btn_confirm,btn_cancel ;
            btn_confirm = dialog_confirm.findViewById(R.id.btn_confirm);
            btn_cancel = dialog_confirm.findViewById(R.id.btn_cancel);
            btn_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONGenerator generator = new JSONGenerator();
                    JSONArray data = generator.selectTime(tmp.event_id);
                    myService.sendData(data);
                    dialog_confirm.dismiss();
                    dialog_selected_time.dismiss();
                }
            });
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_confirm.dismiss();
                }
            });
            tv_add_event_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog_confirm.show();
                }
            });
            time_list.addView(content[i]);
        }
        dialog_selected_time.show();
    }

    private interface  CheckStatus{
        public void complete();
    }

    private void setCheckStatus(CheckStatus check){
        this.checkstatus = check;
    }





}

