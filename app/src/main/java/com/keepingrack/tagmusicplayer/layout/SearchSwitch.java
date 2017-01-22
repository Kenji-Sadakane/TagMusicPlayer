package com.keepingrack.tagmusicplayer.layout;

import android.widget.CompoundButton;
import android.widget.Switch;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.R;

public class SearchSwitch {

    public static TYPE SEARCH_TYPE = TYPE.TAG;
    public enum TYPE {TITLE, TAG};

    private MainActivity activity;

    public SearchSwitch(MainActivity _activity) {
        this.activity = _activity;
    }

    public void setOnCheckedChangeListener() {
        ((Switch) activity.findViewById(R.id.switch1)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SEARCH_TYPE = TYPE.TAG;
                } else {
                    SEARCH_TYPE = TYPE.TITLE;
                }
                activity.keyWord.execSearch();
            }
        });
    }
}
