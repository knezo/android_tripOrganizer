package com.example.triporganizer;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {

    public static String timestampToTime(long ts){
        Date tripDate = new Date(ts);
        SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
        String time = sdfTime.format(tripDate);
        return time;
    }

    public static String timestampToDate(long ts){
        Date tripDate = new Date(ts);
        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy");
        String date = sdfDate.format(tripDate);
        return date;
    }
}
