package com.keepingrack.tagmusicplayer.layout.musicField;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.bean.MusicItem;
import com.keepingrack.tagmusicplayer.layout.TagField;

import static com.keepingrack.tagmusicplayer.MainActivity.SELECT_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;

public class MusicTag {

    private MainActivity activity;

    public MusicTag(MainActivity _activity) {
        this.activity = _activity;
    }

    // タグ情報表示
    public void showTagInfo() throws Exception {
        MusicItem musicItem = musicItems.get(SELECT_MUSIC);
        if (musicItem == null) {
            return;
        }
        LinearLayout row = musicItems.get(SELECT_MUSIC).getRow();
        if (row != null && row.getChildCount() < 3) {
            TagField tagField = new TagField(activity);
            addTagView(row, tagField.createTagField(SELECT_MUSIC), tagField.createTagFieldParams());
        }
    }

    private void addTagView(final LinearLayout row, final RelativeLayout tagFieldLayout, final LinearLayout.LayoutParams tagFieldParams) {
        row.addView(tagFieldLayout, tagFieldParams);
    }

    // タグ情報非表示
    public void hideTagInfo() {
        MusicItem musicItem = musicItems.get(SELECT_MUSIC);
        if (musicItem == null) {
            return;
        }
        LinearLayout selectedRow = musicItem.getRow();
        if (selectedRow != null && selectedRow.getChildCount() > 2) {
            removeTagView(selectedRow);
        }
    }

    private void removeTagView(final LinearLayout row) {
        // 楽曲キー、タイトル以外を削除
        row.removeViewAt(2);
    }
}
