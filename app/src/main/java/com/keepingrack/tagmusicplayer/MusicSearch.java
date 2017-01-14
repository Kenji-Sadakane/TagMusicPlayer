package com.keepingrack.tagmusicplayer;

import android.database.Cursor;

import com.keepingrack.tagmusicplayer.db.DBAdapter;
import com.keepingrack.tagmusicplayer.db.entity.MusicTagsRecord;
import com.keepingrack.tagmusicplayer.db.helper.MusicTagsHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;
import static com.keepingrack.tagmusicplayer.MainActivity.tagKinds;

public class MusicSearch {

    private MainActivity activity;
    private DBAdapter dbAdapter;
    private MusicTagsHelper musicTagsHelper;

    public MusicSearch(MainActivity _activity) {
        this.activity = _activity;
    }

    private void before() {
        dbAdapter = new DBAdapter(activity);
        dbAdapter.open();
        musicTagsHelper = new MusicTagsHelper(dbAdapter.db);
    }
    private void after() {
        dbAdapter.close();
    }
    private List<MusicTagsRecord> cursorToRecords(Cursor cursor) {
        List<MusicTagsRecord> records = new ArrayList<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            MusicTagsRecord record = new MusicTagsRecord(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            records.add(record);
            cursor.moveToNext();
        }
        return records;
    }

    public List<MusicTagsRecord> selectMusicAndTags() {
        before();

        List<MusicTagsRecord> records = cursorToRecords(musicTagsHelper.getAllRecords());
        Set<String> set = new HashSet<>();
        for (MusicTagsRecord record : records) {
            String key = record.getKey();
            String filePath = record.getFilePath();
            File file = new File(filePath);
            String tags = record.getTags();
            List<String> tagArray = Arrays.asList(tags.split("\t"));
            set.addAll(tagArray);
            musicItems.put(key, new MusicItem(file.getName(), file, tagArray, null));
        }
        tagKinds = new ArrayList<>(set);

        after();
        return records;
    }

    // 仮実装
//    public void insert() {
//        before();
//        for (Map.Entry<String, MusicItem> musicItemMap : musicItems.entrySet()) {
//            String key = musicItemMap.getKey();
//            String filePath = musicItemMap.getValue().getFile().getAbsolutePath();
//            StringBuffer tags = new StringBuffer();
//            for (String tag : musicItemMap.getValue().getTags()) {
//                tags.append(tag);
//                tags.append("\t");
//            }
//            musicTagsHelper.insert(key, filePath, tags.toString());
//        }
//        after();
//    }
}
