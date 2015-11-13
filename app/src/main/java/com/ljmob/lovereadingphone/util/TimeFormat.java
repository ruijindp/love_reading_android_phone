package com.ljmob.lovereadingphone.util;

import android.text.format.DateFormat;

import com.ljmob.lovereadingphone.R;
import com.londonx.lutil.Lutil;

import java.util.Calendar;

/**
 * Created by london on 15/11/13.
 * 时间格式化
 */
public class TimeFormat {
    private static String ago;

    public static String format(long millisecond) {
        if (ago == null) {
            ago = Lutil.context.getString(R.string._ago_year);
        }
        String dateDime = "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecond);
        Calendar calendarNow = Calendar.getInstance();
        int deltaDay = calendarNow.get(Calendar.DAY_OF_YEAR)
                - calendar.get(Calendar.DAY_OF_YEAR);
        if (deltaDay > 365) {//一年前
            dateDime = (deltaDay % 365) + ago;
        } else if (deltaDay > 1) {
            dateDime = DateFormat.format("MM/dd HH:mm", millisecond).toString();
        } else {
            dateDime = DateFormat.format("HH:mm", millisecond).toString();
        }
        return dateDime;
    }
}
