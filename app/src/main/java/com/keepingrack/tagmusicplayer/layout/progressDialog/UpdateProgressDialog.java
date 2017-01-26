package com.keepingrack.tagmusicplayer.layout.progressDialog;

import android.app.ProgressDialog;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class UpdateProgressDialog extends CommonProgressDialog {

    public static final String TITLE = "楽曲リスト更新";
    public static final String[] MESSAGES = {
            "楽曲ファイル検索(1/3)",
            "データベース更新(2/3)",
            "画面レイアウト更新(3/3)"
    };

    public UpdateProgressDialog() {
        super(activity);

        this.setTitle(TITLE);
        this.setMessage("");
        this.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.setCancelable(false);
    }

    public void setMessageAsync(final String msg) {
        final ProgressDialog progressDialog = this;
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                progressDialog.setMessage(msg);
            }
        });
    }
}
