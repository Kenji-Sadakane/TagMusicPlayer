package com.keepingrack.tagmusicplayer.layout.musicField;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.keepingrack.tagmusicplayer.R;

import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;
import static com.keepingrack.tagmusicplayer.MainActivity.musicKeys;

public class MusicScrollView extends ScrollView {
    public MusicScrollView(Context context, AttributeSet attr) {
        super(context, attr);

        this.setOnScrollChangeListener();
        this.setOnTouchListener(getOnTouchListener());
    }

    // スクロール時処理
    public void setOnScrollChangeListener() {
        this.getViewTreeObserver().addOnScrollChangedListener(getOnScrollChangeListener());
    }
    private OnScrollChangedListener getOnScrollChangeListener() {
        return new OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = ((MusicScrollView) findViewById(R.id.scrollView)).getScrollY();
                for (String key : musicKeys) {
                    LinearLayout musicRow = musicItems.get(key).getRow();
                    if (musicRow.getVisibility() != View.GONE) {
                        int rowY = (int) musicRow.getY();
                        if (scrollY - 2000 < rowY && rowY < scrollY + 2000) {
                            musicRow.setVisibility(View.VISIBLE);
                        } else {
                            musicRow.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        };
    }

    // スクロールビュータッチ時処理
    private View.OnTouchListener getOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    activity.musicField.hideKeyBoard();
                    activity.musicSeekBar.invisible();
                } catch (Exception ex) {
                    activity.musicField.msgField.outErrorMessage(ex);
                }
                // イベントの伝搬を阻止しない
                return false;
            }
        };
    }

    // 再生中の楽曲にスクロールする
    public void scrollMusicView() {
        int y = (int) musicItems.get(PLAYING_MUSIC).getRow().getY();
        scrollMusicView(y);
    }

    public void scrollMusicView(int y) {
        this.smoothScrollTo(0, y);
    }
}
