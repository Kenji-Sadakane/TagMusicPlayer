package com.keepingrack.tagmusicplayer.layout.bottomField;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.MainActivity.displayMusicNames;

public class NextButton extends Button {
    public NextButton(Context context, AttributeSet attr) {
        super(context, attr);

        // リスナー
        this.setOnClickListener(getOnClickListener());
    }

    private OnClickListener getOnClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 次ボタン押下時
                try {
                    switch (activity.musicPlayer.getActionOnNextClicked()) {
                        case STOP:
                            activity.stopMusic();
                            break;
                        case GO_NEXT:
                            Integer nextTrackNo = activity.musicPlayer.getNextTrackNo();
                            if (nextTrackNo == null) nextTrackNo = 0;
                            activity.playMusic(displayMusicNames.get(nextTrackNo));
                    }
                } catch (Exception ex) {
                    activity.msgView.outErrorMessage(ex);
                }
            }
        };
    }
}
