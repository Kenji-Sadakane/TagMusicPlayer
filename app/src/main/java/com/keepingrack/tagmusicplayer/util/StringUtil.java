package com.keepingrack.tagmusicplayer.util;

public class StringUtil {
    // String型に変換
    public static String castString(double str) { return String.valueOf(str); }
    public static String castString(float str) { return String.valueOf(str); }
    public static String castString(int str) { return String.valueOf(str); }
    public static String castString(long str) { return String.valueOf(str); }
    public static String castString(boolean str) { return String.valueOf(str); }
    // int型に変換
    public static int castInt(String str, int defaultNum) {
        int result = defaultNum;
        try {
            result = Integer.parseInt(str);
        } catch (Exception e) {
            //
        }
        return result;
    }

    // 引数の文字列を全て連結
    public static String concat(String... str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            sb.append(str[i]);
        }
        return sb.toString();
    }

    // 引数の文字列をカンマ区切りで連結
    public static String csvLine(String... str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length; i++) {
            if (i != 0) {
                sb.append(",");
            }
            sb.append(str[i]);
        }
        return sb.toString();
    }

    // ファイル名から拡張子を削除した名前を返却
    public static String deleteSuffix(String fileName) {
        String result = fileName;
        int point = fileName.lastIndexOf(".");
        if (point != -1) {
            result = fileName.substring(0, point);
        }
        return result;
    }

    public static String listToStr(short[] list) {
        StringBuffer sb = new StringBuffer();
        if (list != null || list.length > 0) {
            for (short elm : list) {
                sb.append(castString(elm));
                sb.append(" ");
            }
        }
        return sb.toString();
    }
}
