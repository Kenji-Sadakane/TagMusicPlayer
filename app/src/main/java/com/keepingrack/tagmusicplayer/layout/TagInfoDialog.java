package com.keepingrack.tagmusicplayer.layout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.R;
import com.keepingrack.tagmusicplayer.Variable;

import java.util.ArrayList;
import java.util.List;

import static com.keepingrack.tagmusicplayer.external.file.MusicFile.TAG_NOTHING_LIST;
import static com.keepingrack.tagmusicplayer.layout.musicField.MusicLinearLayout.checkedMusicKeyList;

public class TagInfoDialog {

    private MainActivity activity;

    public TagInfoDialog(MainActivity _activity) {
        this.activity = _activity;
    }

    public void showDialog() {
        final View dialogBody = createDialogBody();
        final AlertDialog.Builder alertDlg = new AlertDialog.Builder(activity);
        alertDlg.setTitle(createDialogTitle());
        alertDlg.setView(dialogBody);
        alertDlg.setPositiveButton(
                "保存",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OK ボタンクリック処理
                        try {
                            List<String> tags = new ArrayList<>();
                            LinearLayout tagTexts = (LinearLayout) ((ScrollView) dialogBody).getChildAt(0);
                            for (int i = 0; i < tagTexts.getChildCount(); i++) {
                                LinearLayout row = (LinearLayout) tagTexts.getChildAt(i);
                                String tag = ((EditText) row.getChildAt(1)).getText().toString();
                                if (!tag.isEmpty() && !tags.contains(tag)) {
                                    tags.add(tag);
                                    Variable.addTagKinds(tag);
                                }
                            }
                            if (tags.isEmpty()) { tags = TAG_NOTHING_LIST; }
                            for (int i = 0; i < checkedMusicKeyList.size(); i++) {
                                String key = checkedMusicKeyList.get(i);
                                Variable.setMusicTags(key, tags);
                                activity.musicTagsLogic.update(key);
                                activity.musicLinearLayout.deselectMusic(key);
                                if (i == checkedMusicKeyList.size() - 1) {
                                    activity.musicLinearLayout.selectMusicAndDeselectOldMusic(key);
                                }
                            }
                        } catch (Exception ex) {
                            activity.msgView.outErrorMessage(ex);
                        }
                    }
                });
        alertDlg.setNegativeButton(
                "キャンセル",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel ボタンクリック処理
                    }
                });
        alertDlg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                activity.findViewById(R.id.bottomField).setVisibility(View.VISIBLE);
            }
        });
        // 表示
        alertDlg.create().show();
        // シークバー等非表示
        activity.findViewById(R.id.bottomField).setVisibility(View.GONE);
    }

    private String createDialogTitle() {
        if (checkedMusicKeyList.size() == 1) {
            return Variable.getMusicTitle(checkedMusicKeyList.get(0));
        } else {
            return "タグ編集(" + checkedMusicKeyList.size() + "曲)";
        }
    }

    private View createDialogBody() {
        ScrollView dialogBody = new ScrollView(activity);
        LinearLayout tagTexts = new LinearLayout(activity);
        tagTexts.setOrientation(LinearLayout.VERTICAL);
        String key = checkedMusicKeyList.get(checkedMusicKeyList.size() - 1);
        if (Variable.getMusicTags(key) == null || Variable.getMusicTags(key).isEmpty()) {
            tagTexts.addView(createDialogRow(""));
        } else {
            for (String tag : Variable.getMusicTags(key)) {
                tagTexts.addView(createDialogRow(tag));
            }
        }
        dialogBody.addView(tagTexts);
        return dialogBody;
    }

    private View createDialogRow(String tag) {
        LinearLayout row = new LinearLayout(activity);
        row.setHorizontalGravity(LinearLayout.HORIZONTAL);
        LayoutParams rowParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(10,0,50,0); // 左, 上, 右, 下
        row.setLayoutParams(rowParams);
        // ＋ボタン
        Button bt = new Button(activity);
        bt.setText("＋");
        bt.setBackgroundColor(Color.rgb(255, 255, 255));
        bt.setPadding(0,0,0,0);
//        bt.setHeight(50);
//        bt.setWidth(50);
        LayoutParams buttonParams = new LayoutParams(120, 120);
        buttonParams.setMargins(0,0,0,0); // 左, 上, 右, 下
        bt.setLayoutParams(buttonParams);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDialogRow((Button) v);
            }
        });
        row.addView(bt);
        // タグテキスト
        EditText editText = new EditText(activity);
        editText.setText(tag);
        editText.setMinWidth(activity.getWindowManager().getDefaultDisplay().getWidth()); // 大きめに設定して問題なし？
        LayoutParams textParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        textParams.setMargins(0,0,0,0); // 左, 上, 右, 下
        editText.setLayoutParams(textParams);
        editText.setPadding(20,0,0,0);
        row.addView(editText);
        return row;
    }

    private void addDialogRow(Button bt) {
        LinearLayout row = (LinearLayout) bt.getParent();
        LinearLayout dialogBody = (LinearLayout) row.getParent();
        dialogBody.addView(createDialogRow(""), getLinearLayoutChildIndex(dialogBody, row) + 1);
    }

    private Integer getLinearLayoutChildIndex(LinearLayout layout, View child) {
        Integer index = 0;
        for (int i = 0; i < layout.getChildCount(); i++) {
            if (child.equals(layout.getChildAt(i))) {
                index = i;
                break;
            }
        }
        return index;
    }
}
