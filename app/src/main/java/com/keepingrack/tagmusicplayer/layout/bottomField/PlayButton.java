package com.keepingrack.tagmusicplayer.layout.bottomField;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ToggleButton;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.MainActivity.mp;

public class PlayButton extends ToggleButton {
    public PlayButton(Context context, AttributeSet attr) {
        super(context, attr);

        // リスナー
        this.setOnClickListener(getOnClickListener());
    }

    private OnClickListener getOnClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activity.grayPanel.screenLock();
                    switch (activity.musicPlayer.getActionOnPlayClicked()) {
                        case PAUSE:
                            mp.pause();
                            break;
                        case RESUME:
                            mp.start();
                            break;
                        case START:
                            activity.playMusic(activity.musicPlayer.getPlayMusicKeyOnPlayClicked());
                            activity.shuffleMusicList.exec();
                            break;
                        case NOTHING:
                            activity.stopMusic();
                            break;
                    }
                } catch (Exception ex) {
                    activity.msgView.outErrorMessage(ex);
                } finally {
                    activity.grayPanel.screenLockRelease();
                }
            }
        };
    }
}
