package com.keepingrack.tagmusicplayer.layout;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.R;

import static com.keepingrack.tagmusicplayer.MainActivity.mp;
import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;

public class MusicSeekBar {

    public static final String DEFAULT_TIME = "00:00";
    public static String PLAY_TIME_TEXT = "00:00";
    public static int PLAY_TIME;
    public boolean visible = true;
    public int hiddenTime = 0;

    private MainActivity activity;

    public MusicSeekBar(MainActivity _activity) {
        this.activity = _activity;
//        setListener();
    }

    // リスナー
//    private void setListener() {
//        setOnSeekBarChangeListener();
//    }

    // シークバー変更時処理
    public void setOnSeekBarChangeListener() {
        ((SeekBar) activity.findViewById(R.id.seekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // ユーザが手動でシークした時のみ
                if (fromUser) {
                    if (!PLAYING_MUSIC.isEmpty()) {
                        mp.seekTo(progress);
                        seekBar.setProgress(progress);
                        setPlayTime(progress);
                    } else {
                        seekBar.setProgress(0);
                        setPlayTime(0);
                    }
                }
            }
        });
    }

    public String convertDisplayTimeText(int time) {
        String displayTime = DEFAULT_TIME;
        try {
            displayTime = String.format("%02d:%02d", (time / 1000) / 60, (time / 1000) % 60);
        } catch (Exception ex) {
            // 例外時はデフォルト
        }
        return displayTime;
    }

    public void setPlayTime() {
        setPlayTime(mp.getCurrentPosition());
    }

    public void setPlayTime(int time) {
        final SeekBar seekBar = (SeekBar) activity.findViewById(R.id.seekBar);
        final TextView playTimeText = (TextView) activity.findViewById(R.id.playTime);
        PLAY_TIME = time;
        PLAY_TIME_TEXT = convertDisplayTimeText(time);
        String fromPlayTimeText = playTimeText.getText().toString();
        if (!fromPlayTimeText.equals(PLAY_TIME_TEXT)) {
            playTimeText.post(new Runnable() {
                @Override
                public void run() {
                    playTimeText.setText(PLAY_TIME_TEXT);
                }
            });
            seekBar.post(new Runnable() {
                @Override
                public void run() {
                    seekBar.setProgress(PLAY_TIME);
                }
            });
        }
    }

    public void setTotalTime(int time) {
        SeekBar seekBar = (SeekBar) activity.findViewById(R.id.seekBar);
        TextView totalTimeText = (TextView) activity.findViewById(R.id.totalTime);
        if (time != 0) {
            totalTimeText.setText(convertDisplayTimeText(time));
            seekBar.setMax(time);
        } else {
            totalTimeText.setText(DEFAULT_TIME);
            seekBar.setMax(0);
        }
    }

    public void visible() {
        visible(600);
    }

    public void visible(final int animationTime) {
        if (!visible) {
            visible = true;
            final LinearLayout bottomField = (LinearLayout) activity.findViewById(R.id.bottomField);
            bottomField.post(new Runnable() {
                @Override
                public void run() {
                    bottomField.setVisibility(View.VISIBLE);
                    ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(bottomField, "translationY", 400f, 0f);
                    objectAnimator.setDuration(animationTime);
                    objectAnimator.start();
                }
            });
        }
        hiddenTime = 0;
    }

    public void invisible() {
        invisible(400);
    }

    public void invisible(final int animationTime) {
        if (visible) {
            visible = false;
            final LinearLayout bottomField = (LinearLayout) activity.findViewById(R.id.bottomField);
            bottomField.post(new Runnable() {
                 @Override
                 public void run() {
                     ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(bottomField, "translationY", 0f, 400f);
                     objectAnimator.setDuration(animationTime);
                     objectAnimator.addListener(new Animator.AnimatorListener() {
                         @Override
                         public void onAnimationStart(Animator animation) {}
                         @Override
                         public void onAnimationEnd(Animator animation) {
                             bottomField.setVisibility(View.GONE);
                         }
                         @Override
                         public void onAnimationCancel(Animator animation) {}
                         @Override
                         public void onAnimationRepeat(Animator animation) {}
                     });
                     objectAnimator.start();
                 }
             });
        }
        hiddenTime = 0;
    }
}
