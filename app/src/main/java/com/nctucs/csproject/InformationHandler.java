package com.nctucs.csproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;
import com.nctucs.csproject.Data.EventsStatusData;
import com.nctucs.csproject.Data.GroupData;
import com.nctucs.csproject.Data.NotificationData;
import com.nctucs.csproject.Data.SelectedTimeData;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class InformationHandler {
    public static Socket socket;
    public static GoogleSignInAccount mAccount;
    public static GoogleAccountCredential mCredential;
    public static Bitmap mphoto;
    public static GoogleSignInClient mClient;
    public static Boolean isRegister = false;
    private static ArrayList<EventsStatusData> eventsStatusData;
    private static ArrayList<NotificationData> notificationData;
    private static ArrayList<SelectedTimeData> selectedTimeData;
    private static ArrayList<GroupData> groupData;
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

    public static void setEventsStatusData(ArrayList<EventsStatusData> data){
        if(eventsStatusData == null)
            eventsStatusData = data;
        else eventsStatusData.addAll(data);
    }
    public static void setNotificationData(ArrayList<NotificationData> data){
            notificationData = data;
    }
    public static  void setSelectedTimeData(ArrayList<SelectedTimeData> data){
        selectedTimeData = data;
    }
    public static void setGroupData(ArrayList<GroupData> data){
        groupData = data;
    }

    public static ArrayList<GroupData> getGroupData(){return groupData;}

    public  static ArrayList<SelectedTimeData> getSelectedTimeData(){
        return selectedTimeData;
    }

    public static ArrayList<EventsStatusData> getEventsStatusData() {
        return eventsStatusData;
    }

    public static ArrayList<NotificationData> getNotificationData() {
        return notificationData;
    }
    public static void setIsRegister(Boolean reply){
        isRegister = reply;
    }

    public static Boolean IsRegister() {
        return isRegister;
    }
}
