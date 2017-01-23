package com.keepingrack.tagmusicplayer.layout.topField;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CompoundButton;
import android.widget.Switch;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class SearchSwitch extends Switch {

    public static TYPE SEARCH_TYPE = TYPE.TAG;
    public enum TYPE {TITLE, TAG};

    public SearchSwitch(Context context, AttributeSet attr) {
        super(context, attr);

        // リスナー
        this.setOnCheckedChangeListener(getOnCheckedChangeListener());
    }

    public OnCheckedChangeListener getOnCheckedChangeListener() {
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SEARCH_TYPE = TYPE.TAG;
                } else {
                    SEARCH_TYPE = TYPE.TITLE;
                }
                activity.keyWord.execSearch();
            }
        };
    }
}
