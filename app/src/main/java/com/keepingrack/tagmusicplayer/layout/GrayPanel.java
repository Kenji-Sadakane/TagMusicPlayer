package com.keepingrack.tagmusicplayer.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

/**
 * ボタン2度押し対策として
 * 処理中に全画面表示されるView
 */
public class GrayPanel extends View {
    public GrayPanel(Context context, AttributeSet attr) {
        super(context, attr);

        this.setOnTouchListener(getOnPanelTouchListener());
    }

    private View.OnTouchListener getOnPanelTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // イベントの伝搬を阻止
                return true;
            }
        };
    }

    public void screenLock(final int millisecond) {
        this.setVisibility(View.VISIBLE);
        final GrayPanel grayPanel = this;
        activity.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                grayPanel.setVisibility(View.GONE);
            }
        }, millisecond);
    }
}
