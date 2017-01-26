package com.keepingrack.tagmusicplayer.layout.progressDialog;

import android.app.ProgressDialog;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class InitProgressDialog extends CommonProgressDialog {

    public static final String MESSAGE = "Loading...";

    public InitProgressDialog() {
        super(activity);

        this.setMessage(MESSAGE);
        this.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        this.setCancelable(false);
    }
}
