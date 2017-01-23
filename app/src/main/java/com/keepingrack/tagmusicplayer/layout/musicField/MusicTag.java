package com.keepingrack.tagmusicplayer.layout.musicField;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.keepingrack.tagmusicplayer.MainActivity;

public class MusicTag {

    private MainActivity activity;

    public MusicTag(MainActivity _activity) {
        this.activity = _activity;
    }

    private void addTagView(final LinearLayout row, final RelativeLayout tagFieldLayout, final LinearLayout.LayoutParams tagFieldParams) {
        row.addView(tagFieldLayout, tagFieldParams);
    }
}
