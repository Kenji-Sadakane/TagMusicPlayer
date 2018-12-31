package com.keepingrack.tagmusicplayer.layout.musicField;

import android.widget.RelativeLayout;

import com.keepingrack.tagmusicplayer.Variable;
import com.keepingrack.tagmusicplayer.file.ApplicationLog;

import java.util.List;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.util.StringUtil.*;

public class TagFieldLayout extends RelativeLayout {

    /**
     * タグTextViewの配置場所
     * UPPER_LEFT:左上(1個目のタグ)
     * RIGHT_OF:前のタグの右
     * NEXT_LINE:次行
     */
    public enum TAG_LOCATION {UPPER_LEFT, RIGHT_OF, NEXT_LINE}

    // 本レイアウトの最大幅
    private int maxWidth;
    // タグの表示幅合計
    private float tagLengthByLine = 0;
    // 最終タグのID
    private int lastTagId = -1;

    public int getMaxWidth() { return maxWidth; }
    public void setMaxWidth(int maxWidth) { this.maxWidth = maxWidth; }
    public float getTagLengthByLine() { return tagLengthByLine; }
    public void setTagLengthByLine(float tagLengthByLine) { this.tagLengthByLine = tagLengthByLine; }
    public int getLastTagId() { return lastTagId; }
    public void setLastTagId(int lastTagId) { this.lastTagId = lastTagId; }

    public TagFieldLayout() {
        super(activity);
        this.setLayoutParams(createLayoutParams());
    }

    private LayoutParams createLayoutParams() {
        LayoutParams tagFieldParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tagFieldParams.setMargins(20, 0, 20, 10); // 左, 上, 右, 下
        return tagFieldParams;
    }

    public void addTagTextViews(String key) {
        initializeField();
        List<String> tags = Variable.getMusicTags(key);
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                TagTextView tagTextView = new TagTextView(tag);
                addTagTextViewByLocation(tagTextView);
            }
        }
    }

    // 初期化
    private void initializeField() {
        tagLengthByLine = 0;
        lastTagId = -1;
    }

    private void addTagTextViewByLocation(TagTextView tagTextView) {
        float tagTextViewLength = tagTextView.getDesiredWidth();
        TAG_LOCATION tagLocation = getTagTextLocation(tagTextViewLength);
        setLocationToLayoutParam(tagTextView, tagLocation);
        this.addView(tagTextView);
        setLastTagId(tagTextView.getId());
        updateTagLengthByLine(tagTextViewLength, tagLocation);
        ApplicationLog.debug(concat("tag:", tagTextView.getText().toString(),
                ".location", tagLocation.toString(), ",tagLengthByLine", castString(tagLengthByLine)
        ));
    }

    private TAG_LOCATION getTagTextLocation(float desiredWidth) {
        TAG_LOCATION location = TAG_LOCATION.UPPER_LEFT;
        if (this.getChildCount() != 0) {
            if ((tagLengthByLine + desiredWidth) > (getMaxWidth() - 40)) { // 40=tagFieldのmargin(左)+margin(右)
                location = TAG_LOCATION.NEXT_LINE;
            } else {
                location = TAG_LOCATION.RIGHT_OF;
            }
        }
        return location;
    }

    private void setLocationToLayoutParam(TagTextView tagTextView, TAG_LOCATION tagLocation) {
        LayoutParams tagTextParams = (LayoutParams) tagTextView.getLayoutParams();
        switch (tagLocation) {
            case UPPER_LEFT:
                break;
            case RIGHT_OF:
                tagTextParams.addRule(RelativeLayout.ALIGN_TOP, getLastTagId());
                tagTextParams.addRule(RelativeLayout.RIGHT_OF, getLastTagId());
                break;
            case NEXT_LINE:
                tagTextParams.addRule(RelativeLayout.BELOW, getLastTagId());
                break;
            default:
                break;
        }
    }

    // 全タグ表示幅合計を更新
    private void updateTagLengthByLine(float length, TAG_LOCATION tagLocation) {
        switch (tagLocation) {
            case UPPER_LEFT:
            case RIGHT_OF:
                tagLengthByLine += length;
                break;
            case NEXT_LINE:
                tagLengthByLine = length;
                break;
            default:
                break;
        }
    }
}
