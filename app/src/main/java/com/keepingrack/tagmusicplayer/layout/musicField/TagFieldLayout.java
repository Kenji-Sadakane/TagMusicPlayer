package com.keepingrack.tagmusicplayer.layout.musicField;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.keepingrack.tagmusicplayer.Variable;

import java.util.List;

import static com.keepingrack.tagmusicplayer.MainActivity.DISPLAY_WIDTH;
import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class TagFieldLayout extends RelativeLayout {

    /**
     * タグTextViewの配置場所
     * UPPER_LEFT:左上(1個目のタグ)
     * RIGHT_OF:前のタグの右
     * NEXT_LINE:次行
     */
    public enum TAG_LOCATION {UPPER_LEFT, RIGHT_OF, NEXT_LINE}

    // タグの表示幅合計
    private float tagLengthByLine = 0;
    // 最終タグのID
    private int lastTagId = -1;

    public float getTagLengthByLine() { return tagLengthByLine; }
    public void setTagLengthByLine(float tagLengthByLine) { this.tagLengthByLine = tagLengthByLine; }
    public int getLastTagId() { return lastTagId; }
    public void setLastTagId(int lastTagId) { this.lastTagId = lastTagId; }

    public TagFieldLayout(String key) {
        super(activity);

        // レイアウト
        this.setLayoutParams(createLayoutParams());
        // コンテンツ
        addTagTextViews(key);
    }

    private LayoutParams createLayoutParams() {
        LayoutParams tagFieldParams = new LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tagFieldParams.setMargins(20, 0, 20, 10); // 左, 上, 右, 下
        return tagFieldParams;
    }

    private void addTagTextViews(String key) {
        initializeField();
        List<String> tags = Variable.getMusicTags(key);
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                this.addView(new TagTextView(this, tag));
            }
        }
    }

    // 初期化
    private void initializeField() {
        tagLengthByLine = 0;
        lastTagId = -1;
    }

    public TAG_LOCATION judgeTagTextLocation(float desiredWidth) {
        TAG_LOCATION location = TAG_LOCATION.UPPER_LEFT;
        if (this.getChildCount() != 0) {
            if ((tagLengthByLine + desiredWidth) > (DISPLAY_WIDTH - 40)) { // 40=tagFieldのmargin(左)+margin(右)
                location = TAG_LOCATION.NEXT_LINE;
            } else {
                location = TAG_LOCATION.RIGHT_OF;
            }
        }
        return location;
    }

    // 全タグ表示幅合計を更新
    public void updateTagLengthByLine(float length) {
        tagLengthByLine += length;
    }
}
