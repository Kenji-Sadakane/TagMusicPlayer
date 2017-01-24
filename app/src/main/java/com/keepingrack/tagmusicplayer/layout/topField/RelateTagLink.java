package com.keepingrack.tagmusicplayer.layout.topField;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.R;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class RelateTagLink extends TextView {

    public static boolean isRelateTagShow = false;

    public RelateTagLink(Context context, AttributeSet attr) {
        super(context, attr);

        // リスナー
        this.setOnClickListener(getOnClickListener());
    }

    private View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 関連タグ表示切り替え時
                RelateTagLink link = (RelateTagLink) v;
                if (isRelateTagShow) {
                    activity.relateTagField.hideRelateTagField();
                } else {
                    activity.relateTagField.showRelateTagField();
                }
            }
        };
    }

    public void changeTextToClose() {
        this.setText(R.string.hide_relate_tag);
        isRelateTagShow = true;
    }

    public void changeTextToOpen() {
        this.setText(R.string.show_relate_tag);
        isRelateTagShow = false;
    }
}
