package com.nctucs.csproject;
import org.json.*;

public class JSONGenerator {
    private JSONArray arr;

    public JSONGenerator(){
        arr = new JSONArray();
    }

    public JSONArray setUp(String data_auth, String data_verify) {
        JSONObject auth = new JSONObject();
        JSONObject verify = new JSONObject();
        try {
            auth.put("function","Auth");
            auth.put("Data", data_auth);
            verify.put("function", "Verify");
            verify.put("Data", data_verify);
            this.arr.put(auth);
            this.arr.put(verify);
        }catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray register(String stu_id, String name, String email) {
        JSONObject register = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            register.put("function", "Register");
            data.put("Student_id", stu_id);
            data.put("Name", name);
            data.put("Email", email);
            register.put("Data", data);
            this.arr.put(register);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray inviteEvent(String name,String location,String description,int preference,int time,int group,long now_date) {
        JSONObject invitation = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            invitation.put("function", "Add_Event");
            data.put("Event_name",name);
            data.put("Event_location",location);
            data.put("Event_description",description);
            data.put("Event_time",(time == 0? 30 : time*60));
            data.put("Event_preference",preference);
            data.put("Event_group",group);
            data.put("Event_date",now_date);
            invitation.put("Data",data);
            this.arr.put(invitation);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray selectTime(int id){
        JSONObject select = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            data.put("Event_id",id);
            select.put("function", "SelectedTime");
            select.put("Data",data);
            arr.put(select);
        }catch (JSONException e){
            e.printStackTrace();
        }

        return  arr;
    }
    public JSONArray createGroup(String name) {
        JSONObject invitation = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            invitation.put("function", "CreateGroup");
            data.put("group_name",name);
            invitation.put("Data",data);
            this.arr.put(invitation);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray search(String value){
        JSONObject search = new JSONObject();
        try {
            search.put("function","Search");
            search.put("Data",value);
            arr.put(search);

        }catch (JSONException e){
            e.printStackTrace();
        }
        return this.arr;
    }
    public JSONArray addmember(int group_id,int user_id){
        JSONObject add = new JSONObject();
        JSONObject data = new JSONObject();
        try{
            add.put("function","AddMember");
            data.put("group_id",group_id);
            data.put("user_id",user_id);
            add.put("Data",data);
            this.arr.put(add);
        }catch (JSONException e){
            e.printStackTrace();
        }
        return this.arr;
    }
    public JSONArray reply(String type, int id, int status) {
        JSONObject reply = new JSONObject();
        try {
            JSONObject data = new JSONObject();
            reply.put("function", "Reply_Notification");
            data.put("type", type);
            data.put("id", id);
            data.put("status", status);
            reply.put("Data",data);
            this.arr.put(reply);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray confirmEvent(int id, int status) {
        JSONObject confirm = new JSONObject();
        try {
            confirm.put("function", "ConfirmEvent");
            confirm.put("id", id);
            confirm.put("status", status);
            this.arr.put(confirm);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }



}

//"[{\"function\" : \"Auth\", \"Data\" : \"abc\"}, {\"function\" : \"Verify\", \"Data\" : \"def\"}]"