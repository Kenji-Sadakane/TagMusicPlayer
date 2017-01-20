package com.keepingrack.tagmusicplayer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.keepingrack.tagmusicplayer.MainActivity.displayMusicNames;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;
import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.SELECT_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.musicKeys;

public class MusicField {

    private MainActivity activity;

    public MusicField(MainActivity _activity) {
        this.activity = _activity;
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
                    outErrorMessage(ex);
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
        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.linearLayout);
        for (String key : musicKeys) {
            MusicItem musicItem = musicItems.get(key);
            // 楽曲フィールド作成
            LinearLayout row = createMusicRow();
            LayoutParams rowParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); // width, height
            row.addView(createKeyText(key));
            row.addView(createMusicTitle(key));
            if (layout.getChildCount() > 20) {
                row.setVisibility(View.INVISIBLE);
            }
            addMusicRow(row, rowParams);
            // 共通変数設定
            if (!displayMusicNames.contains(key)) { displayMusicNames.add(key); }
            musicItem.setRow(row);
        }

        // 余白追加
        TextView whiteSpace = new TextView(activity);
        whiteSpace.setHeight(350);
        addWhiteSpaceView(whiteSpace);
    }

    private void addMusicRow(final LinearLayout row, final LayoutParams rowParams) {
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

    private void addWhiteSpaceView(final TextView whiteSpace) {
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
        removeAllMusicRow();
        displayMusicNames.clear();
        for (Map.Entry<String, MusicItem> musicItemMap : musicItems.entrySet()) {
            MusicItem musicItem = musicItemMap.getValue();
            musicItem.setRow(null); // この処理いる？
        }
    }

    private void removeAllMusicRow() {
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
    private LinearLayout createMusicRow() {
        LinearLayout row = new LinearLayout(activity);
        row.setOrientation(LinearLayout.VERTICAL);
        row.setBackgroundResource(R.drawable.normal_row);
        row.setMinimumHeight(150);
        // 楽曲クリック時
        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    hideKeyBoard();
                    activity.musicSeekBar.visible();
                    String key = getMusicKey((LinearLayout) v);
                    selectMusic(key);
                } catch (Exception ex) {
                    activity.musicField.outErrorMessage(ex);
                }
            }
        });
        // 楽曲長押し時
        row.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                hideKeyBoard();
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
        hideTagInfo();
        SELECT_MUSIC = key;
        showTagInfo();
    }

    // 楽曲非選択処理
    public void unselectedMusic() {
        hideTagInfo();
        SELECT_MUSIC = "";
    }

    // タグ情報表示
    private void showTagInfo() throws Exception {
        MusicItem musicItem = musicItems.get(SELECT_MUSIC);
        if (musicItem == null) {
            return;
        }
        LinearLayout row = musicItems.get(SELECT_MUSIC).getRow();
        TagField tagField = new TagField(activity);
        addTagView(row, tagField.createTagField(SELECT_MUSIC), tagField.createTagFieldParams());
    }

    private void addTagView(final LinearLayout row, final RelativeLayout tagFieldLayout, final LinearLayout.LayoutParams tagFieldParams) {
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                row.addView(tagFieldLayout, tagFieldParams);
            }
        });
    }

    // タグ情報非表示
    private void hideTagInfo() {
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
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                // 楽曲キー、タイトル以外を削除
                row.removeViewAt(2);
            }
        });
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
                    changeMusicVisibility(row, View.INVISIBLE);
                } else {
                    changeMusicVisibility(row, View.VISIBLE);
                }
                displayMusicNames.add(key);
            } else {
                changeMusicVisibility(row, View.GONE);
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

    // エラーメッセージ表示
    public void outErrorMessage(final Exception ex) {
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                TextView text = (TextView) activity.findViewById(R.id.helloWorld);
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                pw.flush();
                text.setText(sw.toString());
                text.setVisibility(View.VISIBLE);
            }
        });
    }

    public void outErrorMessage(final String str) {
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                TextView text = (TextView) activity.findViewById(R.id.helloWorld);
                text.setText(str);
                text.setVisibility(View.VISIBLE);
            }
        });
    }

    public void addErrorMessage(final String str) {
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                TextView text = (TextView) activity.findViewById(R.id.helloWorld);
                text.setText(text.getText() + str);
                text.setVisibility(View.VISIBLE);
            }
        });
    }
}
