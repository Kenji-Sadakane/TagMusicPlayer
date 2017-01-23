package com.keepingrack.tagmusicplayer.layout;

import android.widget.TextView;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicTag;

public class MusicField {

    public MusicTag musicTag;
    private MainActivity activity;

    public MusicField(MainActivity _activity) {
        this.activity = _activity;
        musicTag = new MusicTag(activity);
    }

    public void hideKeyBoard() {
        ((TextView) activity.findViewById(R.id.dummyText)).requestFocus();
    }

}
