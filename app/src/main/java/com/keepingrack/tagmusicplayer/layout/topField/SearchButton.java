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

    // 検索処理
    public void execSearch() {
        try {
            preSearch();
            ObjectAnimator anm = activity.musicLinearLayout.getHideAnimation();
            anm.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }
                @Override
                public void onAnimationEnd(Animator animation) {
                    try {
                        activity.musicScrollView.hideMusicRow();
                        activity.musicLinearLayout.changeMusicList();
                        activity.relateTagLogic.updateRelateTags();
                        if (!Variable.getDisplayMusicNames().contains(SELECT_MUSIC)) {
                            activity.musicLinearLayout.deselectMusic(SELECT_MUSIC);
                        }
                        postSearch();
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

    // 検索前処理
    private void preSearch() {
        activity.hideKeyBoard();
        activity.relateTagLayout.removeRelateTagField();
        activity.relateTagLogic.hideRelateTagField();
        activity.musicScrollView.hideTooUnderMusicRow();
    }

    // 検索後処理
    private void postSearch() {
        ObjectAnimator anm = activity.musicLinearLayout.getShowAnimation();
        anm.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                activity.musicScrollView.showMusicRow(20);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                activity.musicScrollView.showMusicRow();
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        anm.start();
    }
}
