package com.keepingrack.tagmusicplayer.db.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MusicTagsRecord {
    private String key;
    private String filePath;
    private String tags;
    private String updateTime;
}
