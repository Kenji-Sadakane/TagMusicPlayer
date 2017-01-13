package com.example.sada.tagmusicplayer;

import android.widget.LinearLayout;

import java.io.File;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class MusicItem {

    private String title;

    private File file;

    private List<String> tags;

    private LinearLayout row;
}
