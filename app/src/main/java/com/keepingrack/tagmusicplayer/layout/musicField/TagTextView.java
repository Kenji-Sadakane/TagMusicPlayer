package com.keepingrack.tagmusicplayer.layout.musicField;

import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.layout.musicField.TagFieldLayout.TAG_LOCATION;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class TagTextView extends TextView {

    // 親レイアウト
    private TagFieldLayout tagFieldLayout;

    // 親レイアウトへの配置場所
    private TAG_LOCATION location;

    public TagTextView(TagFieldLayout layout, String tag) {
        super(activity);

        // 親レイアウト
        tagFieldLayout = layout;
        // コンテンツ
        this.setId(View.generateViewId());
        this.setText(tag);
        // レイアウト
        this.setPadding(10, 0, 10, 0); // 左, 上, 右, 下
        this.setBackgroundResource(R.drawable.tag_item);
        this.setClickable(true);
        this.setLayoutParams(createLayoutParams());
        // リスナー
        this.setOnClickListener(getOnClickListener());
        // 親レイアウト情報更新
        updateRelateTagLayout();
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

    public RelativeLayout.LayoutParams createLayoutParams() {
        RelativeLayout.LayoutParams tagTextParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tagTextParams.setMargins(10, 0, 0, 15); // 左, 上, 右, 下
        location = tagFieldLayout.judgeTagTextLocation(getDesiredWidth());
        switch (location) {
            case UPPER_LEFT:
                break;
            case RIGHT_OF:
                tagTextParams.addRule(RelativeLayout.ALIGN_TOP, tagFieldLayout.getLastTagId());
                tagTextParams.addRule(RelativeLayout.RIGHT_OF, tagFieldLayout.getLastTagId());
                break;
            case NEXT_LINE:
                tagTextParams.addRule(RelativeLayout.BELOW, tagFieldLayout.getLastTagId());
                break;
            default:
                break;
        }
        return tagTextParams;
    }

    // 親レイアウトの情報更新
    private void updateRelateTagLayout() {
        tagFieldLayout.setLastTagId(getId());
        if (TAG_LOCATION.NEXT_LINE.equals(location)) {
            tagFieldLayout.setTagLengthByLine(getDesiredWidth());
        } else {
            tagFieldLayout.updateTagLengthByLine(getDesiredWidth());
        }
    }

    public float getDesiredWidth() {
        return Layout.getDesiredWidth(getText(), getPaint()) + 30; // 30=padding(左)+padding(右)+margin(左)+margin(右)
    }
}
