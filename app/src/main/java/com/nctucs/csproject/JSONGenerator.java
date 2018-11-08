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
    public JSONArray inviteEvent(String name,String location,String description,int preference,int time,int group) {
        JSONObject invitation = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            invitation.put("function", "Add_Event");
            data.put("Event_name",name);
            data.put("Event_location",location);
            data.put("Event_description",description);
            data.put("Event_time",time);
            data.put("Event_preference",preference);
            data.put("Event_group",group);
            invitation.put("Data",data);
            this.arr.put(invitation);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray inviteGroup() {
        JSONObject invitation = new JSONObject();
        try {
            invitation.put("function", "Add_Group");
            this.arr.put(invitation);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray reply(String type, int id, boolean status) {
        JSONObject reply = new JSONObject();
        try {
            reply.put("function", "Reply_Notification");
            reply.put("type", type);
            reply.put("id", id);
            reply.put("status", status);
            this.arr.put(reply);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray confirmEvent(int id, boolean status) {
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