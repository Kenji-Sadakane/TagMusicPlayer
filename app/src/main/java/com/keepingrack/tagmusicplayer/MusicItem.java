package com.keepingrack.tagmusicplayer;

import android.widget.LinearLayout;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MusicItem {

    private String absolutePath;

    private String title;

    private List<String> tags;

    private LinearLayout row;
}
