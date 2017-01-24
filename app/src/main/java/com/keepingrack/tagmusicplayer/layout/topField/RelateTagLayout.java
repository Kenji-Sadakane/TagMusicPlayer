package com.keepingrack.tagmusicplayer.layout.topField;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.keepingrack.tagmusicplayer.bean.RelateTag;
import com.keepingrack.tagmusicplayer.layout.topField.RelateTagTextView.RELATE_TAG_LOCATION;

import static com.keepingrack.tagmusicplayer.MainActivity.DISPLAY_WIDTH;
import static com.keepingrack.tagmusicplayer.MainActivity.relateTags;
import static com.keepingrack.tagmusicplayer.layout.RelateTagField.selectedTags;
import static com.keepingrack.tagmusicplayer.layout.RelateTagField.unselectedTags;

public class RelateTagLayout extends RelativeLayout {

    private int relateTagLineCount = 0;

    public RelateTagLayout(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void addRelateTags() {
        int prevTagId = -1;
        int tagLengthByLine = 0;
        relateTagLineCount = 1;
        for (RelateTag relateTag : relateTags) {
            RelateTagTextView tagText = new RelateTagTextView(relateTag.getTag());
            tagLengthByLine += tagText.getDesiredWidth();
            RELATE_TAG_LOCATION location = judgeTagTextLocation(tagLengthByLine);
            tagText.setLayoutParams(location, prevTagId);
            this.addView(tagText);
            if (location.equals(RELATE_TAG_LOCATION.NEXT_LINE)) {
                tagLengthByLine = 0;
                relateTagLineCount++;
            }
            prevTagId = tagText.getId();
        }
    }

    // 次のタグの配置場所を判定
    private RELATE_TAG_LOCATION judgeTagTextLocation(int tagLengthByLine) {
        RELATE_TAG_LOCATION location = RELATE_TAG_LOCATION.UPPER_LEFT;
        if (this.getChildCount() != 0) {
            if (tagLengthByLine > DISPLAY_WIDTH - 40) { // 40=tagFieldのmargin(左)+margin(右)
                location = RELATE_TAG_LOCATION.NEXT_LINE;
            } else {
                location = RELATE_TAG_LOCATION.RIGHT_OF;
            }
        }
        return location;
    }

    public void removeRelateTagField() {
        this.removeAllViews();
        selectedTags.clear();
        unselectedTags.clear();
    }

    // 関連タグフィールドの表示幅を算出
    public int calcRelateTagFieldHeight() {
        return relateTagLineCount > 4 ? 300 : relateTagLineCount * 75;
    }
}
