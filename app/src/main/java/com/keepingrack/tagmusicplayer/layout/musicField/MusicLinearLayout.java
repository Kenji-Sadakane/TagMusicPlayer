package com.keepingrack.tagmusicplayer.layout.musicField;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.Variable;

import java.util.ArrayList;
import java.util.List;

import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class MusicLinearLayout extends LinearLayout {

    public static String SELECT_MUSIC = "";

    public MusicLinearLayout(Context context) {
        super(context);
    }
    public MusicLinearLayout(Context context, AttributeSet attr) {
        super(context, attr);
    }

    // スクロールビュー内コンテンツ作成
    public void createContents() throws Exception {
        clearContents();
        for (String key : Variable.getMusicKeys()) {
            createAndAddMusicRow(key);
        }
        // 余白追加
        addWhiteSpaceViewAsync();
    }
    private void createAndAddMusicRow(String key) {
        MusicRow row = new MusicRow(key);
        LayoutParams rowParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); // width, height
//        if (getChildCount() > 20) {
//            row.setVisibility(View.INVISIBLE);
//        }
        addMusicRow(row, rowParams);
        Variable.addDisplayMusicNames(key);
        Variable.setMusicRow(key, row);
    }
    private void addMusicRow(final MusicRow row, final LinearLayout.LayoutParams rowParams) {
        final MusicLinearLayout layout = this;
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                layout.addView(row, rowParams);
            }
        });
    }

    // 余白用View追加
    private void addWhiteSpaceViewAsync() {
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                addWhiteSpaceView();
            }
        });
    }
    private void addWhiteSpaceView() {
        TextView whiteSpace = new TextView(activity);
        whiteSpace.setHeight(350);
        this.addView(whiteSpace);
    }

    // スクロールビュー内コンテンツをクリア
    private void clearContents() {
        removeAllMusicRow();
        Variable.clearDisplayMusicNames();
        Variable.clearMusicRow();
    }
    public void removeAllMusicRow() {
        final LinearLayout layout = this;
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                if (layout.getChildCount() > 0) {
                    layout.removeAllViews();
                }
            }
        });
    }

    // 表示楽曲リスト変更
    public void changeMusicList() {
        Variable.clearDisplayMusicNames();
        for (String key : Variable.getMusicKeys()) {
            MusicRow row = Variable.getMusicRow(key);
            if (activity.keyWordEditText.checkKeyWordMatch(key) && activity.relateTagLogic.chkRelateTagState(key)) {
//                if (Variable.getDisplayMusicNames().size() > 20) {
//                    row.changeMusicVisibility(View.INVISIBLE);
//                } else {
//                    row.changeMusicVisibility(View.VISIBLE);
//                }
                row.changeMusicVisibility(View.VISIBLE);
                Variable.addDisplayMusicNames(key);
            } else {
                row.changeMusicVisibility(View.GONE);
            }
        }
        activity.shuffleMusicList.exec();
        activity.musicScrollView.scrollMusicView(0);
    }

    // 楽曲を選択、現在楽曲を非選択
    public void selectMusicAndDeselectOldMusic(String key) {
        deselectOldMusic(key);
        selectMusic(key);
    }
    public void selectMusic(String key) {
        if (Variable.getMusicItem(key) != null && Variable.getMusicRow(key) != null) {
            Variable.getMusicRow(key).showTagInfo();
            SELECT_MUSIC = key;
        }
    }

    // 楽曲非選択
    private void deselectOldMusic(String key) {
        if (!SELECT_MUSIC.isEmpty() && !SELECT_MUSIC.equals(key)) {
            deselectMusic(SELECT_MUSIC);
        }
    }
    public void deselectMusic(String key) {
        if (Variable.getMusicItem(key) != null && Variable.getMusicRow(key) != null) {
            Variable.getMusicRow(key).hideTagInfo();
            SELECT_MUSIC = "";
        }
    }

    // 再生楽曲の背景色を変える
    public void setMusicRowBackGround() {
        if (!PLAYING_MUSIC.isEmpty() && Variable.getMusicItem(PLAYING_MUSIC) != null) {
            final MusicRow row = Variable.getMusicRow(PLAYING_MUSIC);
            activity.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (row != null) {
                        row.setMusicRowBackGround();
                    }
                }
            });
        }
    }
    // 再生楽曲の背景色を戻す
    public void resetMusicRowBackGround() {
        if (!PLAYING_MUSIC.isEmpty() && Variable.getMusicItem(PLAYING_MUSIC) != null) {
            final MusicRow row = Variable.getMusicRow(PLAYING_MUSIC);
            activity.handler.post(new Runnable() {
                @Override
                public void run() {
                    if (row != null) {
                        row.resetMusicRowBackGround();
                    }
                }
            });
        }
    }

    // 楽曲フィールド非表示アニメーション
    public ObjectAnimator getHideAnimation() {
        ObjectAnimator anm = ObjectAnimator.ofFloat(this, "translationY", 0f, -4000f);
        anm.setDuration(1000);
        return anm;
    }
    // 楽曲フィールド表示アニメーション
    public ObjectAnimator getShowAnimation() {
        ObjectAnimator anm = ObjectAnimator.ofFloat(this, "translationY", -4000f, 0f);
        anm.setDuration(1000);
        return anm;
    }

    // 楽曲リストをX軸方向に移動
    public void addX(int distant) {
        this.setX(this.getX() + distant);
    }
}
