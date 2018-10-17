package com.nctucs.csproject.Activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.TextView;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Event;
import com.nctucs.csproject.InformationHandler;
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
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.nctucs.csproject.Adapter.ContentAdapter;




public class MainActivity extends Navigation_BaseActivity {

    public static final String ADDRESS = "178.128.90.63";
    public static final int PORT = 8888;

    MaterialCalendarView materialCalendarView;//布局内的控件
    CalendarDay currentDate;//自定义的日期对象
    int mYear, mMonth,mDay;
    private TextView test;
    private RecyclerView mRecyclerview;
    private ContentAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private GoogleSignInAccount mAccount;








    private DrawerLayout mDrawerLayout;

    private static  Socket mSocket;
    private Boolean connected = false;
    private OutputStream outputStream;
    private List<Event> events = null;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDrawerLayout = findViewById(R.id.drawer_layout);


        Toolbar toolbar = findViewById(R.id.toolbar);

        NavigationView navigationView = findViewById(R.id.view_nav);

        setToolbar(toolbar);
        mAccount = InformationHandler.getAccount();



        materialCalendarView = (MaterialCalendarView) findViewById(R.id.calendarView);// 實例化
        materialCalendarView.setBackgroundColor(getResources().getColor(R.color.main_background));
        //编辑日历属性
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)   //设置每周开始的第一天
                .setCalendarDisplayMode(CalendarMode.MONTHS)//设置显示模式，可以显示月的模式，也可以显示周的模式
                .commit();// 返回对象并保存
        //      设置点击日期的监听
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
                        System.out.println(events == null);
                        adapter.setData(events);
                    }
                });
                currentDate.getCalendar().add(Calendar.DAY_OF_MONTH,-1);
            }
        });

        mRecyclerview = findViewById(R.id.rv_content);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        adapter = new ContentAdapter(this);
        mRecyclerview.setLayoutManager(mLayoutManager);
        mRecyclerview.setAdapter(adapter);




    }

    @Override
    protected void onResume() {
        super.onResume();
        final String authcode = mAccount.getServerAuthCode();
        Thread t = new Thread(mConnectServer);
        t.start();
        while (t.isAlive())
            ;

        if(connected) {
            System.out.println("connected");
            InformationHandler.setSocket(mSocket);
            try {
                outputStream = mSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Thread th = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(authcode.getBytes(Charset.forName("UTF-8")));
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            });
            th.start();
        }
    }


    private Runnable mConnectServer = new Runnable() {
        @Override
        public void run() {
            try {
                mSocket = new Socket(ADDRESS, PORT);
                connected = true;
            }
            catch (IOException e){
                e.printStackTrace();
            }
        }
    };
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


}
