package com.keepingrack.tagmusicplayer.db.logic;

import android.database.Cursor;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.Variable;
import com.keepingrack.tagmusicplayer.bean.MusicItem;
import com.keepingrack.tagmusicplayer.db.DBAdapter;
import com.keepingrack.tagmusicplayer.db.entity.MusicTagsRecord;
import com.keepingrack.tagmusicplayer.db.helper.MusicTagsHelper;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicRow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.keepingrack.tagmusicplayer.util.ListUtil.*;
import static com.keepingrack.tagmusicplayer.util.StringUtil.*;
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

    private Map<String, MusicTagsRecord> cursorToRecordMap(Cursor cursor) {
        Map<String, MusicTagsRecord> recordMap = new HashMap<>();
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            MusicTagsRecord record = new MusicTagsRecord(cursor.getString(0), cursor.getString(1), cursor.getString(2));
            recordMap.put(cursor.getString(0), record);
            cursor.moveToNext();
        }
        return recordMap;
    }

    public Map<String, MusicTagsRecord> selectAllRecords() {
        before();
        Map<String, MusicTagsRecord> recordMap = cursorToRecordMap(musicTagsHelper.getAllRecords());
        after();
        return recordMap;
    }

    public void selectAndReflectTags() {
        before();

        List<MusicTagsRecord> records = cursorToRecords(musicTagsHelper.getAllRecords());
        Variable.clearTagKinds();
        Variable.clearMusicKeys();
        Variable.clearMusicItems();
        for (MusicTagsRecord record : records) {
            String key = record.getKey();
            String filePath = record.getFilePath();
            File file = new File(filePath);
            String tags = record.getTags();
            List<String> tagArray = stringToList(tags, SEPARATE);
            Variable.addTagKinds(tagArray);
            Variable.addMusicKeys(key);
            Variable.putMusicItems(key, new MusicItem(file.getAbsolutePath(), file.getName(), tagArray, new MusicRow("")));
        }

        after();
    }

    public void deleteAll() {
        before();
        musicTagsHelper.deleteAllRecords();
        after();
    }

    public void insertAll() {
        before();
        for (String key : Variable.getMusicKeys()) {
            MusicItem musicItem = Variable.getMusicItem(key);
            String filePath = musicItem.getAbsolutePath();
            String tags = listToString(musicItem.getTags(), SEPARATE);
            musicTagsHelper.insertRecord(key, filePath, tags);
        }
        after();
    }

    public void update(String key) {
        before();
        MusicItem musicItem = Variable.getMusicItem(key);
        String filePath = musicItem.getAbsolutePath();
        String tags = listToString(musicItem.getTags(), SEPARATE);
        musicTagsHelper.updateByKey(key, filePath, tags);
        after();
    }
}
