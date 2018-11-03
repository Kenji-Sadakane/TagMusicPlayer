package com.keepingrack.tagmusicplayer.layout.progressDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class CommonProgressDialog extends ProgressDialog {
    public CommonProgressDialog(Context context) {
        super(context);
    }

    public void showWithScreenLock() {
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                activity.grayPanel.setVisibility(View.VISIBLE);
            }
        });
        show();
    }

    public void endLoading() {
        final ProgressDialog progressDialog = this;
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                activity.grayPanel.setVisibility(View.GONE);
                progressDialog.dismiss();
            }
        });
    }
}
