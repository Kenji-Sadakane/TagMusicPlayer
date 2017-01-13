package com.example.sada.tagmusicplayer;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.sada.tagmusicplayer.MainActivity.displayMusicNames;
import static com.example.sada.tagmusicplayer.MainActivity.musicItems;
import static com.example.sada.tagmusicplayer.MainActivity.PLAYING_MUSIC;
import static com.example.sada.tagmusicplayer.MainActivity.SELECT_MUSIC;

public class MusicField {

    private MainActivity activity;

    public MusicField(MainActivity _activity) {
        this.activity = _activity;
    }

    // リスナー
    public void setListener() {
        setOnScrollChangeListener();
        setOnTouchListener();
    }

    // スクロール時処理
    private void setOnScrollChangeListener() {
        final ScrollView scrollView = ((ScrollView) activity.findViewById(R.id.scrollView));
        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY();
                for (Map.Entry<String, MusicItem> musicItemMap : musicItems.entrySet()) {
                    LinearLayout musicRow = musicItemMap.getValue().getRow();
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
        for (Map.Entry<String, MusicItem> musicItemMap : musicItems.entrySet()) {
            String key = musicItemMap.getKey();
            MusicItem musicItem = musicItemMap.getValue();
            // 楽曲フィールド作成
            LinearLayout row = createMusicRow();
            LayoutParams rowParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); // width, height
            row.addView(createKeyText(key));
            row.addView(createMusicTitle(key));
            if (layout.getChildCount() > 20) {
                row.setVisibility(View.INVISIBLE);
            }
            layout.addView(row, rowParams);
            // 共通変数設定
            if (!displayMusicNames.contains(key)) { displayMusicNames.add(key); }
            musicItem.setRow(row);
        }

        // 余白追加
        TextView whiteSpace = new TextView(activity);
        whiteSpace.setHeight(350);
        layout.addView(whiteSpace);
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

    // スクロールビュー内コンテンツをクリア
    private void clearContents() {
        LinearLayout layout = (LinearLayout) activity.findViewById(R.id.linearLayout);
        if (layout.getChildCount() > 0) {
            layout.removeAllViews();
        }
        displayMusicNames.clear();
        for (Map.Entry<String, MusicItem> musicItemMap : musicItems.entrySet()) {
            MusicItem musicItem = musicItemMap.getValue();
            musicItem.setRow(null);
        }
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
        if (!SELECT_MUSIC.isEmpty()) {
            hideTagInfo();
        }
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
        if (SELECT_MUSIC.isEmpty()) {
            return;
        }
        List<String> tags = musicItems.get(SELECT_MUSIC).getTags();
        LinearLayout row = musicItems.get(SELECT_MUSIC).getRow();
        LinearLayout tagField = createTagField(row);
        int tagLengthByField = 0;
        if (tags != null && !tags.isEmpty()) {
            for (String tag : tags) {
                int tagLength = tag.getBytes("Shift_JIS").length;
                if (tagField.getChildCount() > 0 && (tagLengthByField + tagLength) > 40) {
                    tagField = createTagField(row);
                    tagLengthByField = tagLength;
                } else {
                    tagLengthByField += tagLength;
                }
                TextView tagText = createTagTextView(tag);
                tagField.addView(tagText);
            }
        }
    }

    // タグ(全て)を表示するLinearLayout生成
    private LinearLayout createTagField(LinearLayout row) {
        LinearLayout tagField = new LinearLayout(activity);
        tagField.setOrientation(LinearLayout.HORIZONTAL);
        LayoutParams tagFieldParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        tagFieldParams.setMargins(15, 0, 15, 15); // 左, 上, 右, 下
        row.addView(tagField, tagFieldParams);
        return tagField;
    }

    // タグ(1個)のTextView生成
    private TextView createTagTextView(String tag) {
        TextView tagText = new TextView(activity);
        tagText.setClickable(true);
        tagText.setText(tag);
        tagText.setPadding(5, 0, 5, 0); // 左, 上, 右, 下
        tagText.setBackgroundResource(R.drawable.tag_item);
        LinearLayout.LayoutParams tagTextParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tagTextParams.setMargins(0, 0, 10, 0); // 左, 上, 右, 下
        tagText.setLayoutParams(tagTextParams);
        // タグクリック時
        tagText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = ((TextView) v).getText().toString();
                ((Switch) activity.findViewById(R.id.switch1)).setChecked(true);
                ((AutoCompleteTextView) activity.findViewById(R.id.editText)).setText(tag);
                activity.keyWord.execSearch();
            }
        });
        return tagText;
    }

    // タグ情報非表示
    private void hideTagInfo() {
        if (SELECT_MUSIC.isEmpty()) {
            return;
        }
        LinearLayout selectedRow = musicItems.get(SELECT_MUSIC).getRow();
        if (selectedRow != null) {
            int tagFieldCount = selectedRow.getChildCount() - 2;
            if (tagFieldCount > 0) {
                for (int i = 0; i < tagFieldCount; i++) {
                    // 楽曲キー、タイトル以外を削除
                    selectedRow.removeViewAt(2);
                }
            }
        }
    }

    // 再生楽曲の背景色を戻す
    public void resetMusicRowBackGround() {
        if (!PLAYING_MUSIC.isEmpty()) {
            musicItems.get(PLAYING_MUSIC).getRow().setBackgroundResource(R.drawable.normal_row);
        }
    }

    // 再生楽曲の背景色を変える
    public void setMusicRowBackGround() {
        musicItems.get(PLAYING_MUSIC).getRow().setBackgroundResource(R.drawable.selected_row);
    }

    // 楽曲の表示切り替え
    public void changeMusicVisibility(final LinearLayout layout, final int visibility) {
        layout.post(new Runnable() {
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
        for (Map.Entry<String, MusicItem> musicItemMap : musicItems.entrySet()) {
            String key = musicItemMap.getKey();
            MusicItem musicItem = musicItemMap.getValue();
            if (activity.keyWord.checkKeyWordMatch(key) && activity.relateTagField.chkRelateTagState(key)) {
                if (displayMusicNames.size() > 20) {
                    changeMusicVisibility(musicItem.getRow(), View.INVISIBLE);
                } else {
                    changeMusicVisibility(musicItem.getRow(), View.VISIBLE);
                }
                displayMusicNames.add(key);
            } else {
                changeMusicVisibility(musicItem.getRow(), View.GONE);
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
        TextView text = (TextView) activity.findViewById(R.id.helloWorld);
        text.post(new Runnable() {
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

    public void outErrorMessage(String str) {
        TextView text = (TextView) activity.findViewById(R.id.helloWorld);
        text.setText(str);
        text.setVisibility(View.VISIBLE);
    }
}
