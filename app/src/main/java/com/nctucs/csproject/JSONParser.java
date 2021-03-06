package com.nctucs.csproject;

import com.nctucs.csproject.Data.EventsStatusData;
import com.nctucs.csproject.Data.GroupData;
import com.nctucs.csproject.Data.NotificationData;
import com.nctucs.csproject.Data.SelectedTimeData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class JSONParser {
    private Boolean reply_verify;
    private JSONArray object_update_status;
    private JSONArray  mArray,object_notification,object_status,object_groups_list,object_select_time
            ,object_search;
    private int group_id;
    private int reply_register;
    private String name;
    int type;
    public static final int TYPE_EXCEPTION = 1999;
    public static final int TYPE_REPLY_VERIFY = 2000;
    public static final int TYPE_UPDATE_DATA = 2001;
    public static final int TYPE_NOTIFICATION = 2002;
    public static final int TYPE_STATUS = 2003;
    public static final int TYPE_GROUP_LIST = 2004;
    public static final int TYPE_UPDATE_STATUS = 2005;
    public static final int TYPE_REPLY_REGISTER = 2006;
    public static final int TYPE_REPLY_ADD_EVENT = 2007;
    public static final int TYPE_REPLY_SEARCH = 2008;
    public static final int TYPE_REPLY_CREATE = 2009;

   public JSONParser(String input){
       parse(input);
    }
    public void parse(String input){
        try{
            mArray = new JSONArray(input);
            for(int i = 0 ; i < mArray.length() ; i++){
                JSONObject a = mArray.getJSONObject(i);
                String funciton = a.getString("function");
                if(funciton.equals("ReplyVerify")){
                    type = TYPE_REPLY_VERIFY;
                    reply_verify = a.getBoolean("Data");
                    name = a.getString("Name");
                }
                else if(funciton.equals("Notification")){
                    type = TYPE_NOTIFICATION;
                    object_notification = a.getJSONArray("Data");
                }
                else if(funciton.equals("Status")){
                    type = TYPE_STATUS;
                    object_status = a.getJSONArray("Data");
                }
                else if(funciton.equals("Group_List")){
                    type = TYPE_GROUP_LIST;
                    object_groups_list = a.getJSONArray("Data");
                }
                else if(funciton.equals("update_status")){
                    type = TYPE_UPDATE_STATUS;
                    object_update_status = a.getJSONArray("Data");
                }
                else if(funciton.equals("ReplyRegister")){
                    type = TYPE_REPLY_REGISTER;
                    reply_register = a.getInt("Data");
                }
                else if(funciton.equals("ReplyAddEvent")){
                    type = TYPE_REPLY_ADD_EVENT;
                    object_select_time = a.getJSONArray("Data");

                }
                else if(funciton.equals("update_group")){
                    type = TYPE_GROUP_LIST;
                    object_groups_list = a.getJSONArray("Data");
                }
                else if(funciton.equals("Search")){
                    type = TYPE_REPLY_SEARCH;
                    object_search = a.getJSONArray("Data");
                }
                else if(funciton.equals("CreateGroup")){
                    type = TYPE_REPLY_CREATE;
                    System.out.println("parse = " + type);
                    JSONObject object_create;
                    object_create = a.getJSONObject("Data");
                    group_id = object_create.getInt("group_id");
                    System.out.println("parse id= " + group_id);
                }
            }
            if(mArray.length() > 1)
                type = TYPE_UPDATE_DATA;
        }catch (JSONException e){
            System.out.println("parse"+e.getMessage());
            type = TYPE_EXCEPTION;
        }
    }
    public int getType(){
       return  type;
    }

    public int getReplyRegister(){
       return reply_register;
    }
    public Boolean getVerifyData(){
        return reply_verify;
    }
    public ArrayList<NotificationData> getNotificationData(){
       ArrayList<NotificationData> data_list = new ArrayList<NotificationData>();
       try{
           for(int i = 0 ; i < object_notification.length() ; i++){
               NotificationData data = new NotificationData();
               JSONObject tmp = object_notification.getJSONObject(i);
               String n_type = tmp.getString("Type");
               if(n_type.equals("Group")){
                   data.type = 0;
                   data.group_id = tmp.getInt("Group_id");
                   data.group_name = tmp.getString("Group_name");
                   data.group_inviter = tmp.getString("Group_inviter");
               }else if(n_type.equals("invitation")){
                    data.type = 1;
                    data.events_name = tmp.getString("Event_name");
                    data.event_id = tmp.getInt("Event_id");
                    data.event_groups = tmp.getString("Event_group");
                    data.event_inviter = tmp.getString("Event_inviter");
                    data.event_description = tmp.getString("Event_description");
                    data.event_start_time = new Date(tmp.getLong("Event_start_time")*1000);
                    data.event_end_time = new Date(tmp.getLong("Event_end_time")*1000);
               }
               data_list.add(data);

           }
       }catch (JSONException e){
           System.out.println("getNoti"+e.getMessage());
       }
       System.out.println("Return = " + data_list.size());
       return data_list;
    }

    public ArrayList<EventsStatusData> getEventStatusData(){
       ArrayList<EventsStatusData> data_list = new ArrayList<EventsStatusData>();
       try{
           for(int i = 0 ; i <object_status.length() ; i++){
               JSONObject tmp  = object_status.getJSONObject(i);
               EventsStatusData data = new EventsStatusData();
               data.event_id = tmp.getInt("Event_id");
               data.events_name = tmp.getString("Event_name");
               data.events_start_time = new Date(tmp.getLong("Event_start_time"));
               data.events_end_time = new Date(tmp.getLong("Event_end_time"));
               JSONArray member,status;
               member = tmp.getJSONArray("Member");
               status = tmp.getJSONArray("Status");
               ArrayList<String> member_list = new ArrayList<String>(member.length());
               int [] status_list = new int [status.length()];
               for(int j = 0 ;j < member.length() ; j++){
                    member_list.add(member.getString(j));
                    status_list[j] = status.getInt(j);
               }
               data.member_list = member_list;
               data.reply_status = status_list;

               data_list.add(data);
           }
       }catch (JSONException e){
           System.out.println("getEvent"+e.getMessage());
       }
       return data_list;
    }

    public ArrayList<GroupData> getGroupData(){
       ArrayList<GroupData> data_list = new ArrayList<GroupData>();
       try{
           for (int i = 0 ; i < object_groups_list.length() ; i++){
                JSONObject tmp = object_groups_list.getJSONObject(i);
                GroupData data = new GroupData();
                data.group_id = tmp.getInt("Group_id");
                data.group_name = tmp.getString("Group_name");
                JSONArray member = tmp.getJSONArray("Group_member");
                ArrayList<String> member_list = new ArrayList<String>();
                for(int j = 0 ; j < member.length() ; j++){
                    member_list.add(member.getString(j));
                }
                data.member_list = member_list;
                data_list.add(data);
           }
       }catch (JSONException e){
           e.printStackTrace();
       }
       return data_list;
    }

    public ArrayList<SelectedTimeData> getSelectData(){
       ArrayList<SelectedTimeData> data_list = new ArrayList<SelectedTimeData>();
       try{
           for(int i = 0 ; i < object_select_time.length() ; i++){
               JSONObject tmp = object_select_time.getJSONObject(i);
               SelectedTimeData data = new SelectedTimeData();
               data.start = tmp.getLong("Starttime");
               data.end = tmp.getLong("Endtime");
               data.event_id = tmp.getInt("Event_id");
               data.attendnumber = tmp.getInt("Attendnumber");
               data_list.add(data);
           }

       }catch (JSONException e){
           e.printStackTrace();
       }
       return  data_list;
    }

    public JSONArray getUpdateStatusData(){
       return object_update_status;
    }

    public JSONArray getSearchData(){
       return object_search;
    }

    public int getGroupID(){
       return group_id;
    }

    public String getName(){
       return name;
    }



}