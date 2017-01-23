package com.keepingrack.tagmusicplayer.layout.bottomField;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.MainActivity.displayMusicNames;
import static com.keepingrack.tagmusicplayer.MainActivity.mp;

public class PrevButton extends Button {
    public PrevButton(Context context, AttributeSet attr) {
        super(context, attr);

        // リスナー
        this.setOnClickListener(getOnClickListener());
    }

    private OnClickListener getOnClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    switch (activity.musicPlayer.getActionOnPrevClicked()) {
                        case RESTART:
                            mp.seekTo(0);
                            activity.musicScrollView.scrollMusicView();
                            break;
                        case STOP:
                            activity.stopMusic();
                            break;
                        case GO_PREV:
                            Integer prevTrackNo = activity.musicPlayer.getPrevTrackNo();
                            if (prevTrackNo == null) prevTrackNo = 0;
                            activity.stopMusic();
                            activity.playMusic(displayMusicNames.get(prevTrackNo));
                            break;
                    }
                } catch (Exception ex) {
                    activity.msgView.outErrorMessage(ex);
                }
            }
        };
    }
}
