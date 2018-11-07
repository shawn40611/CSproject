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

    public static final String SOCKER_ACTION = "Control";
    public static final String SOCKER_RCV = "ReceiveStr";
    private ReceiveThread listener;
    private GoogleAccountCredential mCredential;


    public static final String ADDRESS = "178.128.90.63";
    public static final int PORT = 8888;

    private static final String SCOPES = "https://www.googleapis.com/auth/calendar";
    private GoogleSignInClient mGoogleSignInClient;

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
                    buf = new byte[1024];
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
                    Intent intent = new Intent(SOCKER_RCV);
                    intent.putExtra("Data", data);

                    sendBroadcast(intent);

                }
            }
        }

    }
}