package com.keepingrack.tagmusicplayer.layout.topField;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.keepingrack.tagmusicplayer.Variable;
import com.keepingrack.tagmusicplayer.bean.RelateTag;

import lombok.Getter;
import lombok.Setter;

import static com.keepingrack.tagmusicplayer.MainActivity.DISPLAY_WIDTH;
import static com.keepingrack.tagmusicplayer.RelateTagLogic.selectedTags;
import static com.keepingrack.tagmusicplayer.RelateTagLogic.unselectedTags;

@Getter
@Setter
public class RelateTagLayout extends RelativeLayout {

    /**
     * タグTextViewの配置場所
     * UPPER_LEFT:左上(1個目のタグ)
     * RIGHT_OF:前のタグの右
     * NEXT_LINE:次行
     */
    public enum RELATE_TAG_LOCATION {UPPER_LEFT, RIGHT_OF, NEXT_LINE}

    // 関連タグ表示行数
    private int relateTagLineCount = 1;
    // 全関連タグの表示幅合計
    private float tagLengthByLine = 0;
    // 最終タグのID
    private int lastTagId = -1;

    public RelateTagLayout(Context context, AttributeSet attr) {
        super(context, attr);
    }

    // 関連タグテキストViewを追加
    public void addRelateTags() {
        initializeField();
        for (RelateTag relateTag : Variable.getRelateTags()) {
            this.addView(new RelateTagTextView(relateTag.getTag()));
        }
    }

    // 初期化
    private void initializeField() {
        relateTagLineCount = 1;
        tagLengthByLine = 0;
        lastTagId = -1;
    }

    // 次のタグの配置場所を判定
    public RELATE_TAG_LOCATION judgeTagTextLocation(float desiredWidth) {
        RELATE_TAG_LOCATION location = RELATE_TAG_LOCATION.UPPER_LEFT;
        if (this.getChildCount() != 0) {
            if ((tagLengthByLine + desiredWidth) > (DISPLAY_WIDTH - 40)) { // 40=tagFieldのmargin(左)+margin(右)
                location = RELATE_TAG_LOCATION.NEXT_LINE;
            } else {
                location = RELATE_TAG_LOCATION.RIGHT_OF;
            }
        }
        return location;
    }

    // 関連タグを全て削除
    public void removeRelateTagField() {
        this.removeAllViews();
        selectedTags.clear();
        unselectedTags.clear();
    }

    // 関連タグフィールドの表示幅を算出
    public int calcRelateTagFieldHeight() {
        return relateTagLineCount > 4 ? 300 : relateTagLineCount * 75 + 15;
    }

    // 全タグ表示幅合計を更新
    public void updateTagLengthByLine(float length) {
        tagLengthByLine += length;
    }

    // 関連タグ表示行数を加算
    public void addRelateTagLineCount() {
        relateTagLineCount += 1;
    }
}
