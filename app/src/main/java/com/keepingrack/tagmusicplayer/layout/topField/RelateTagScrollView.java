package com.keepingrack.tagmusicplayer.layout.topField;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class RelateTagScrollView extends ScrollView {

    public RelateTagScrollView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void setRelateTagFieldHeight() {
        setRelateTagFieldHeight(activity.relateTagLayout.calcRelateTagFieldHeight());
    }
    public void setRelateTagFieldHeight(int height) {
        this.getLayoutParams().height = height;
    }
}
