package com.example.sada.tagmusicplayer;

import android.widget.Button;
import android.widget.ToggleButton;

public class MusicPlayerButton {

    /**
     * ループボタンの状態
     * OFF:ループしない
     * ON:ループする
     * ONE:同一曲のみをループする
     */
    public enum LOOP_STATUS {OFF, ON, ONE}

    public LOOP_STATUS currentLoopStatus = LOOP_STATUS.ON;

    private MainActivity activity;

    public MusicPlayerButton(MainActivity _activity) {
        this.activity = _activity;
    }

    // 再生ボタンのチェック状態を変える
    public void playButtonCheck(boolean state) {
        ToggleButton playButton = (ToggleButton) activity.findViewById(R.id.playButton);
        playButton.setChecked(state);
    }

    public void clickLoopButton() {
        Button loopButton = (Button) activity.findViewById(R.id.loopButton);
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
