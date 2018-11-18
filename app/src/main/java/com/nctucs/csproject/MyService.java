package com.nctucs.csproject;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
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
import com.nctucs.csproject.Activity.MainActivity;
import com.nctucs.csproject.Activity.WelComeActivity;

import org.json.JSONArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Arrays;

import static android.content.ContentValues.TAG;

public class MyService extends Service {
    private Socket socket;
    private OutputStream outputStream;
    private final IBinder binder = new MyBinder();
    private Boolean stop = false;
    private GoogleSignInAccount mAccount;
    private Bitmap user_photo;

    public static final String SOCKER_RCV = "ReceiveStr";
    private ReceiveThread listener;
    private GoogleAccountCredential mCredential;


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
        return super.onStartCommand(intent, flags, startId);

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
                        outputStream.write(data.getBytes(Charset.forName("UTF-8")));
                        outputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
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
        private InputStream inputStream;
        private byte[] buf;
        private String data;

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
                    if(data != null)
                        System.out.println("data = "+data);
                    JSONParser parser = new JSONParser(data);
                    int type = parser.getType();
                    Intent intent = new Intent(SOCKER_RCV);
                    intent.putExtra("TYPE",type);
                    switch (type){
                        case JSONParser.TYPE_UPDATE_DATA:
                            InformationHandler.setNotificationData(parser.getNotificationData());
                            InformationHandler.setEventsStatusData(parser.getEventStatusData());
                            InformationHandler.setGroupData(parser.getGroupData());
                            InformationHandler.setIsRegister(parser.getVerifyData());
                            break;
                        case  JSONParser.TYPE_NOTIFICATION:
                            InformationHandler.setNotificationData(parser.getNotificationData());
                            break;
                        case  JSONParser.TYPE_STATUS:
                            InformationHandler.setEventsStatusData(parser.getEventStatusData());
                            break;
                        case JSONParser.TYPE_REPLY_VERIFY:
                            InformationHandler.setIsRegister(parser.getVerifyData());
                            break;
                        case JSONParser.TYPE_GROUP_LIST:
                            break;
                        case JSONParser.TYPE_REPLY_REGISTER:
                            intent.putExtra("reply",parser.getReplyRegister());
                            break;
                        case JSONParser.TYPE_REPLY_ADD_EVENT:
                            InformationHandler.setSelectedTimeData(parser.getSelectData());
                            break;

                    }

                    sendBroadcast(intent);

                }
            }
        }

    }
}