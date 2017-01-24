package com.keepingrack.tagmusicplayer.layout.topField;

import android.graphics.Color;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.bean.RelateTag;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class RelateTagTextView extends TextView {

    public RelateTagTextView(String tag) {
        super(activity);

        // レイアウト
        this.setClickable(true);
        this.setPadding(5, 0, 5, 0); // 左, 上, 右, 下
        this.setBackgroundResource(R.drawable.tag_item);
        this.setLayoutParams(createLayoutParams());
        // コンテンツ
        this.setText(tag);
        // リスナー
        this.setOnClickListener(getOnClickListener());
    }

    private LinearLayout.LayoutParams createLayoutParams() {
        LinearLayout.LayoutParams tagTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tagTextParams.setMargins(0, 0, 10, 0); // 左, 上, 右, 下
        return tagTextParams;
    }

    private View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                String tag = textView.getText().toString();
                RelateTag relateTag = activity.relateTagField.findRelateTag(tag);
                if (relateTag == null) {
                    return;
                }
                switch (relateTag.getState()) {
                    case DEFAULT:
                        relateTag.setState(RelateTag.STATE.SELECTED);
                        textView.setBackgroundResource(R.drawable.related_tag_selected);
                        textView.setTextColor(Color.parseColor("#FF4081"));
                        activity.relateTagField.selectedTags.add(tag);
                        break;
                    case SELECTED:
                        relateTag.setState(RelateTag.STATE.UNSELECTED);
                        textView.setBackgroundResource(R.drawable.tag_item);
                        textView.setTextColor(Color.parseColor("#F0F0F0"));
                        activity.relateTagField.selectedTags.remove(tag);
                        activity.relateTagField.unselectedTags.add(tag);
                        break;
                    case UNSELECTED:
                        relateTag.setState(RelateTag.STATE.DEFAULT);
                        textView.setBackgroundResource(R.drawable.tag_item);
                        textView.setTextColor(Color.parseColor("#FF4081"));
                        activity.relateTagField.unselectedTags.remove(tag);
                        break;
                }
                activity.musicLinearLayout.changeMusicList();
            }
        };
    }
}
