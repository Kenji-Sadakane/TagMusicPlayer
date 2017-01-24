package com.keepingrack.tagmusicplayer.layout;

import android.view.View;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.bean.RelateTag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.keepingrack.tagmusicplayer.MainActivity.displayMusicNames;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;
import static com.keepingrack.tagmusicplayer.MainActivity.relateTags;
import static com.keepingrack.tagmusicplayer.MainActivity.tagKinds;
import static com.keepingrack.tagmusicplayer.layout.topField.SearchSwitch.SEARCH_TYPE;

public class RelateTagField {

    public static List<String> selectedTags = new ArrayList<>();
    public static List<String> unselectedTags = new ArrayList<>();

    private MainActivity activity;

    public RelateTagField(MainActivity _activity) {
        this.activity = _activity;
    }

    // 関連タグフィールド表示を最新化
    public void updateRelateTags() throws Exception {
        activity.relateTagLayout.removeRelateTagField();
        if (canShowRelateTag()) {
            activity.findViewById(R.id.switchRelateTagText).setVisibility(View.VISIBLE);
            createRelateTagList();
            activity.relateTagLayout.addRelateTags();
        } else {
            activity.findViewById(R.id.switchRelateTagText).setVisibility(View.GONE);
        }
        activity.relateTagScrollView.setRelateTagFieldHeight();
    }

    // 関連タグ表示可否判定
    private boolean canShowRelateTag() {
        boolean result = false;
        switch (SEARCH_TYPE) {
            case TITLE:
                break;
            case TAG:
                String keyWord = activity.keyWordEditText.getText().toString();
                if (keyWord != null && !keyWord.isEmpty() && tagKinds.contains(keyWord)) {
                    result = true;
                }
                break;
        }
        return result;
    }

    // 関連タグリスト生成
    private void createRelateTagList() {
        relateTags.clear();
        addRelateTagByDisplayMusic();
        choiceShowRelateTags();
    }

    // 表示楽曲から関連タグを取得しリストに追加
    private void addRelateTagByDisplayMusic() {
        String keyWord = activity.keyWordEditText.getText().toString();
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
        if (relateTags.isEmpty()) { return; }
        List<RelateTag> tmpList = new ArrayList<>();
        tmpList.addAll(relateTags);
        Collections.sort(tmpList, new Comparator<RelateTag>() {
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
        relateTags.clear();
        relateTags.addAll(tmpList);
    }

    // 関連タグリストから抽出
    public RelateTag findRelateTag(String tag) {
        RelateTag target = null;
        for (RelateTag relateTag : relateTags) {
            if (tag.equals(relateTag.getTag())) {
                target = relateTag;
                break;
            }
        }
        return target;
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
        activity.relateTagScrollView.setRelateTagFieldHeight();
        activity.relateTagScrollView.setVisibility(View.VISIBLE);
        activity.relateTagLink.changeTextToClose();
    }

    public void hideRelateTagField() {
        activity.relateTagScrollView.setRelateTagFieldHeight(0);
        activity.relateTagScrollView.setVisibility(View.GONE);
        activity.relateTagLink.changeTextToOpen();
    }

    public void initializeTagField() {
        activity.relateTagLayout.removeRelateTagField();
        hideRelateTagField();
        activity.findViewById(R.id.switchRelateTagText).setVisibility(View.GONE);
    }
}
