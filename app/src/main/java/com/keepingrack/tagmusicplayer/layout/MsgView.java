package com.keepingrack.tagmusicplayer.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;

/**
 * エラーメッセージ表示ビュー(デバッグ用)
 */
public class MsgView extends TextView {
    public MsgView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    // エラーメッセージ表示
    public void outErrorMessage(final Exception ex) {
        final MsgView msgView = this;
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                ex.printStackTrace(pw);
                pw.flush();
                msgView.setText(sw.toString());
                msgView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void outErrorMessage(final String str) {
        final MsgView msgView = this;
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                msgView.setText(str);
                msgView.setVisibility(View.VISIBLE);
            }
        });
    }

    public void addErrorMessage(final String str) {
        final MsgView msgView = this;
        activity.handler.post(new Runnable() {
            @Override
            public void run() {
                msgView.setText(msgView.getText() + str);
                msgView.setVisibility(View.VISIBLE);
            }
        });
    }
}
