package com.nctucs.csproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.Arrays;

public class InformationHandler {
    public static Socket socket;
    public static GoogleSignInAccount mAccount;
    public static GoogleAccountCredential mCredential;
    public static Bitmap mphoto;
    public static GoogleSignInClient mClient;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };

    public static void setAccount(GoogleSignInAccount account){
        InformationHandler.mAccount = account;
    }
    public static GoogleSignInClient getClient(){
        if(mClient == null){

        }

            return mClient;

    }
    public static  void setClient(GoogleSignInClient client){
        mClient = client;
    }

    public static GoogleSignInAccount getAccount(){
        if(mAccount != null)
            return  mAccount;
        return  null;
    }

    public static GoogleAccountCredential getCredential(Context context){
            if(mCredential == null){
                mCredential =  GoogleAccountCredential.usingOAuth2(
                        context, Arrays.asList(SCOPES))
                        .setSelectedAccount(mAccount.getAccount());
            }
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


}
