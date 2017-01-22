package com.keepingrack.tagmusicplayer.layout.musicField;

import android.view.View;
import android.widget.TextView;

import com.keepingrack.tagmusicplayer.MainActivity;
import com.keepingrack.tagmusicplayer.R;

import java.io.PrintWriter;
import java.io.StringWriter;

public class MsgField {
    private MainActivity activity;

    public MsgField(MainActivity _activity) {
        this.activity = _activity;
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
