package com.keepingrack.tagmusicplayer.layout.musicField;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.keepingrack.tagmusicplayer.Variable;

import lombok.Getter;
import lombok.Setter;

import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.activity;

@Getter
@Setter
public class MusicScrollView extends ScrollView {

    private boolean execScrollChangeEvent = true;
    private int scrollValueOnEvent = 0;

    public MusicScrollView(Context context, AttributeSet attr) {
        super(context, attr);

        this.setOnTouchListener(getOnTouchListener());
        this.setOnScrollChangeListener(getOnScrollChanged());
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

    protected ScrollView.OnScrollChangeListener getOnScrollChanged() {
        return new ScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View view, int x, int y, int oldX, int oldY) {
                if (execScrollChangeEvent) {
                    if (Math.abs(y - scrollValueOnEvent) > 3000) {
                        setScrollValueOnEvent(y);
                        showNearMusicRow();
                    }
                }
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
    public void hideFarMusicRow() {
        int scrollY = this.getScrollY();
        int target = scrollY / 150;
        int minTarget = (target - 10 > 0) ? (target - 10) : 0;
        int maxTarget = minTarget + 20;
        for (int i = 0; i < Variable.getDisplayMusicNames().size(); i++) {
            String key = Variable.getDisplayMusicNames().get(i);
            MusicRow row = Variable.getMusicRow(key);
            if (i < minTarget || maxTarget < i) {
                row.changeMusicVisibility(INVISIBLE);
            } else {
                continue;
            }
        }
    }

    // 現スクロール位置より一定距離以内の楽曲を表示
    public void showNearMusicRow() {
        int scrollY = this.getScrollY();
        int target = scrollY / 150;
        int minTarget = (target - 30 > 0) ? (target - 30) : 0;
        int maxTarget = target + 60;
        for (int i = 0; i < Variable.getDisplayMusicNames().size(); i++) {
            String key = Variable.getDisplayMusicNames().get(i);
            MusicRow row = Variable.getMusicRow(key);
            if (i < minTarget || maxTarget < i) {
                continue;
            } else {
                row.changeMusicVisibility(VISIBLE);
            }
        }
    }

    // 全楽曲を非表示
    public void hideMusicRow() {
        for (String key : Variable.getMusicKeys()) {
            MusicRow musicRow = Variable.getMusicRow(key);
            if (musicRow.getVisibility() != View.GONE) {
                musicRow.changeMusicVisibility(INVISIBLE);
            }
        }
    }

    // 上から指定楽曲数を表示
    public void showMusicRow(int showCount) {
        for (int i = 0; i < Variable.getDisplayMusicNames().size(); i++) {
            String key = Variable.getDisplayMusicNames().get(i);
            if (i < showCount) {
                Variable.getMusicRow(key).changeMusicVisibility(VISIBLE);
            } else {
                break;
            }
        }
    }

    // 全楽曲を表示
    public void showMusicRow() {
        for (String key : Variable.getDisplayMusicNames()) {
            Variable.getMusicRow(key).changeMusicVisibility(VISIBLE);
        }
    }
}
