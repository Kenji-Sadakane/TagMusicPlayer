package com.keepingrack.tagmusicplayer.db;

import java.util.Date;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.keepingrack.tagmusicplayer.db.helper.MusicTagsHelper.COL_FILE_PATH;
import static com.keepingrack.tagmusicplayer.db.helper.MusicTagsHelper.COL_KEY;
import static com.keepingrack.tagmusicplayer.db.helper.MusicTagsHelper.COL_TAGS;
import static com.keepingrack.tagmusicplayer.db.helper.MusicTagsHelper.COL_UPDATE_TIME;
import static com.keepingrack.tagmusicplayer.db.helper.MusicTagsHelper.TABLE_NAME;


public class DBAdapter {
    static final String DATABASE_NAME = "tagMusicPlayer.db";
    static final int DATABASE_VERSION = 1;
    protected final Context context;
    protected DatabaseHelper dbHelper;
    public SQLiteDatabase db;

    public DBAdapter(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    //
    // SQLiteOpenHelper
    //
    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL( "CREATE TABLE " + TABLE_NAME + " ("
                    + COL_KEY + " TEXT NOT NULL,"
                    + COL_FILE_PATH + " TEXT NOT NULL,"
                    + COL_TAGS + " TEXT NOT NULL,"
                    + COL_UPDATE_TIME + " TEXT NOT NULL);");
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    //
    // Adapter Methods
    //
    public DBAdapter open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        dbHelper.close();
    }

    //
    // App Methods
    //
    public boolean deleteAllNotes(){
        return db.delete(TABLE_NAME, null, null) > 0;
    }
    public boolean deleteNote(String key){
        return db.delete(TABLE_NAME, COL_KEY + "=" + key, null) > 0;
    }
    public Cursor getAllNotes(){
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }
    public void saveNote(String key, String filePath, String tags) {
        Date dateNow = new Date ();
        ContentValues values = new ContentValues();
        values.put(COL_KEY, key);
        values.put(COL_FILE_PATH, filePath);
        values.put(COL_TAGS, tags);
        values.put(COL_UPDATE_TIME, dateNow.toLocaleString());
        db.insertOrThrow(TABLE_NAME, null, values);
    }
}
