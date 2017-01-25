package com.keepingrack.tagmusicplayer.layout.topField;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.keepingrack.tagmusicplayer.Variable;

import static com.keepingrack.tagmusicplayer.MainActivity.activity;
import static com.keepingrack.tagmusicplayer.layout.musicField.MusicLinearLayout.SELECT_MUSIC;

public class SearchButton extends Button {
    public SearchButton(Context context, AttributeSet attr) {
        super(context, attr);

        // リスナー
        this.setOnClickListener(getOnClickListener());
    }

    private View.OnClickListener getOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                execSearch();
            }
        };
    }

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
                        activity.relateTagLayout.removeRelateTagField();
                        activity.relateTagLogic.hideRelateTagField();
                        activity.musicLinearLayout.changeMusicList();
                        activity.relateTagLogic.updateRelateTags();
                        if (!Variable.getDisplayMusicNames().contains(SELECT_MUSIC)) {
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
}
