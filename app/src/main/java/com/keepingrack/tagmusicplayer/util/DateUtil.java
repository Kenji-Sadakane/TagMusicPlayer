package com.keepingrack.tagmusicplayer.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    public static String dateToStr(Date date, String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(date);
    }

    public static String getNowDateStr(String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(new Date());
    }

    public static String getDateStr(long date, String dateFormat) {
        return new SimpleDateFormat(dateFormat).format(new Date(date));
    }

    public static long getDateDiff(Date fromDate, Date toDate) {
        long diffSecond = 0;
        if (fromDate != null && toDate != null) {
            diffSecond = (toDate.getTime() - fromDate.getTime()) / 1000;
        }
        return diffSecond;
    }
}
