package com.nctucs.csproject.Data;

import java.util.ArrayList;
import java.util.Date;

public class NotificationData extends Object{
    public int type;
    public int group_id;
    public String group_inviter;
    public String group_name;
    public ArrayList<String> member_list;
    public int event_id;
    public String events_name = "";
    public String event_groups = "";
    public String event_inviter = "";
    public Date   events_time;
    public String event_description = "";
}
