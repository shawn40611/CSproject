package com.nctucs.csproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;

import java.net.Socket;
import java.util.Arrays;

public class InformationHandler {
    public static Socket socket;
    public static GoogleSignInAccount mAccount;
    public static GoogleAccountCredential mCredential;
    public static Bitmap mphoto;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };

    public static void setAccount(GoogleSignInAccount account){
        InformationHandler.mAccount = account;
    }

    public static GoogleSignInAccount getAccount(){
        if(mAccount != null)
            return  mAccount;
        return  null;
    }

    public static GoogleAccountCredential getCredential(){
            return  mCredential;
    }

    public static void setCredential(GoogleAccountCredential credential){
        mCredential = credential;
    }
    public  static void setBitmap(Bitmap bitmap){
        mphoto = bitmap;
    }
    public static Bitmap getBitmap(){
        return mphoto;
    }
    public static synchronized Socket getSocket(){
        return socket;
    }

    public static synchronized void setSocket(Socket socket){
        InformationHandler.socket = socket;
    }

}
