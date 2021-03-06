package com.nctucs.csproject;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.Task;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.nctucs.csproject.Activity.EventsStatusActivity;
import com.nctucs.csproject.Activity.MainActivity;
import com.nctucs.csproject.Activity.NotificationActivity;
import com.nctucs.csproject.Activity.WelComeActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class MyService extends Service {
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private final IBinder binder = new MyBinder();
    private Boolean stop = false;

    public static final String SOCKER_RCV = "ReceiveStr";
    private ReceiveThread listener;
    long[] mVibratePattern = new long[]{0, 200, 100, 200};



    public static final String ADDRESS = "178.128.90.63";
    public static final int PORT = 8888;


    @Override
    public void onCreate() {
        super.onCreate();

        if (socket == null) {
            Thread thread;
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket(ADDRESS, PORT);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();
            while (thread.isAlive())
                ;
        }
        if (socket != null) {
            try {
                outputStream = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if (listener == null) {
            listener = new ReceiveThread(socket);
            listener.start();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String s = intent.getStringExtra("Set_up");
        sendData(s);
        System.out.println("onStartCommand "+s);
        return START_REDELIVER_INTENT;

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public void sendData(final String data) {
        if (outputStream != null) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("send =" + data);
                        outputStream.write(data.getBytes(Charset.forName("UTF-8")));
                        outputStream.flush();

                    } catch (IOException e) {
                        System.out.println("send data exception" + e.getMessage());
                    }
                }
            });
            t.start();
        }
    }
    public void sendData(final JSONArray data){
        if (outputStream != null) {
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(data.toString().getBytes(Charset.forName("UTF-8")));
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }

    public class MyBinder extends Binder {
        public MyService getService() {
            return MyService.this;
        }
    }

    public class ReceiveThread extends Thread {
        private Socket mSocket;
        private byte[] buf;
        private String tmp;
        private String data;
        private Vibrator vibrator;
        private NotificationManager manager;

        public ReceiveThread(Socket s) {
            mSocket = s;
            if (mSocket != null) {
                try {
                    inputStream = mSocket.getInputStream();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void run() {
            super.run();
            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ArrayList<NotificationChannel> channels = new ArrayList<NotificationChannel>();
                channels.add(new NotificationChannel("channel1", "name", NotificationManager.IMPORTANCE_DEFAULT));
                channels.add(new NotificationChannel("channel2", "nam2", NotificationManager.IMPORTANCE_DEFAULT));
                manager.createNotificationChannels(channels);
            }
            tmp = "";
            if (mSocket != null) {
                while (!stop && !mSocket.isClosed()) {
                    buf = new byte[20000];
                    try {
                        inputStream.read(buf);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    try {
                        data = new String(buf, "UTF-8").trim();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(data != null) {
                        System.out.println("data = " + data);
                        tmp += data;
                    }
                    JSONParser parser = new JSONParser(tmp);
                    int type = parser.getType();
                    Intent intent = new Intent(SOCKER_RCV);
                    Bundle bag = new Bundle();
                    System.out.println("service type = " + type);
                    bag.putInt("TYPE",type);
                    Notification notification;
                    PendingIntent appIntent;
                    Intent notifyIntent;


                    switch (type){
                        case JSONParser.TYPE_UPDATE_DATA:
                            InformationHandler.setNotificationData(parser.getNotificationData());
                            InformationHandler.setEventsStatusData(parser.getEventStatusData());
                            InformationHandler.setGroupData(parser.getGroupData());
                            InformationHandler.setIsRegister(parser.getVerifyData());
                            if(parser.getVerifyData())
                                InformationHandler.setName(parser.getName());
                            tmp = "";
                            break;
                        case  JSONParser.TYPE_NOTIFICATION:
                            InformationHandler.setNotificationData(parser.getNotificationData());
                            notifyIntent = new Intent(getApplicationContext(), NotificationActivity.class);
                            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if(vibrator.hasVibrator())
                                vibrator.vibrate(mVibratePattern,-1);
                            appIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifyIntent, 0);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                notification = new Notification.Builder(getApplicationContext())
                                        .setContentIntent(appIntent)
                                        .setSmallIcon(R.drawable.ic_notification)
                                        .setTicker("notification on status bar.")
                                        .setWhen(System.currentTimeMillis())
                                        .setAutoCancel(true)
                                        .setContentTitle("New Notification")
                                        .setContentText("There is a new notification!")
                                        .setChannelId("channel1")
                                        .build();
                            }else{
                                notification = new Notification.Builder(getApplicationContext())
                                        .setContentIntent(appIntent)
                                        .setSmallIcon(R.drawable.ic_notification)
                                        .setTicker("notification on status bar.")
                                        .setWhen(System.currentTimeMillis())
                                        .setAutoCancel(true)
                                        .setContentTitle("New Notification")
                                        .setContentText("There is a new notification!")
                                        .build();
                            }
                            manager.notify(123,notification);
                            Message message = new Message();
                            message.what = 2001;
                            Navigation_BaseActivity.callHandler(message);
                            tmp = "";
                            break;
                        case  JSONParser.TYPE_STATUS:
                            InformationHandler.setEventsStatusData(parser.getEventStatusData());
                            if(vibrator.hasVibrator())
                                vibrator.vibrate(mVibratePattern,-1);
                            notifyIntent = new Intent(getApplicationContext(), EventsStatusActivity.class);
                            notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            appIntent = PendingIntent.getActivity(getApplicationContext(), 0, notifyIntent, 0);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                notification = new Notification.Builder(getApplicationContext())
                                        .setContentIntent(appIntent)
                                        .setSmallIcon(R.drawable.ic_notification)
                                        .setTicker("notification on status bar.")
                                        .setWhen(System.currentTimeMillis())
                                        .setAutoCancel(true)
                                        .setContentTitle("Event Build Success")
                                        .setContentText("You Can Check Event Status Now!!")
                                        .setChannelId("channel2")
                                        .build();
                            }else{
                                notification = new Notification.Builder(getApplicationContext())
                                        .setContentIntent(appIntent)
                                        .setSmallIcon(R.drawable.ic_notification)
                                        .setTicker("notification on status bar.")
                                        .setWhen(System.currentTimeMillis())
                                        .setAutoCancel(true)
                                        .setContentTitle("Event Build Success")
                                        .setContentText("You Can Check Event Status Now!!")
                                        .build();
                            }
                            manager.notify(124,notification);
                            Message nmessage = new Message();
                            nmessage.what = 2002;
                            Navigation_BaseActivity.callHandler(nmessage);
                            tmp = "";
                            break;
                        case JSONParser.TYPE_REPLY_VERIFY:
                            InformationHandler.setIsRegister(parser.getVerifyData());
                            tmp = "";
                            break;
                        case JSONParser.TYPE_GROUP_LIST:
                            InformationHandler.setGroupData(parser.getGroupData());
                            tmp = "";
                            break;
                        case JSONParser.TYPE_REPLY_REGISTER:
                            if(vibrator.hasVibrator())
                                vibrator.vibrate(mVibratePattern,-1);
                            intent.putExtra("reply",parser.getReplyRegister());
                            tmp = "";
                            break;
                        case JSONParser.TYPE_REPLY_ADD_EVENT:
                            InformationHandler.setSelectedTimeData(parser.getSelectData());
                            tmp = "";
                            break;
                        case JSONParser.TYPE_UPDATE_STATUS:
                            JSONArray temp = parser.getUpdateStatusData();
                            try {
                                for(int i = 0 ; i < temp.length() ; i++) {
                                    JSONObject object = temp.getJSONObject(i);
                                    int id = object.getInt("Event_id");
                                    JSONArray status = object.getJSONArray("Status");
                                    InformationHandler.updataStatus(id, status);
                                }
                            }catch (JSONException e){

                            }
                            tmp = "";
                            break;
                        case JSONParser.TYPE_REPLY_CREATE:
                            int group_id = parser.getGroupID();
                            bag.putInt("group_id",group_id);
                            tmp = "";
                            break;
                        case JSONParser.TYPE_REPLY_SEARCH:
                            JSONArray array = parser.getSearchData();
                            bag.putString("SearchData",array.toString());
                            tmp = "";
                            break;
                        case JSONParser.TYPE_EXCEPTION:
                            continue;

                    }
                    intent.putExtras(bag);

                    sendBroadcast(intent);

                }
            }
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("On service destroy");
        JSONObject object = new JSONObject();
        stop = true;
        try {
            if(outputStream != null)
                outputStream.close();
            if(inputStream != null)
                inputStream.close();
            if(socket != null) {
                socket.shutdownOutput();
                socket.shutdownInput();
            }
        }catch (IOException e){
            System.out.println(e.getMessage().toString());
        }

    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        System.out.println("On task destroy");
        this.stopSelf();


    }
}