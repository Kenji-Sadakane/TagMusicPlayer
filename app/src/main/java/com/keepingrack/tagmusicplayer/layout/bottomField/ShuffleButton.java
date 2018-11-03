package com.keepingrack.tagmusicplayer.layout.bottomField;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ToggleButton;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class ShuffleButton extends ToggleButton {
    public ShuffleButton(Context context, AttributeSet attr) {
        super(context, attr);

        // リスナー
        this.setOnClickListener(getOnClickListener());
    }

    private OnClickListener getOnClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.grayPanel.screenLock();
                activity.shuffleMusicList.exec();
                activity.grayPanel.screenLockRelease();
            }
        };
    }
}
