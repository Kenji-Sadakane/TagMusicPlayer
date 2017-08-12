package com.keepingrack.tagmusicplayer.layout.topField;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import com.keepingrack.tagmusicplayer.Variable;

import static com.keepingrack.tagmusicplayer.MainActivity.DISPLAY_WIDTH;
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

    /**
     * 検索処理
     * 1.現在のスクロール位置から一定値以上離れている楽曲を非表示化
     * 2.アニメーションで全楽曲を-Y方向に移動しユーザから見えなくする
     * 3.全楽曲をX方向に移動する(次処理で楽曲リストを操作してもユーザから見えない状態を維持するため)
     * 4.検索条件に応じて楽曲リストの表示／削除切り替え
     * 5.新楽曲リストの上位数曲のみ表示
     * 6.全楽曲のX位置を戻す
     * 7.アニメーションで全楽曲をY方向に移動しユーザから見えるようにする
     * 8.新楽曲リストを全て表示化
     *
     * (用語 表示:VISIBLE、非表示:INVISIBLE、削除:GONE)
     */
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
                        activity.musicLinearLayout.addX(DISPLAY_WIDTH);
                        activity.musicLinearLayout.changeMusicList();
                        activity.relateTagLogic.updateRelateTags();
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
        activity.grayPanel.screenLock();
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
                activity.musicLinearLayout.addX(-DISPLAY_WIDTH);
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                activity.musicScrollView.showMusicRow();
                activity.grayPanel.screenLockRelease();
            }
            @Override
            public void onAnimationCancel(Animator animation) {}
            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        anm.start();
    }
}
