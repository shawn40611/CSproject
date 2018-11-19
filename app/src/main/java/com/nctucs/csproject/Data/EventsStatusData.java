package com.nctucs.csproject.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class EventsStatusData extends Object{
    public int event_id;
    public String events_name = "";
    public Date   events_start_time;
    public Date events_end_time;
    public ArrayList<String> member_list;
    public int [] reply_status;

}
