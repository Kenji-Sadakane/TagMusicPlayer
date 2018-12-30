package com.keepingrack.tagmusicplayer.external.db.entity;

public class MusicTagsRecord {
    private String key;
    private String filePath;
    private String tags;

    public String getKey() { return key; }
    public void setKey(String key) { this.key = key; }
    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public MusicTagsRecord(String key, String filePath, String tags) {
        this.key = key;
        this.filePath = filePath;
        this.tags = tags;
    }
}
