package com.keepingrack.tagmusicplayer.layout;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.bean.MusicItem;
import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.layout.musicField.MsgField;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicRow;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.keepingrack.tagmusicplayer.MainActivity.displayMusicNames;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;
import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.musicKeys;

public class MusicField {

    public MsgField msgField;
    public MusicRow musicRow;
    public MusicTag musicTag;
    private MainActivity activity;

    public MusicField(MainActivity _activity) {
        this.activity = _activity;
        msgField = new MsgField(activity);
        musicRow = new MusicRow(activity);
        musicTag = new MusicTag(activity);
    }

    // リスナー
    public void setListener() {
        setOnPanelTouchListener();
        setOnScrollChangeListener();
        setOnTouchListener();
    }

    // スクロールビュータッチ時処理
    private void setOnPanelTouchListener() {
        ((View) activity.findViewById(R.id.grayPanel)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // イベントの伝搬を阻止
                return true;
            }
        });
    }

    // スクロール時処理
    private void setOnScrollChangeListener() {
        final ScrollView scrollView = ((ScrollView) activity.findViewById(R.id.scrollView));
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                for (String key : musicKeys) {
                    LinearLayout musicRow = musicItems.get(key).getRow();
                    if (musicRow.getVisibility() != View.GONE) {
                        int rowY = (int) musicRow.getY();
                        if (scrollY - 2000 < rowY && rowY < scrollY + 2000) {
                            musicRow.setVisibility(View.VISIBLE);
                        } else {
                            musicRow.setVisibility(View.INVISIBLE);
                        }
                    }
                }
            }
        });
    }

    // スクロールビュータッチ時処理
    private void setOnTouchListener() {
        ((ScrollView) activity.findViewById(R.id.scrollView)).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    int action = event.getAction();
                    hideKeyBoard();
                    activity.musicSeekBar.invisible();
                } catch (Exception ex) {
                    msgField.outErrorMessage(ex);
                }
                // イベントの伝搬を阻止しない
                return false;
            }
        });
    }

    public void hideKeyBoard() {
        ((TextView) activity.findViewById(R.id.dummyText)).requestFocus();
    }

    // スクロールビュー内コンテンツ作成
    public void createContents() throws Exception {
        clearContents();
        for (String key : musicKeys) {
            musicRow.createAndAddMusicRow(key);
        }
        // 余白追加
        addWhiteSpaceView();
    }

    private void addWhiteSpaceView() {
        final TextView whiteSpace = new TextView(activity);
        whiteSpace.setHeight(350);
        final LinearLayout layout = (LinearLayout) activity.findViewById(R.id.linearLayout);
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                layout.addView(whiteSpace);
            }
        });
    }

    // スクロールビュー内コンテンツをクリア
    private void clearContents() {
        musicRow.removeAllMusicRow();
        displayMusicNames.clear();
        for (Map.Entry<String, MusicItem> musicItemMap : musicItems.entrySet()) {
            MusicItem musicItem = musicItemMap.getValue();
            musicItem.setRow(null); // この処理いる？
        }
    }

    public void screenLock(final int millsecond) {
        ((View) activity.findViewById(R.id.grayPanel)).setVisibility(View.VISIBLE);
        activity.handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ((View) activity.findViewById(R.id.grayPanel)).setVisibility(View.GONE);
            }
        }, millsecond);
    }

    // 再生中の楽曲にスクロールする
    public void scrollMusicView() {
        int y = (int) musicItems.get(PLAYING_MUSIC).getRow().getY();
        scrollMusicView(y);
    }

    public void scrollMusicView(int y) {
        ((ScrollView) activity.findViewById(R.id.scrollView)).smoothScrollTo(0, y);
    }

    // 表示楽曲リスト変更
    public void changeMusicList() {
        displayMusicNames.clear();
        for (String key : musicKeys) {
            LinearLayout row = musicItems.get(key).getRow();
            if (activity.keyWord.checkKeyWordMatch(key) && activity.relateTagField.chkRelateTagState(key)) {
                if (displayMusicNames.size() > 20) {
                    musicRow.changeMusicVisibility(row, View.INVISIBLE);
                } else {
                    musicRow.changeMusicVisibility(row, View.VISIBLE);
                }
                displayMusicNames.add(key);
            } else {
                musicRow.changeMusicVisibility(row, View.GONE);
            }
        }
        activity.shuffleMusicList.exec();
        scrollMusicView(0);

//        LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
//        layout.startAnimation((Animation) AnimationUtils.loadAnimation(this, R.anim.out_animation));
//        for (int i = 0; i < layout.getChildCount(); i++) {
//            if (layout.getChildAt(i) instanceof LinearLayout) {
//                LinearLayout row = (LinearLayout) layout.getChildAt(i);
//                changeVisibility(keyWord, row);
//            }
//        }
//        layout.startAnimation((Animation) AnimationUtils.loadAnimation(this, R.anim.in_animation));
    }

    // 楽曲フィールド非表示アニメーション
    public ObjectAnimator getHideAnimation() {
        LinearLayout target = (LinearLayout) activity.findViewById(R.id.linearLayout);
        List<Animator> anmList= new ArrayList<Animator>();
        ObjectAnimator anm = ObjectAnimator.ofFloat(target, "translationY", 0f, -4000f);
        anm.setDuration(1000);
        anmList.add(anm);
        return anm;
    }

    // 楽曲フィールド表示アニメーション
    public ObjectAnimator getShowAnimation() {
        LinearLayout target = (LinearLayout) activity.findViewById(R.id.linearLayout);
        List<Animator> anmList= new ArrayList<Animator>();
        ObjectAnimator anm = ObjectAnimator.ofFloat(target, "translationY", -4000f, 0f);
        anm.setDuration(1000);
        anmList.add(anm);
        return anm;
    }
}
