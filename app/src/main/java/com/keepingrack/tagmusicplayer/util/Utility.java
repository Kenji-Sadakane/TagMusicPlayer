package com.keepingrack.tagmusicplayer.util;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Utility {
    public static String getDateYMD() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }

    public static String listToString(List<String> list, String separate) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (!separate.isEmpty() && i < list.size() - 1) {
                sb.append(separate);
            }
        }
        return sb.toString();
    }

    public static List<String> stringToList(String str, String separate) {
        return Arrays.asList(str.split(separate));
    }
}
