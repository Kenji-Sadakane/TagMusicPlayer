package com.keepingrack.tagmusicplayer.external.db.helper;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MusicTagsHelper {

    public static final String TABLE_NAME = "musicTags";
    public static final String COL_KEY = "key";
    public static final String COL_FILE_PATH = "filePath";
    public static final String COL_TAGS = "tags";
    public static String SEPARATE = "\t";

    private SQLiteDatabase db;

    public MusicTagsHelper(SQLiteDatabase db) {
        this.db = db;
    }

    public boolean deleteAllRecords() {
        return db.delete(TABLE_NAME, null, null) > 0;
    }
//
//    public boolean deleteByKey(String key){
//        return db.delete(TABLE_NAME, COL_KEY + "=" + key, null) > 0;
//    }

    public Cursor getAllRecords() {
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }

    public void insertRecord(String key, String filePath, String tags) {
        ContentValues values = new ContentValues();
        values.put(COL_KEY, key);
        values.put(COL_FILE_PATH, filePath);
        values.put(COL_TAGS, tags);
        db.insertOrThrow(TABLE_NAME, null, values);
    }

    public void updateByKey(String key, String filePath, String tags) {
        ContentValues values = new ContentValues();
        values.put(COL_FILE_PATH, filePath);
        values.put(COL_TAGS, tags);
        String whereClause = COL_KEY + " = '" + key + "'";
        db.update(TABLE_NAME, values, whereClause, null);
    }
}
