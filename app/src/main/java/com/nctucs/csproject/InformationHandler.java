package com.nctucs.csproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.services.calendar.CalendarScopes;
import com.nctucs.csproject.Data.EventsStatusData;
import com.nctucs.csproject.Data.GroupData;
import com.nctucs.csproject.Data.NotificationData;
import com.nctucs.csproject.Data.SelectedTimeData;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;

public class InformationHandler {
    public static GoogleSignInAccount mAccount;
    public static Bitmap mphoto;
    public static Boolean isRegister = false;
    private static ArrayList<EventsStatusData> eventsStatusData;
    private static ArrayList<NotificationData> notificationData;
    private static ArrayList<SelectedTimeData> selectedTimeData;
    private static ArrayList<GroupData> groupData;
    private static String me;
    private static final String[] SCOPES = { CalendarScopes.CALENDAR };

    public static void setAccount(GoogleSignInAccount account){
        InformationHandler.mAccount = account;
    }

    public static GoogleSignInAccount getAccount(){
        if(mAccount != null)
            return  mAccount;
        return  null;
    }


    public  static void setBitmap(Bitmap bitmap){
        mphoto = bitmap;
    }
    public static Bitmap getBitmap(){
        return mphoto;
    }
    public static void setName(String name){
        me = name;
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
    public static void addGroupData(GroupData data){
        System.out.println("ADD" + data.group_name);
        groupData.add(data);
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

    public static void updataStatus(int id, JSONArray status){
        for(int i = 0 ; i < eventsStatusData.size() ; i++){
            EventsStatusData data = eventsStatusData.get(i);
            if(data.event_id == id){
                System.out.println(data.events_name);
                try {
                    for(int j = 0 ; j < data.member_list.size();j++){
                        data.reply_status[j] = status.getInt(j);
                    }
                }catch (JSONException e){
                    System.out.println("Exception in update " + e);
                }

            }
        }
    }
    public static void setIsRegister(Boolean reply){
        isRegister = reply;
    }
    public static String getName(){
        return me;
    }

    public static Boolean IsRegister() {
        return isRegister;
    }


}
