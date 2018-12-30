package com.keepingrack.tagmusicplayer.util;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.keepingrack.tagmusicplayer.util.ListUtil.*;
import static com.keepingrack.tagmusicplayer.util.StringUtil.*;

public class FileUtil {
    /**
     * 指定パス配下のファイル数を再帰的にカウント(※ディレクトリは除外)
     * @return ファイル数
     */
    public static int getFileCountRecursive(String path) {
        int count = 0;
        File[] files = new File(path).listFiles();
        if (files != null && files.length > 0) {
            for (File file : files) {
                if (file.isFile()) {
                    count++;
                }
                if (file.isDirectory()) {
                    count += getFileCountRecursive(file.getAbsolutePath());
                }
            }
        }
        return count;
    }

    /**
     * ルートから指定されたディレクトリパスまで再帰的に作成
     * @return 作成成否
     */
    public static boolean makeDirRecursive(String absoluteDirPath) {
        List<String> dirNameList = removeEmptyItem(Arrays.asList(absoluteDirPath.split("/")));
        String tmpPath = "/";
        for (int i = 0; i < dirNameList.size(); i++) {
            tmpPath = concat(tmpPath, dirNameList.get(i), "/");
            makeDirIfNotExists(tmpPath);
        }
        return (isExistsFile(absoluteDirPath));
    }

    // 指定パスのディレクトリが存在しなければ作成(※親ディレクトリは存在している前提)
    public static void makeDirIfNotExists(String absoluteDirPath) {
        File dir = new File(absoluteDirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    // 指定パスのファイルが存在するかチェック
    public static boolean isExistsFile(String absolutePath) {
        File file = new File(absolutePath);
        return (file.exists());
    }
}
