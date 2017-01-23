package com.keepingrack.tagmusicplayer.layout.musicField;

import android.text.Layout;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.R;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class TagTextView extends TextView {

    /**
     * タグTextViewの配置場所
     * UPPER_LEFT:左上(1個目のタグ)
     * RIGHT_OF:前のタグの右
     * NEXT_LINE:次行
     */
    public enum TAG_LOCATION {UPPER_LEFT, RIGHT_OF, NEXT_LINE}

    public TagTextView(String tag) {
        super(activity);

        // レイアウト
        this.setPadding(10, 0, 10, 0); // 左, 上, 右, 下
        this.setBackgroundResource(R.drawable.tag_item);
        this.setClickable(true);
        // コンテンツ
        this.setId(View.generateViewId());
        this.setText(tag);
        // リスナー
        this.setOnClickListener(getOnClickListener());
    }

    private View.OnClickListener getOnClickListener() {
        final String tag = this.getText().toString();
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.searchSwitch.setChecked(true);
                activity.keyWordEditText.setText(tag);
                activity.keyWordEditText.execSearch();
            }
        };
    }

    public void setLayoutParams(TAG_LOCATION location, int prevTagId) {
        RelativeLayout.LayoutParams tagTextParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tagTextParams.setMargins(10, 0, 0, 15); // 左, 上, 右, 下
        switch (location) {
            case UPPER_LEFT:
                break;
            case RIGHT_OF:
                tagTextParams.addRule(RelativeLayout.ALIGN_TOP, prevTagId);
                tagTextParams.addRule(RelativeLayout.RIGHT_OF, prevTagId);
                break;
            case NEXT_LINE:
                tagTextParams.addRule(RelativeLayout.BELOW, prevTagId);
                break;
            default:
                break;
        }
        this.setLayoutParams(tagTextParams);
    }

    public float getDesiredWidth() {
        return Layout.getDesiredWidth(getText(), getPaint()) + 30; // 30=padding(左)+padding(右)+margin(左)+margin(右)
    }
}
