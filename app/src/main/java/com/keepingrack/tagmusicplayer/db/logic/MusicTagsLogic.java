package com.keepingrack.tagmusicplayer.db.logic;

import android.database.Cursor;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.MusicItem;
import com.keepingrack.tagmusicplayer.db.DBAdapter;
import com.keepingrack.tagmusicplayer.db.entity.MusicTagsRecord;
import com.keepingrack.tagmusicplayer.db.helper.MusicTagsHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;
import static com.keepingrack.tagmusicplayer.MainActivity.tagKinds;
import static com.keepingrack.tagmusicplayer.util.Utility.*;
import static com.keepingrack.tagmusicplayer.db.helper.MusicTagsHelper.SEPARATE;

public class MusicTagsLogic {

    private MainActivity activity;
    private DBAdapter dbAdapter;
    private MusicTagsHelper musicTagsHelper;

    public MusicTagsLogic(MainActivity _activity) {
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
            MusicTagsRecord record = new MusicTagsRecord(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            records.add(record);
            cursor.moveToNext();
        }
        return records;
    }

    public List<MusicTagsRecord> selectMusicAndTags() {
        before();

        List<MusicTagsRecord> records = cursorToRecords(musicTagsHelper.getAllRecords());
        for (MusicTagsRecord record : records) {
            String key = record.getKey();
            String filePath = record.getFilePath();
            File file = new File(filePath);
            String tags = record.getTags();
            List<String> tagArray = stringToList(tags, SEPARATE);
            tagKinds.addAll(tagArray);
            musicItems.put(key, new MusicItem(file.getAbsolutePath(), file.getName(), tagArray, null));
        }

        after();
        return records;
    }

    public void deleteAll() {
        before();
        musicTagsHelper.deleteAllRecords();
        after();
    }

    public void insertAll() {
        before();
        for (Map.Entry<String, MusicItem> musicItemMap : musicItems.entrySet()) {
            String key = musicItemMap.getKey();
            String filePath = musicItemMap.getValue().getAbsolutePath();
            String tags = listToString(musicItemMap.getValue().getTags(), SEPARATE);
            musicTagsHelper.insertRecord(key, filePath, tags);
        }
        after();
    }

    public void update(String key) {
        before();
        MusicItem musicItem = musicItems.get(key);
        String filePath = musicItem.getAbsolutePath();
        String tags = listToString(musicItem.getTags(), SEPARATE);
        musicTagsHelper.updateByKey(key, filePath, tags);
        after();
    }
}
