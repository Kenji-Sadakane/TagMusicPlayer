package com.keepingrack.tagmusicplayer.layout.musicField;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.R;

import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.SELECT_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.displayMusicNames;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;

public class MusicRow {

    private MainActivity activity;

    public MusicRow(MainActivity _activity) {
        this.activity = _activity;
    }

    public void createAndAddMusicRow(String key) {
        LinearLayout row = createMusicRow();
        LayoutParams rowParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); // width, height
        row.addView(createKeyText(key));
        row.addView(createMusicTitle(key));
        if (((LinearLayout) activity.findViewById(R.id.linearLayout)).getChildCount() > 20) {
            row.setVisibility(View.INVISIBLE);
        }
        addMusicRow(row, rowParams);
        if (!displayMusicNames.contains(key)) { displayMusicNames.add(key); }
        musicItems.get(key).setRow(row);
    }

    private void addMusicRow(final LinearLayout row, final LinearLayout.LayoutParams rowParams) {
        final LinearLayout layout = (LinearLayout) activity.findViewById(R.id.linearLayout);
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                layout.addView(row, rowParams);
            }
        });
    }

    // キー表示用TextView(非表示)作成
    private TextView createKeyText(String key) {
        TextView keyText = new TextView(activity);
        keyText.setText(key);
        keyText.setVisibility(View.GONE);
        return keyText;
    }

    // 楽曲タイトル表示用TextView作成
    private TextView createMusicTitle(String key) {
        TextView musicText = new TextView(activity);
        musicText.setText(musicItems.get(key).getTitle());
        musicText.setPadding(5, 5, 5, 15); // 左, 上, 右, 下
        return musicText;
    }

    public void removeAllMusicRow() {
        final LinearLayout layout = (LinearLayout) activity.findViewById(R.id.linearLayout);
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                if (layout.getChildCount() > 0) {
                    layout.removeAllViews();
                }
            }
        });
    }

    // 1曲表示用フィールド作成
    public LinearLayout createMusicRow() {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setBackgroundResource(R.drawable.normal_row);
        row.setMinimumHeight(150);
        // 楽曲クリック時
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    activity.musicField.screenLock(500);
                    activity.musicField.hideKeyBoard();
                    activity.musicSeekBar.visible();
                    String key = getMusicKey((LinearLayout) v);
                    selectMusic(key);
                } catch (Exception ex) {
                    activity.musicField.msgField.outErrorMessage(ex);
                }
            }
        });
        // 楽曲長押し時
        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.musicField.hideKeyBoard();
                String key = getMusicKey((LinearLayout) v);
                activity.tagInfoDialog.showDialog(key);
                return false;
            }
        });
        return row;
    }

    // 楽曲フィールド(1曲辺り)からキー取得
    private String getMusicKey(LinearLayout row) {
        return ((TextView) row.getChildAt(0)).getText().toString();
    }

    // 楽曲選択処理
    public void selectMusic(String key) throws Exception {
        if (key.equals(SELECT_MUSIC)) { return; }
        activity.musicField.musicTag.hideTagInfo();
        SELECT_MUSIC = key;
        activity.musicField.musicTag.showTagInfo();
    }

    // 楽曲非選択処理
    public void unselectedMusic() {
        activity.musicField.musicTag.hideTagInfo();
        SELECT_MUSIC = "";
    }

    // 再生楽曲の背景色を戻す
    public void resetMusicRowBackGround() {
        if (!PLAYING_MUSIC.isEmpty() && musicItems.get(PLAYING_MUSIC) != null) {
            final LinearLayout row = musicItems.get(PLAYING_MUSIC).getRow();
            activity.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (row != null) {
                        row.setBackgroundResource(R.drawable.normal_row);
                    }
                }
            });
        }
    }

    // 再生楽曲の背景色を変える
    public void setMusicRowBackGround() {
        if (!PLAYING_MUSIC.isEmpty() && musicItems.get(PLAYING_MUSIC) != null) {
            final LinearLayout row = musicItems.get(PLAYING_MUSIC).getRow();
            activity.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (row != null) {
                        row.setBackgroundResource(R.drawable.selected_row);
                    }
                }
            });
        }
    }

    // 楽曲の表示切り替え
    public void changeMusicVisibility(final LinearLayout layout, final int visibility) {
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(visibility);
            }
        });
    }
}
