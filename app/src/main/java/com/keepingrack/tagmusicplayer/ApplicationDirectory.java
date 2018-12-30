package com.keepingrack.tagmusicplayer;

import android.content.Context;
import android.os.Build;
import android.os.storage.StorageManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static com.keepingrack.tagmusicplayer.util.FileUtil.*;
import static com.keepingrack.tagmusicplayer.util.StringUtil.*;

public class ApplicationDirectory {
    private static String RELATIVE_PATH = "Android/data/com.keepingrack.tagmusicplayer/files";

    public static String getAbsolutePath(Context context, boolean isSdCard) throws Exception {
        String absolutePath = "";
        String tmpPath = "";
        // Android 4.4 未満
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            List<String> storageDirPathList = getStorageDirPathList(context, isSdCard);
            for (String storageDirPath : storageDirPathList) {
                tmpPath = concat(storageDirPath, "/", RELATIVE_PATH);
                if (isExistsFile(tmpPath) || makeDirRecursive(tmpPath)) {
                    absolutePath = tmpPath;
                    break;
                }
            }
        }
        // Android 4.4 以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            File[] dirs = context.getExternalFilesDirs(null); // dirs[1]が外部ストレージのはず
            absolutePath = (!isSdCard) ? dirs[0].getAbsolutePath() : (dirs.length >= 2) ? dirs[1].getAbsolutePath() : "";
        }
        return absolutePath;
    }

    private static List<String> getStorageDirPathList(Context context, boolean isSdCard) throws Exception {
        List<String> sdCardDirPathList = new ArrayList<>();
        List<String> innerStorageDirPathList = new ArrayList<>();
        StorageManager sm = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        // StorageVolumeの一覧を取得する。非公開メソッドなので、リフレクションを使用。
        // Environment.getExternalStorageDirectory を追うと、
        // StorageManagerを使ってStorageVolumeを取得しているのがわかる。
        Method getVolumeListMethod = sm.getClass().getDeclaredMethod("getVolumeList");
        Object[] volumeList = (Object[]) getVolumeListMethod.invoke(sm);

        for (Object volume : volumeList) {
            // getPathFileメソッドは、StorageVolumeのFileオブジェクトを取得するメソッド。
            Method getPathFileMethod = volume.getClass().getDeclaredMethod("getPathFile");
            File file = (File) getPathFileMethod.invoke(volume);
            String storageBasePath = file.getAbsolutePath();
            // isRemovableメソッドは、StorageVolumeが取り外し可能かどうかを判定するメソッド。
            Method isRemovableMethod = volume.getClass().getDeclaredMethod("isRemovable");
            boolean isRemovable = (boolean) isRemovableMethod.invoke(volume);
            // ストレージが取り外し可能かどうか（SDカードかどうか）を判定。
            if (isRemovable) {
                // ベースパスがマウントされているかどうかを判定。
                if (isMountedPath(storageBasePath)) {
                    // StorageVolumeの中で、取り外し可能でかつマウント済みのパスは、SDカード。
                    // マウント済みかどうかを確認しないと、機種によっては /mnt/Private などのパスも含まれてしまうことがある。
                    if (!sdCardDirPathList.contains(storageBasePath)) {
                        sdCardDirPathList.add(storageBasePath);
                    }
                }
            } else {
                // StorageVolumeの中で、取り外し不可能なパスは、内部ストレージ。
                if (!innerStorageDirPathList.contains(storageBasePath)) {
                    innerStorageDirPathList.add(storageBasePath);
                }
            }
        }
        // Android4.4系のみ、getExternalFilesDirs で一度filesディレクトリを生成する必要がある。
        // Android4.4系は、File.mkdirsなどでfilesディレクトリを生成できないため。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            context.getExternalFilesDirs(null);
        }

        if (isSdCard) {
            return sdCardDirPathList;
        } else {
            return innerStorageDirPathList;
        }
    }

    private static boolean isMountedPath(String basePath) throws Exception {
        boolean isMounted = false;
        BufferedReader br = null;

        try {
            // マウントポイントを取得する
            File mounts = new File("/proc/mounts");
            if (mounts.exists()) {
                br = new BufferedReader(new FileReader(mounts));
                String line;
                // マウントポイントに該当するパスがあるかチェックする
                while ((line = br.readLine()) != null) {
                    if (line.contains(basePath)) {
                        // 該当するパスがあればマウントされているため、処理を終える
                        isMounted = true;
                        break;
                    }
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
        return isMounted;
    }
}
