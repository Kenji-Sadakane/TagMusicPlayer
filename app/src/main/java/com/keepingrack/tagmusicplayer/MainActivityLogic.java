package com.keepingrack.tagmusicplayer;

import com.keepingrack.tagmusicplayer.layout.progressDialog.InitProgressDialog;
import com.keepingrack.tagmusicplayer.layout.progressDialog.UpdateProgressDialog;

import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.activity;

public class MainActivityLogic {

    // 初期表示時処理
    public static void initProcess() {
        final InitProgressDialog progressDialog = new InitProgressDialog();
        progressDialog.showWithScreenLock();
        startInitProcessThread(progressDialog);
        startPlayTimeUpdateThread();
        startBottomFieldShowThread();
    }
    private static void startInitProcessThread(final InitProgressDialog progressDialog) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    activity.musicTagsLogic.selectAndReflectTags();
                    activity.musicLinearLayout.createContents();
                    activity.musicLinearLayout.changeMusicList();
                } catch (Exception ex) {
                    activity.msgView.outErrorMessage(ex);
                } finally {
                    progressDialog.endLoading();
                }
            }
        }).start();
    }
    private static void startPlayTimeUpdateThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // 再生時間更新
                    if (!PLAYING_MUSIC.isEmpty()) {
                        activity.musicSeekBar.setPlayTime();
                    }
                    sleep(500);
                }
            }
        }).start();
    }
    private static void startBottomFieldShowThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    // シークバー＆ボタン表示
                    if (!activity.musicSeekBar.visible && !activity.keyWordEditText.focusOn) {
                        activity.musicSeekBar.hiddenTime++;
                        if (activity.musicSeekBar.hiddenTime > 2) {
                            activity.musicSeekBar.visible();
                        }
                    }
                    sleep(1000);
                }
            }
        }).start();
    }

    // 更新押下時処理
    public static void updateProcess() {
        try {
            if (!PLAYING_MUSIC.isEmpty()) {
                activity.stopMusic();
            }
            final UpdateProgressDialog progressDialog = new UpdateProgressDialog();
            progressDialog.showWithScreenLock();
            activity.keyWordEditText.setText("");
            activity.relateTagLogic.initializeTagField();
            startUpdateProcessThread(progressDialog);
        } catch (Exception ex) {
            activity.msgView.outErrorMessage(ex);
        }
    }
    private static void startUpdateProcessThread(final UpdateProgressDialog progressDialog) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // 楽曲ファイル捜索
                    progressDialog.setMessageAsync(UpdateProgressDialog.MESSAGES[0]);
                    activity.musicFile.readMusicFilesAndDatabase();
                    // DB更新
                    progressDialog.setMessageAsync(UpdateProgressDialog.MESSAGES[1]);
                    activity.musicTagsLogic.deleteAll();
                    activity.musicTagsLogic.insertAll();
                    // 楽曲リスト(画面部品)作成
                    progressDialog.setMessageAsync(UpdateProgressDialog.MESSAGES[2]);
                    activity.musicLinearLayout.createContents();
                    activity.musicPlayer.showTrackNo();
                } catch (Exception ex) {
                    activity.msgView.outErrorMessage(ex);
                } finally {
                    progressDialog.endLoading();
                }
            }
        }).start();
    }

    private static void sleep(int millisecond) {
        try {
            Thread.sleep(millisecond);
        } catch (Exception ex) {
            activity.msgView.outErrorMessage(ex);
        }
    }
}
