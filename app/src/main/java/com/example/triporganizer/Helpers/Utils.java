package com.example.triporganizer.Helpers;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static java.lang.Integer.parseInt;

public class Utils {

    public static long timeToTimestamp(String time, String date){
        String[] arrTmp = time.split(":");
        int timeHour = parseInt(arrTmp[0]);
        int timeMin = parseInt(arrTmp[1]);

        arrTmp = date.split("[.]");
        int dateDay = parseInt(arrTmp[0]);
        int dateMonth = parseInt(arrTmp[1]);
        int dateYear = parseInt(arrTmp[2]);

        Calendar calendar = new GregorianCalendar(dateYear, dateMonth-1, dateDay, timeHour, timeMin);
        long timestamp = calendar.getTimeInMillis();

        return timestamp;
    }

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
