package com.nctucs.csproject;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Arrays;

public class MyService extends Service{
    private Socket socket;
    private OutputStream outputStream;
    private final IBinder binder = new MyBinder();
    private Boolean stop = false;

    public static final String SOCKER_ACTION = "Control";
    public static final String SOCKER_RCV = "ReceiveStr";
    private ReceiveThread listener;


    public static final String ADDRESS = "178.128.90.63";
    public static final int PORT = 8888;

    @Override
    public void onCreate() {
        super.onCreate();

        socket = InformationHandler.getSocket();
        if(socket == null){
            try {
                socket = new Socket(ADDRESS,PORT);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        if(socket != null){
            try {
                outputStream = socket.getOutputStream();
            }catch (IOException e){
                e.printStackTrace();
            }

        }
        listener = new ReceiveThread(socket);
        listener.start();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    void SendData(final String data){
        if(outputStream != null){
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        outputStream.write(data.getBytes(Charset.forName("UTF-8")));
                        outputStream.flush();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            });
            t.start();
        }
    }
    public class MyBinder extends Binder{
        MyService getService(){
            return MyService.this;
        }
    }
    public class ReceiveThread extends Thread{
        private Socket mSocket;
        private InputStream inputStream;
        private byte [] buf;
        private String data;

        public ReceiveThread(Socket s){
            mSocket = s;
            try {
                inputStream = mSocket.getInputStream();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            super.run();
            while(!stop && !socket.isClosed()){
                buf = new byte[1024];
                try{
                    inputStream.read(buf);
                }catch (IOException e){
                    e.printStackTrace();
                }

                try {
                    data = new String(buf,"UTF-8").trim();
                }catch (UnsupportedEncodingException e){
                    e.printStackTrace();
                }
                System.out.println("Service:"+data);
                Intent intent = new Intent(SOCKER_RCV);
                intent.putExtra("Data",data);

                sendBroadcast(intent);

            }
        }

    }
}
