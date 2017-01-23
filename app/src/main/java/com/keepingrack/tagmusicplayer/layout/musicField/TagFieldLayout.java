package com.keepingrack.tagmusicplayer.layout.musicField;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.keepingrack.tagmusicplayer.layout.musicField.TagTextView.TAG_LOCATION;

import java.util.List;

import static com.keepingrack.tagmusicplayer.MainActivity.DISPLAY_WIDTH;
import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;

public class TagFieldLayout extends RelativeLayout {

    public TagFieldLayout(String key) {
        super(activity);

        // レイアウト
        this.setLayoutParams(createLayoutParams());
        // コンテンツ
        setTagTextViews(key);
    }

    private LinearLayout.LayoutParams createLayoutParams() {
        LinearLayout.LayoutParams tagFieldParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tagFieldParams.setMargins(20, 0, 20, 20); // 左, 上, 右, 下
        return tagFieldParams;
    }

    private void setTagTextViews(String key) {
        List<String> tags = musicItems.get(key).getTags();
        if (tags != null && !tags.isEmpty()) {
            int prevTagId = -1;
            int tagLengthByLine = 0;
            for (String tag : tags) {
                TagTextView tagText = new TagTextView(tag);
                tagLengthByLine += tagText.getDesiredWidth();
                TAG_LOCATION location = judgeTagTextLocation(tagLengthByLine);
                tagText.setLayoutParams(location, prevTagId);
                this.addView(tagText);
                prevTagId = tagText.getId();
                tagLengthByLine = doResetTagLengthByLine(location) ? 0 : tagLengthByLine;
            }
        }
    }

    private TAG_LOCATION judgeTagTextLocation(int tagLengthByLine) {
        TAG_LOCATION location = TAG_LOCATION.UPPER_LEFT;
        if (this.getChildCount() != 0) {
            if (tagLengthByLine > DISPLAY_WIDTH - 40) { // 40=tagFieldのmargin(左)+margin(右)
                location = TAG_LOCATION.NEXT_LINE;
            } else {
                location = TAG_LOCATION.RIGHT_OF;
            }
        }
        return location;
    }

    private boolean doResetTagLengthByLine(TAG_LOCATION location) {
        switch (location) {
            case NEXT_LINE:
                return true;
        }
        return false;
    }
}
