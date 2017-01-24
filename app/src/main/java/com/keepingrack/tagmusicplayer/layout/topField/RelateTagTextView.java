package com.keepingrack.tagmusicplayer.layout.topField;

import android.graphics.Color;
import android.text.Layout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.bean.RelateTag;
import com.keepingrack.tagmusicplayer.layout.topField.RelateTagLayout.RELATE_TAG_LOCATION;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class RelateTagTextView extends TextView {

    // 親レイアウトへの配置場所
    private RELATE_TAG_LOCATION location;

    public RelateTagTextView(String tag) {
        super(activity);

        // コンテンツ
        this.setId(View.generateViewId());
        this.setText(tag);
        // レイアウト
        this.setClickable(true);
        this.setPadding(10, 0, 10, 0); // 左, 上, 右, 下
        this.setBackgroundResource(R.drawable.tag_item);
        this.setLayoutParams(createLayoutParams());
        // リスナー
        this.setOnClickListener(getOnClickListener());
        // 親レイアウトの情報更新
        updateRelateTagLayout();
    }

    private RelativeLayout.LayoutParams createLayoutParams() {
        RelativeLayout.LayoutParams tagTextParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tagTextParams.setMargins(10, 0, 0, 15); // 左, 上, 右, 下
        location = activity.relateTagLayout.judgeTagTextLocation(getDesiredWidth());
        switch (location) {
            case UPPER_LEFT:
                break;
            case RIGHT_OF:
                tagTextParams.addRule(RelativeLayout.ALIGN_TOP, activity.relateTagLayout.getLastTagId());
                tagTextParams.addRule(RelativeLayout.RIGHT_OF, activity.relateTagLayout.getLastTagId());
                break;
            case NEXT_LINE:
                tagTextParams.addRule(RelativeLayout.BELOW, activity.relateTagLayout.getLastTagId());
                break;
            default:
                break;
        }
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

    // 親レイアウトの情報更新
    private void updateRelateTagLayout() {
        activity.relateTagLayout.setLastTagId(getId());
        if (RELATE_TAG_LOCATION.NEXT_LINE.equals(location)) {
            activity.relateTagLayout.addRelateTagLineCount();
            activity.relateTagLayout.setTagLengthByLine(getDesiredWidth());
        } else {
            activity.relateTagLayout.updateTagLengthByLine(getDesiredWidth());
        }
    }

    // 自Viewの横幅算出
    public float getDesiredWidth() {
        return Layout.getDesiredWidth(getText(), getPaint()) + 30;
    }
}
