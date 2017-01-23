package com.keepingrack.tagmusicplayer.layout.topField;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.keepingrack.tagmusicplayer.R;

import java.util.ArrayList;
import java.util.List;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.MainActivity.displayMusicNames;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;
import static com.keepingrack.tagmusicplayer.MainActivity.tagKinds;
import static com.keepingrack.tagmusicplayer.layout.topField.SearchSwitch.SEARCH_TYPE;
import static com.keepingrack.tagmusicplayer.layout.musicField.MusicLinearLayout.SELECT_MUSIC;

public class KeyWordEditText extends AutoCompleteTextView {

    public static final String NO_TAG_WORD ="タグなし";
    public boolean focusOn = false;

    public KeyWordEditText(Context context, AttributeSet attr) {
        super(context, attr);

        // リスナー
        this.setOnFocusChangeListener(getOnFocusChangeListener());
        this.addTextChangedListener(getTextChangeListener());
        this.setOnItemClickListener(getAutoCompleteSelectedListener());
    }

    // フォーカス変更時リスナー
    public View.OnFocusChangeListener getOnFocusChangeListener() {
        return new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    // 受け取った時
                    activity.musicSeekBar.invisible(0);
//                    ((Button) activity.findViewById(R.id.clearButton)).setVisibility(View.VISIBLE);
                    focusOn = true;
                } else {
//                    ((Button) activity.findViewById(R.id.clearButton)).setVisibility(View.GONE);
                    focusOn = false;
                }
            }
        };
    }

    // キーワード変更時リスナー
    private TextWatcher getTextChangeListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                showAutoComplete();
            }
        };
    }
    // オートコンプリート表示
    private void showAutoComplete() {
        switch (SEARCH_TYPE) {
            case TITLE:
                break;
            case TAG:
                displayAutoComplete(createAutoCompleteList());
                break;
        }
    }
    private List<String> createAutoCompleteList() {
        String keyWord = this.getText().toString();
        List<String> autoCompleteList = new ArrayList<>();
        for (String tag : tagKinds) {
            if (tag.toLowerCase().startsWith(keyWord.toLowerCase())) {
                autoCompleteList.add(tag);
            }
        }
        return autoCompleteList;
    }
    private void displayAutoComplete(List<String> autoCompleteList) {
        if (0 < autoCompleteList.size()) {
            final AutoCompleteTextView autoCompleteTextView = this;
            autoCompleteTextView.setThreshold(2);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.auto_complete_item, autoCompleteList);
            autoCompleteTextView.post(new Runnable() {
                @Override
                public void run() {
                    autoCompleteTextView.setAdapter(adapter);
                }
            });
        }
    }

    // autoComplete候補選択時リスナー
    private AdapterView.OnItemClickListener getAutoCompleteSelectedListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                execSearch();
            }
        };
    }

    // 検索処理実施
    public void execSearch() {
        try {
            ObjectAnimator anm = activity.musicLinearLayout.getHideAnimation();
            anm.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    try {
                        activity.hideKeyBoard();
                        activity.relateTagField.removeRelateTagField();
                        activity.relateTagField.hideRelateTagField();
                        activity.musicLinearLayout.changeMusicList();
                        activity.relateTagField.updateRelateTags();
                        if (!displayMusicNames.contains(SELECT_MUSIC)) {
                            activity.musicLinearLayout.deselectMusic(SELECT_MUSIC);
                        }
                        ObjectAnimator anm = activity.musicLinearLayout.getShowAnimation();
                        anm.start();
                    } catch (Exception ex) {
                        activity.msgView.outErrorMessage(ex);
                    }
                }
                @Override
                public void onAnimationCancel(Animator animation) {
                }
                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            anm.start();
        } catch (Exception ex) {
            activity.msgView.outErrorMessage(ex);
        }
    }

    // 楽曲とキーワードを比較し表示是非を判定
    public boolean checkKeyWordMatch(String key) {
        boolean chkResult = false;
        String keyWord = this.getText().toString();
        if (keyWord == null || keyWord.length() == 0) {
            chkResult = true;
        } else {
            switch (SEARCH_TYPE) {
                case TITLE:
                    String musicName = musicItems.get(key).getTitle();
                    if (musicName.toLowerCase().contains(keyWord.toLowerCase())) {
                        // キーワードと楽曲名が部分一致
                        chkResult = true;
                    }
                    break;
                case TAG:
                    if (musicItems.get(key).getTags() == null || musicItems.get(key).getTags().isEmpty()) {
                        if (NO_TAG_WORD.equals(keyWord)) {
                            // 楽曲に設定タグ無し、キーワードが「タグなし」
                            chkResult = true;
                            break;
                        }
                    } else {
                        if (!tagKinds.contains(keyWord)) {
                            chkResult = false;
                            break;
                        } else {
                            for (String tag : musicItems.get(key).getTags()) {
                                if (tag.equals(keyWord)) {
                                    // 楽曲に設定タグあり、キーワードと設定タグが完全一致
                                    chkResult = true;
                                    break;
                                }
                            }
                        }
                    }
                    break;
            }
        }
        return chkResult;
    }
}