package com.keepingrack.tagmusicplayer.layout.musicField;

import android.support.v7.widget.AppCompatTextView;
import android.text.Layout;
import android.view.View;
import android.widget.RelativeLayout.LayoutParams;

import com.keepingrack.tagmusicplayer.R;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class TagTextView extends AppCompatTextView {

    public TagTextView(String tag) {
        super(activity);
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
    }

    private View.OnClickListener getOnClickListener() {
        final String tag = this.getText().toString();
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.searchSwitch.setChecked(true);
                activity.keyWordEditText.setText(tag);
                activity.searchButton.execSearch();
            }
        };
    }

    public LayoutParams createLayoutParams() {
        LayoutParams tagTextParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tagTextParams.setMargins(10, 0, 0, 15); // 左, 上, 右, 下
        return tagTextParams;
    }

    public float getDesiredWidth() {
        return Layout.getDesiredWidth(getText(), getPaint()) + 30; // 30=padding(左)+padding(右)+margin(左)+margin(右)
    }

    public void setLocation() {

    }
}
