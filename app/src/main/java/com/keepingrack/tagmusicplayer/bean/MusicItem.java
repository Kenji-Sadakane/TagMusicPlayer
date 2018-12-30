package com.keepingrack.tagmusicplayer.bean;

import android.widget.LinearLayout;

import com.keepingrack.tagmusicplayer.layout.musicField.MusicRow;

import java.util.List;

public class MusicItem {

    private String absolutePath;
    private String title;
    private List<String> tags;
    private MusicRow row;

    public String getAbsolutePath() { return absolutePath; }
    public void setAbsolutePath(String absolutePath) { this.absolutePath = absolutePath; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public MusicRow getRow() { return row; }
    public void setRow(MusicRow row) { this.row = row; }

    public MusicItem(String absolutePath, String title, List<String> tags, MusicRow row) {
        this.absolutePath = absolutePath;
        this.title = title;
        this.tags = tags;
        this.row = row;
    }
}
