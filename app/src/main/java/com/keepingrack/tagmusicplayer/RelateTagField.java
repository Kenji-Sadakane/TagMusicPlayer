package com.keepingrack.tagmusicplayer;

import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.bean.RelateTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.keepingrack.tagmusicplayer.MainActivity.displayMusicNames;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;
import static com.keepingrack.tagmusicplayer.MainActivity.relateTags;
import static com.keepingrack.tagmusicplayer.MainActivity.tagKinds;
import static com.keepingrack.tagmusicplayer.SearchSwitch.SEARCH_TYPE;

public class RelateTagField {

    public static boolean isRelateTagShow = false;
    public static List<String> selectedTags = new ArrayList<>();
    public static List<String> unselectedTags = new ArrayList<>();

    private MainActivity activity;

    public RelateTagField(MainActivity _activity) {
        this.activity = _activity;
    }

    // 関連タグ表示切り替え時
    public void onRelateTagChangeLinkClicked() {
        if (isRelateTagShow) {
            hideRelateTagField();
        } else {
            showRelateTagField();
        }
    }

    // 関連タグフィールド表示を最新化
    public void updateRelateTags() throws Exception {
        removeRelateTagField();
        if (canShowRelateTag()) {
            activity.findViewById(R.id.switchRelateTagText).setVisibility(View.VISIBLE);
            addRelateTagField();
        } else {
            activity.findViewById(R.id.switchRelateTagText).setVisibility(View.GONE);
        }
        setRelateTagFieldHeight();
    }

    // 関連タグ表示可否判定
    private boolean canShowRelateTag() {
        boolean result = false;
        switch (SEARCH_TYPE) {
            case TITLE:
                break;
            case TAG:
                String keyWord = ((EditText) activity.findViewById(R.id.editText)).getText().toString();
                if (keyWord != null && !keyWord.isEmpty() && tagKinds.contains(keyWord)) {
                    result = true;
                }
                break;
        }
        return result;
    }

    // 関連タグフィールドを画面から削除
    public void removeRelateTagField() {
        ((LinearLayout) activity.findViewById(R.id.relateTagList)).removeAllViews();
        selectedTags.clear();
        unselectedTags.clear();
    }

    // 関連タグフィールドを画面に追加
    public void addRelateTagField() throws Exception {
        createRelateTagList();
        LinearLayout tagField = createTagField();
        int tagLengthByField = 0;
        for (RelateTag relateTag : relateTags) {
            TextView tagText = createTagText(relateTag.getTag());
            int tagLength = relateTag.getTag().getBytes("Shift_JIS").length;
            if (tagField.getChildCount() > 0 && (tagLengthByField + tagLength) > 40) {
                tagField = createTagField();
                tagLengthByField = tagLength;
            } else {
                tagLengthByField += tagLength;
            }
            tagField.addView(tagText);
        }
    }

    // 関連タグフィールドの表示幅を設定
    public void setRelateTagFieldHeight() {
        ((ScrollView) activity.findViewById(R.id.relateTagView)).getLayoutParams().height = calcRelateTagFieldHeight();
    }

    // 関連タグフィールドの表示幅を算出
    public int calcRelateTagFieldHeight() {
        int relateTagLineCount = ((LinearLayout) activity.findViewById(R.id.relateTagList)).getChildCount();
        return relateTagLineCount > 4 ? 300 : relateTagLineCount * 75;
    }

    // 関連タグリスト生成
    private void createRelateTagList() {
        relateTags.clear();
        addRelateTagByDisplayMusic();
        choiceShowRelateTags();
    }

    // 表示楽曲から関連タグを取得しリストに追加
    private void addRelateTagByDisplayMusic() {
        String keyWord = ((EditText) activity.findViewById(R.id.editText)).getText().toString();
        for (String key : displayMusicNames) {
            for (String tag : musicItems.get(key).getTags()) {
                if (tag.equals(keyWord)) {
                    continue;
                }
                RelateTag relateTag = findRelateTag(tag);
                if (relateTag != null) {
                    relateTag.setCount(relateTag.getCount() + 1);
                } else {
                    relateTags.add(new RelateTag(tag, 1, RelateTag.STATE.DEFAULT));
                }
            }
        }
    }

    // 画面表示する関連タグのみ保持する
    private void choiceShowRelateTags() {
        List<RelateTag> tmpRelateTags = new ArrayList<>();
        sortRelateTags();
        for (RelateTag tag : relateTags) {
            if (tag.getCount() > 1) {
                tmpRelateTags.add(tag);
            } else {
                break;
            }
        }
        relateTags = tmpRelateTags;
    }

