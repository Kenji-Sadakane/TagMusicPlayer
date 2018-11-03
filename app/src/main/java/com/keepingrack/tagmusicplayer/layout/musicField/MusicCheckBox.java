package com.keepingrack.tagmusicplayer.layout.musicField;

import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout.LayoutParams;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class MusicCheckBox extends CheckBox {

    private MusicRow row;

    public MusicCheckBox(MusicRow row) {
        super(activity);
        this.row = row;

        this.setId(View.generateViewId());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(params);
        this.setOnClickListener(getOnCheckBoxClickListener());
    }

    private OnClickListener getOnCheckBoxClickListener() {
        return new OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v;
                if (checkBox.isChecked()) {
                    activity.musicLinearLayout.addCheckedMusicKeyList(row.getMusicKey());
                } else {
                    activity.musicLinearLayout.removeCheckedMusicKeyList(row.getMusicKey());
                }
            }
        };
    }

    public void checkManually(boolean checked) {
        this.setChecked(checked);
        if (checked) {
            activity.musicLinearLayout.addCheckedMusicKeyList(row.getMusicKey());
        } else {
            activity.musicLinearLayout.removeCheckedMusicKeyList(row.getMusicKey());
        }
    }
}
