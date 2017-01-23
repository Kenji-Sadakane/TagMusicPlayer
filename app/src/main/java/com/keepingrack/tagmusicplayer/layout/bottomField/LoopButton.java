package com.keepingrack.tagmusicplayer.layout.bottomField;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.R;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class LoopButton extends Button {

    /**
     * ループボタンの状態
     * OFF:ループしない
     * ON:ループする
     * ONE:同一曲のみをループする
     */
    public enum LOOP_STATUS {OFF, ON, ONE}

    public LOOP_STATUS currentLoopStatus = LOOP_STATUS.ON;

    public LoopButton(Context context, AttributeSet attr) {
        super(context, attr);

        // リスナー
        this.setOnClickListener(getOnClickListener());
    }

    private OnClickListener getOnClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                LoopButton loopButton = (LoopButton) v;
                switch (currentLoopStatus) {
                    case ON:
                        currentLoopStatus = LOOP_STATUS.ONE;
                        loopButton.setBackgroundResource(R.drawable.av_loop_1);
                        break;
                    case ONE:
                        currentLoopStatus = LOOP_STATUS.OFF;
                        loopButton.setBackgroundResource(R.drawable.av_loop_off);
                        break;
                    default:
                        currentLoopStatus = LOOP_STATUS.ON;
                        loopButton.setBackgroundResource(R.drawable.av_loop_on);
                }
            }
        };
    }

    // 再生ボタンのチェック状態を変える
    public void playButtonCheck(boolean state) {
        ToggleButton playButton = (ToggleButton) activity.findViewById(R.id.playButton);
        playButton.setChecked(state);
    }

    public boolean isLoopStatusOFF() {
        boolean result = false;
        Button loopButton = (Button) activity.findViewById(R.id.loopButton);
        switch (currentLoopStatus) {
            case OFF:
                result = true;
                break;
        }
        return result;
    }

    public boolean isLoopStatusONE() {
        boolean result = false;
        Button loopButton = (Button) activity.findViewById(R.id.loopButton);
        switch (currentLoopStatus) {
            case ONE:
                result = true;
                break;
        }
        return result;
    }

}
