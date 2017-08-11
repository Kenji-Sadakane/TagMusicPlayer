package com.keepingrack.tagmusicplayer.layout.musicField;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver.OnScrollChangedListener;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.keepingrack.tagmusicplayer.Variable;

import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class MusicScrollView extends ScrollView {
    public MusicScrollView(Context context, AttributeSet attr) {
        super(context, attr);

        this.setOnTouchListener(getOnTouchListener());
    }

    // スクロールビュータッチ時処理
    private View.OnTouchListener getOnTouchListener() {
        return new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    activity.hideKeyBoard();
                    activity.musicSeekBar.invisible();
                } catch (Exception ex) {
                    activity.msgView.outErrorMessage(ex);
                }
                // イベントの伝搬を阻止しない
                return false;
            }
        };
    }

    // 再生中の楽曲にスクロールする
    public void scrollMusicView() {
        int y = 0;
        if (Variable.getMusicRow(PLAYING_MUSIC) != null) {
            y = (int) Variable.getMusicRow(PLAYING_MUSIC).getY();
        }
        scrollMusicView(y);
    }

    public void scrollMusicView(int y) {
        this.smoothScrollTo(0, y);
    }

    // 現スクロール位置より一定距離以上の楽曲を非表示
    public void hideTooUnderMusicRow() {
        int scrollY = this.getScrollY();
        for (String key : Variable.getMusicKeys()) {
            RelativeLayout musicRow = Variable.getMusicRow(key);
            if (musicRow.getVisibility() != View.GONE) {
                int rowY = (int) musicRow.getY();
                if (scrollY - 3000 < rowY && rowY < scrollY + 3000) {
                    musicRow.setVisibility(VISIBLE);
                } else {
                    musicRow.setVisibility(INVISIBLE);
                }
            }
        }
    }

    // 全楽曲を非表示
    public void hideMusicRow() {
        for (String key : Variable.getMusicKeys()) {
            RelativeLayout musicRow = Variable.getMusicRow(key);
            if (musicRow.getVisibility() != View.GONE) {
                musicRow.setVisibility(INVISIBLE);
            }
        }
    }

    // 上から指定楽曲数を表示
    public void showMusicRow(int showCount) {
        for (int i = 0; i < Variable.getDisplayMusicNames().size(); i++) {
            String key = Variable.getDisplayMusicNames().get(i);
            if (i < showCount) {
                Variable.getMusicRow(key).setVisibility(VISIBLE);
            } else {
                Variable.getMusicRow(key).setVisibility(INVISIBLE);
            }
        }
    }

    // 全楽曲を表示
    public void showMusicRow() {
        for (String key : Variable.getMusicKeys()) {
            RelativeLayout musicRow = Variable.getMusicRow(key);
            if (musicRow.getVisibility() != View.GONE) {
                musicRow.setVisibility(VISIBLE);
            }
        }
    }
}
