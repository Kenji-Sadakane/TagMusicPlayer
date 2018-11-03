package com.keepingrack.tagmusicplayer.layout.bottomField;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.keepingrack.tagmusicplayer.Variable;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

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
                    activity.grayPanel.screenLock();
                    switch (activity.musicPlayer.getActionOnNextClicked()) {
                        case STOP:
                            activity.stopMusic();
                            break;
                        case GO_NEXT:
                            Integer nextTrackNo = activity.musicPlayer.getNextTrackNo();
                            if (nextTrackNo == null) nextTrackNo = 0;
                            activity.playMusic(Variable.getDisplayMusicNames().get(nextTrackNo));
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
