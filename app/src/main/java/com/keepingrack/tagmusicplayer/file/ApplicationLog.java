package com.keepingrack.tagmusicplayer.file;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.util.DateUtil.*;
import static com.keepingrack.tagmusicplayer.util.StringUtil.*;

public class ApplicationLog {
    private static String INFO = "INFO";
    private static String WARNING = "WARN";
    private static String ERROR = "ERROR";

    public static void info(String msg) {
        appendLog(INFO, msg);
    }

    public static void warn(String msg) {
        appendLog(WARNING, msg);
    }

    public static void error(String msg) {
        appendLog(ERROR, msg);
    }

    public static void error(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        pw.flush();
        appendLog(ERROR, sw.toString());
    }

    private static void appendLog(String type, String msg) {
        appendLog(concat(getNowDateStr("yyyy/MM/dd HH:mm:ss.SSS"), " ", "[", type, "]", " ", getCalledFromInfo(), " ", msg));
    }

    // 呼出元情報取得
    // ※ファイル名、メソッド名、行番号を取得
    private static String getCalledFromInfo() {
        String result = "";
        StackTraceElement[] steArray = Thread.currentThread().getStackTrace();
        if (steArray.length > 5) {
            StackTraceElement ste = steArray[5]; // 呼出元情報取得(idx番号は遡りたい数+2)
            result = concat(deleteSuffix(ste.getFileName()), "#", ste.getMethodName(), ":", castString(ste.getLineNumber()));
        }
        return result;
    }

    // ファイル作成or追記
    private static void appendLog(String line) {
        try {
            File file = new File(createFileName());
            FileWriter fw = new FileWriter(file, true);
            PrintWriter pw = new PrintWriter(fw);
            pw.println(line);
            pw.flush();
            fw.close();
            pw.close();
        } catch (Exception e) {
            // ログに書き込めない状態
            activity.alertErrorDialog();
        }
    }

    // ファイル名作成
    private static String createFileName() {
        return concat(activity.baseDirPath, "/", "app_", getNowDateStr("yyyyMMdd"), ".log");
    }
}
