package com.keepingrack.tagmusicplayer;

import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;

public class ShuffleMusicList {

    private MainActivity activity;

    public ShuffleMusicList(MainActivity _activity) {
        this.activity = _activity;
    }

    public void exec() {
        if (isShuffleOn()) {
            musicOrderShuffle();
        } else {
            musicOrderDefault();
        }
        activity.musicPlayer.showTrackNo();
    }

    public void musicOrderShuffle() {
        Variable.shuffleDisplayMusicNames();
        if (Variable.getDisplayMusicNames().contains(PLAYING_MUSIC)) {
            Variable.removeDisplayMusicNames(PLAYING_MUSIC);
            Variable.addDisplayMusicNames(0, PLAYING_MUSIC);
        }
    }

    public void musicOrderDefault() {
        List<String> tmpList = new ArrayList<>();
        for (String key : Variable.getMusicKeys()) {
            if (Variable.getDisplayMusicNames().contains(key)) {
                tmpList.add(key);
            }
        }
        Variable.setDisplayMusicNames(tmpList);
    }

    public boolean isShuffleOn() {
        ToggleButton shuffleButton = (ToggleButton) activity.findViewById(R.id.shuffleButton);
        if (shuffleButton.isChecked()) {
            return true;
        } else {
            return false;
        }
    }
}
