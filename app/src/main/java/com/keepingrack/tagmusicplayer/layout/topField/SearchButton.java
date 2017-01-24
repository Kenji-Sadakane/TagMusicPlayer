package com.keepingrack.tagmusicplayer.layout.topField;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class SearchButton extends Button {
    public SearchButton(Context context, AttributeSet attr) {
        super(context, attr);

        // リスナー
        this.setOnClickListener(getOnClickListener());
    }

    private View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.keyWordEditText.execSearch();
            }
        };
    }
}