    // 関連タグリストをソート
    private void sortRelateTags() {
        Collections.sort(relateTags, new Comparator<RelateTag>() {
            @Override
            public int compare(RelateTag a, RelateTag b) {
                // count(曲数)の降順
                if (a.getCount() > b.getCount()) {
                    return -1;
                } else if (a.getCount() < b.getCount()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
    }

    // 関連タグリストから抽出
    private RelateTag findRelateTag(String tag) {
        RelateTag target = null;
        for (RelateTag relateTag : relateTags) {
            if (tag.equals(relateTag.getTag())) {
                target = relateTag;
                break;
            }
        }
        return target;
    }

    // タグテキスト(1つ分)作成
    private TextView createTagText(String tag) {
        TextView tagText = new TextView(activity);
        tagText.setClickable(true);
        tagText.setText(tag);
        tagText.setPadding(5, 0, 5, 0); // 左, 上, 右, 下
        tagText.setBackgroundResource(R.drawable.tag_item);
        LinearLayout.LayoutParams tagTextParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tagTextParams.setMargins(0, 0, 10, 0); // 左, 上, 右, 下
        tagText.setLayoutParams(tagTextParams);
        // タグクリック時
        tagText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = (TextView) v;
                String tag = textView.getText().toString();
                RelateTag relateTag = findRelateTag(tag);
                if (relateTag == null) {
                    return;
                }
                switch (relateTag.getState()) {
                    case DEFAULT:
                        relateTag.setState(RelateTag.STATE.SELECTED);
                        textView.setBackgroundResource(R.drawable.related_tag_selected);
                        textView.setTextColor(Color.parseColor("#FF4081"));
                        selectedTags.add(tag);
                        break;
                    case SELECTED:
                        relateTag.setState(RelateTag.STATE.UNSELECTED);
                        textView.setBackgroundResource(R.drawable.tag_item);
                        textView.setTextColor(Color.parseColor("#F0F0F0"));
                        selectedTags.remove(tag);
                        unselectedTags.add(tag);
                        break;
                    case UNSELECTED:
                        relateTag.setState(RelateTag.STATE.DEFAULT);
                        textView.setBackgroundResource(R.drawable.tag_item);
                        textView.setTextColor(Color.parseColor("#FF4081"));
                        unselectedTags.remove(tag);
                        break;
                }
                activity.musicField.changeMusicList();
            }
        });
        return tagText;
    }

    // タグ表示領域(1行分)生成
    private LinearLayout createTagField() {
        LinearLayout tagField = new LinearLayout(activity);
        tagField.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams tagFieldParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tagFieldParams.setMargins(15, 0, 15, 15); // 左, 上, 右, 下
        ((LinearLayout) activity.findViewById(R.id.relateTagList)).addView(tagField, tagFieldParams);
        return tagField;
    }

    // 関連タグの状態を考慮し対象楽曲の表示可否を判定
    public boolean chkRelateTagState(String key) {
        // 表示タグ、非表示タグが存在しない
        if (selectedTags.isEmpty() && unselectedTags.isEmpty()) {
            return true;
        }
        if (hasUnselectedTag(key)) {
            // 非表示タグ保持
            return false;
        } else if (hasSelectedTag(key)) {
            // 表示タグ保持
            return true;
        }
        // 表示タグ、非表示タグ非所持
        if (!selectedTags.isEmpty()) {
            // 表示タグが存在する
            return false;
        } else if (!unselectedTags.isEmpty()) {
            // 非表示タグが存在
            return true;
        } else {
            // 表示タグ、非表示タグが存在しない(ありえない)
            return true;
        }
    }

    // 選択済み関連タグを対象楽曲が一つでも持っているか判定
    public boolean hasSelectedTag(String key) {
        List<String> musicTags = musicItems.get(key).getTags();
        for (String selectedTag : selectedTags) {
            if (musicTags.contains(selectedTag)) {
                return true;
            }
        }
        return false;
    }

    // 非選択関連タグを対象楽曲が一つでも持っているか判定
    public boolean hasUnselectedTag(String key) {
        List<String> musicTags = musicItems.get(key).getTags();
        for (String unselectedTag : unselectedTags) {
            if (musicTags.contains(unselectedTag)) {
                return true;
            }
        }
        return false;
    }

    public void showRelateTagField() {
        ScrollView relateTagField = (ScrollView) activity.findViewById(R.id.relateTagView);
        setRelateTagFieldHeight();
        relateTagField.setVisibility(View.VISIBLE);
        ((TextView) activity.findViewById(R.id.switchRelateTagText)).setText(R.string.hide_relate_tag);
        isRelateTagShow = true;
    }

    public void hideRelateTagField() {
        ScrollView relateTagField = (ScrollView) activity.findViewById(R.id.relateTagView);
        setRelateTagFieldHeight();
        relateTagField.setVisibility(View.GONE);
        ((TextView) activity.findViewById(R.id.switchRelateTagText)).setText(R.string.show_relate_tag);
        isRelateTagShow = false;
    }
}
