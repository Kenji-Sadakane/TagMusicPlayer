package com.keepingrack.tagmusicplayer.layout.musicField;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.Variable;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.layout.musicField.MusicLinearLayout.SELECT_MUSIC;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MusicRow extends RelativeLayout {

    private TextView musicTitle;

    public MusicRow(String key) {
        super(activity);

        // レイアウト
        this.setBackgroundResource(R.drawable.normal_row);
        this.setMinimumHeight(150);
        // コンテンツ
        this.addView(createKeyText(key));
        createMusicTitle(key);
        this.addView(getMusicTitle());
        // リスナー
        this.setOnClickListener(getOnClickListener());
        this.setOnLongClickListener(getOnLongClickListener());
    }

    // キー表示用TextView(非表示)作成
    private TextView createKeyText(String key) {
        TextView keyText = new TextView(activity);
        keyText.setText(key);
        keyText.setVisibility(View.GONE);
        return keyText;
    }

    // 楽曲タイトル表示用TextView作成
    private void createMusicTitle(String key) {
        TextView musicText = new TextView(activity);
        musicText.setId(View.generateViewId());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(20, 5, 20, 15); // 左, 上, 右, 下
        musicText.setLayoutParams(params);
        musicText.setText(Variable.getMusicItem(key) != null ? Variable.getMusicTitle(key) : "");
//        musicText.setPadding(5, 5, 5, 15); // 左, 上, 右, 下
        setMusicTitle(musicText);
    }

    // 楽曲クリック時
    private View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (getMusicKey().equals(SELECT_MUSIC)) { return; }
                    activity.grayPanel.screenLock(500);
                    activity.hideKeyBoard();
                    activity.musicSeekBar.visible();
                    activity.musicLinearLayout.selectMusicAndDeselectOldMusic(getMusicKey());
                    showTagInfo();
                } catch (Exception ex) {
                    activity.msgView.outErrorMessage(ex);
                }
            }
        };
    }

    // 楽曲長押し時
    private View.OnLongClickListener getOnLongClickListener() {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                activity.hideKeyBoard();
                activity.tagInfoDialog.showDialog(getMusicKey());
                return false;
            }
        };
    }

    // タグ情報表示
    public void showTagInfo() {
        if (this.getChildCount() < 3) {
            TagFieldLayout tagFieldLayout = new TagFieldLayout(getMusicKey());
            LayoutParams params = (RelativeLayout.LayoutParams) tagFieldLayout.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, getMusicTitle().getId());
            this.addView(tagFieldLayout);
        }
    }

    // タグ情報非表示化
    public void hideTagInfo() {
        if (this.getChildCount() > 2) {
            // 楽曲キー、タイトル以外を削除
            this.removeViewAt(2);
        }
    }

    private String getMusicKey() {
        return ((TextView) this.getChildAt(0)).getText().toString();
    }

    // 再生楽曲の背景色を戻す
    public void resetMusicRowBackGround() {
        this.setBackgroundResource(R.drawable.normal_row);
    }

    // 再生楽曲の背景色を変える
    public void setMusicRowBackGround() {
        this.setBackgroundResource(R.drawable.selected_row);
    }

    // 楽曲の表示切り替え
    public void changeMusicVisibility(final int visibility) {
        final RelativeLayout layout = this;
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                layout.setVisibility(visibility);
            }
        });
    }
}
