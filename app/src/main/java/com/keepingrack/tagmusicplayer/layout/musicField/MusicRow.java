package com.keepingrack.tagmusicplayer.layout.musicField;

import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.Variable;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.layout.musicField.MusicLinearLayout.SELECT_MUSIC;

public class MusicRow extends RelativeLayout {

    private MusicCheckBox checkBox;
    private TextView keyText;
    private TextView musicTitle;
    private TagFieldLayout tagField;

    public MusicCheckBox getCheckBox() { return checkBox; }
    public void setCheckBox(MusicCheckBox checkBox) { this.checkBox = checkBox; }
    public TextView getKeyText() { return keyText; }
    public void setKeyText(TextView keyText) { this.keyText = keyText; }
    public TextView getMusicTitle() { return musicTitle; }
    public void setMusicTitle(TextView musicTitle) { this.musicTitle = musicTitle; }
    public TagFieldLayout getTagField() { return tagField; }
    public void setTagField(TagFieldLayout tagField) { this.tagField = tagField; }

    public MusicRow(String key) {
        super(activity);

        // レイアウト
        this.setBackgroundResource(R.drawable.normal_row);
        this.setMinimumHeight(150);
        // コンテンツ
        createCheckBox();
        createKeyText(key);
        createMusicTitle(key);
        this.addView(getCheckBox());
        this.addView(getKeyText());
        this.addView(getMusicTitle());
        // リスナー
        this.setOnClickListener(getOnClickListener());
        this.setOnLongClickListener(getOnLongClickListener());
    }

    // チェックボックス作成
    private void createCheckBox() {
        MusicCheckBox checkBox = new MusicCheckBox(this);
        setCheckBox(checkBox);
    }

    // キー表示用TextView(非表示)作成
    private void createKeyText(String key) {
        TextView keyText = new TextView(activity);
        keyText.setText(key);
        keyText.setVisibility(View.GONE);
        setKeyText(keyText);
    }

    // 楽曲タイトル表示用TextView作成
    private void createMusicTitle(String key) {
        TextView musicText = new TextView(activity);
        musicText.setId(View.generateViewId());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, getCheckBox().getId());
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
                try {
                    activity.hideKeyBoard();
                    checkBox.checkManually(true);
                    activity.tagInfoDialog.showDialog();
                } catch (Exception ex) {
                    activity.msgView.outErrorMessage(ex);
                }
                return false;
            }
        };
    }

    // タグ情報表示
    public void showTagInfo() {
        if (getTagField() == null) {
            TagFieldLayout tagFieldLayout = new TagFieldLayout(getMusicKey());
            LayoutParams params = (RelativeLayout.LayoutParams) tagFieldLayout.getLayoutParams();
            params.addRule(RelativeLayout.BELOW, getMusicTitle().getId());
            params.addRule(RelativeLayout.RIGHT_OF, getCheckBox().getId());
            this.addView(tagFieldLayout);
            setTagField(tagFieldLayout);
        }
    }

    // タグ情報非表示化
    public void hideTagInfo() {
        if (getTagField() != null) {
            this.removeView(getTagField());
            setTagField(null);
        }
    }

    public String getMusicKey() {
        return getKeyText().getText().toString();
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
