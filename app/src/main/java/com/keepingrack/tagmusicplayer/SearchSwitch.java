package com.keepingrack.tagmusicplayer;

import android.widget.CompoundButton;
import android.widget.Switch;

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
