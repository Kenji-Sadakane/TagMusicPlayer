package com.keepingrack.tagmusicplayer.layout;

import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.layout.musicField.MsgField;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicTag;

public class MusicField {

    public MsgField msgField;
    public MusicTag musicTag;
    private MainActivity activity;

    public MusicField(MainActivity _activity) {
        this.activity = _activity;
        msgField = new MsgField(activity);
        musicTag = new MusicTag(activity);
    }

    // リスナー
    public void setListener() {
        setOnPanelTouchListener();
    }

    // グレイパネルタッチ時処理
    private void setOnPanelTouchListener() {
        ((View) activity.findViewById(R.id.grayPanel)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // イベントの伝搬を阻止
                return true;
            }
        });
    }

    public void hideKeyBoard() {
        ((TextView) activity.findViewById(R.id.dummyText)).requestFocus();
    }

    public void screenLock(final int millsecond) {
        ((View) activity.findViewById(R.id.grayPanel)).setVisibility(View.VISIBLE);
        activity.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((View) activity.findViewById(R.id.grayPanel)).setVisibility(View.GONE);
            }
        }, millsecond);
    }


}
