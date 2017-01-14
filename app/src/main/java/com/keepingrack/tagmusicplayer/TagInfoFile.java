package com.keepingrack.tagmusicplayer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.keepingrack.tagmusicplayer.MainActivity.TAG_INFO_PATH;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;
import static com.keepingrack.tagmusicplayer.MainActivity.tagKinds;

public class TagInfoFile {

    public static final String SEPARATE = "\t";
    public static final String LINE_FEED_CODE = "\r\n";
    public static final String NO_TAG_WORD = "タグなし";


    // タグ情報取得
    public void readTagInfo() throws Exception {
        // 初期化
        tagKinds.clear();
        // ファイル読込
        File tagFile = new File(TAG_INFO_PATH);
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(tagFile),"UTF-8"));
        String line;
        while ((line = br.readLine()) != null) {
            // 行ごとにkey valueにマッピング(key=ファイル名, value=タグ情報)
            String[] array = line.split(SEPARATE);
            String key = array[0];
            List<String> tags = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                if (i == 0 || i == 1) {
                    // 0番目はハッシュ値、1番目は曲名なのでスルー
                    continue;
                }
                if (array[i].length() > 0) {
                    tags.add(array[i]);
                    if (!tagKinds.contains(array[i])) { tagKinds.add(array[i]); }
                }
            }
            if (musicItems.get(key) != null) {
                musicItems.get(key).setTags(tags);
            }
        }

        //終了処理
        br.close();
    }

    // タグ情報書き込み
    public void writeTagInfo() throws Exception {
        File tagFile = new File(TAG_INFO_PATH);
        PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tagFile),"UTF-8")));
        for (Map.Entry<String, MusicItem> musicItemMap : musicItems.entrySet()) {
            String key = musicItemMap.getKey();
            MusicItem musicItem = musicItemMap.getValue();
            pw.print(key);
            pw.print(SEPARATE);
            pw.print(musicItem.getTitle());
            if (musicItem.getTags() != null) {
                for (String tag : musicItem.getTags()) {
                    pw.print(SEPARATE);
                    pw.print(tag);
                }
            } else {
                pw.print(SEPARATE);
                pw.print(NO_TAG_WORD);
            }
            pw.print(LINE_FEED_CODE);
        }
        pw.close();
    }
}
