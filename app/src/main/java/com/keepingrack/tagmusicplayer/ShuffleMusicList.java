package com.keepingrack.tagmusicplayer;

import android.widget.ToggleButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.keepingrack.tagmusicplayer.MainActivity.displayMusicNames;
import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;

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
        Collections.shuffle(displayMusicNames);
        if (displayMusicNames.contains(PLAYING_MUSIC)) {
            displayMusicNames.remove(PLAYING_MUSIC);
            displayMusicNames.add(0, PLAYING_MUSIC);
        }
    }

    public void musicOrderDefault() {
        List<String> tmpList = new ArrayList<>();
        for (Map.Entry<String, MusicItem> musicItemMap : musicItems.entrySet()) {
            if (displayMusicNames.contains(musicItemMap.getKey())) {
                tmpList.add(musicItemMap.getKey());
            }
        }
        displayMusicNames = tmpList;
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
