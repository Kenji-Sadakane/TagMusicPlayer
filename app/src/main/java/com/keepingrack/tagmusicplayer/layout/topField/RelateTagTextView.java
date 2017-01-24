package com.keepingrack.tagmusicplayer.layout.topField;

import android.graphics.Color;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.bean.RelateTag;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class RelateTagTextView extends TextView {

    /**
     * タグTextViewの配置場所
     * UPPER_LEFT:左上(1個目のタグ)
     * RIGHT_OF:前のタグの右
     * NEXT_LINE:次行
     */
    public enum RELATE_TAG_LOCATION {UPPER_LEFT, RIGHT_OF, NEXT_LINE}

    public RelateTagTextView(String tag) {
        super(activity);

        // レイアウト
        this.setClickable(true);
        this.setPadding(10, 0, 10, 0); // 左, 上, 右, 下
        this.setBackgroundResource(R.drawable.tag_item);
//        this.setLayoutParams(createLayoutParams());
        // コンテンツ
        this.setId(View.generateViewId());
        this.setText(tag);
        // リスナー
        this.setOnClickListener(getOnClickListener());
    }

    public void setLayoutParams(RELATE_TAG_LOCATION location, int prevTagId) {
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

    public float getDesiredWidth() {
        return Layout.getDesiredWidth(getText(), getPaint()) + 30;
    }
}
