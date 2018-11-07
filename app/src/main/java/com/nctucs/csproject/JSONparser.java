package com.nctucs.csproject;
import org.json.*;

public class JSONparser {
    private JSONArray arr;
    public JSONArray SetUp(String data_auth, String data_verify) {
        JSONObject auth = new JSONObject();
        JSONObject verify = new JSONObject();
        try {
            auth.put("function","Auth");
            auth.put("data", data_auth);
            verify.put("function", "Verify");
            verify.put("function", data_verify);
            this.arr.put(auth);
            this.arr.put(verify);
        }catch(Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray Register(String stu_id, String name, String email) {
        JSONObject register = new JSONObject();
        JSONObject data = new JSONObject();
        try {
            register.put("function", "Register");
            data.put("student_id", stu_id);
            data.put("Name", name);
            data.put("Email", email);
            register.put("Data", data);
            this.arr.put(register);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray InviteEvent() {
        JSONObject invitation = new JSONObject();
        try {
            invitation.put("function", "Add_Event");
            this.arr.put(invitation);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray InviteGroup() {
        JSONObject invitation = new JSONObject();
        try {
            invitation.put("function", "Add_Group");
            this.arr.put(invitation);
        }catch(Exception e){
            System.err.println("Error: " + e.getMessage());
        }
        return this.arr;
    }
    public JSONArray Reply(String type, int id, boolean status) {
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
    public JSONArray ConfirmEvent(int id, boolean status) {
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