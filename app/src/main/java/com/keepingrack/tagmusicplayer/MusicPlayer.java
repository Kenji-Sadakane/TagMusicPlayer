package com.keepingrack.tagmusicplayer;

import android.media.MediaPlayer;
import android.widget.TextView;
import android.widget.ToggleButton;

import static com.keepingrack.tagmusicplayer.MainActivity.displayMusicNames;
import static com.keepingrack.tagmusicplayer.MainActivity.musicItems;
import static com.keepingrack.tagmusicplayer.MainActivity.SELECT_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.PLAYING_MUSIC;
import static com.keepingrack.tagmusicplayer.MainActivity.mp;

public class MusicPlayer {

    /**
     * プレイヤーの挙動
     * PAUSE:一時停止
     * START:再生開始
     * STOP:再生終了
     * RESUME:一時停止解除
     * RESTART:0秒から再度再生
     * GO_PREV:前の曲を再生
     * GP_NEXT:次の曲再生
     * NOTHING:なし
     */
    public enum ACTION {PAUSE, START, STOP, RESUME, RESTART, GO_PREV, GO_NEXT, NOTHING};

    public static final int GO_PREV_TRACK_TIME = 3000;

    private MainActivity activity;

    public MusicPlayer(MainActivity _activity) {
        this.activity = _activity;
    }

    // リスナー(楽曲終了時処理)
    private void setListener() {
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                onCompletionProcess();
            }
        });
        mp.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (activity.musicPlayerButton.isLoopStatusONE()) {
                    activity.musicPlayerButton.clickLoopButton();
                }
                onCompletionProcess();
                return true;
            }
        });
    }

    public void onCompletionProcess() {
        try {
            switch (getActionOnCompletion()) {
                case STOP:
                    activity.stopMusic();
                    break;
                case GO_NEXT:
                    Integer nextTrackNo = getNextTrackNo();
                    if (nextTrackNo == null) nextTrackNo = 0;
                    activity.playMusic(displayMusicNames.get(nextTrackNo));
                    break;
                case RESTART:
                    startMusicPlayer(PLAYING_MUSIC);
                    activity.musicField.scrollMusicView();
                    break;
            }
        } catch (Exception ex) {
            activity.musicField.outErrorMessage(ex);
        }
    }

    // 再生/一時停止ボタン押下時の挙動判定
    public ACTION getActionOnPlayClicked() {
        ACTION action;
        ToggleButton playButton = (ToggleButton) activity.findViewById(R.id.playButton);
            if (!playButton.isChecked()) {
                action = ACTION.PAUSE;
            } else {
                String key = getPlayMusicKeyOnPlayClicked();
                if (key.isEmpty()) {
                    action = ACTION.NOTHING;
                } else if (PLAYING_MUSIC.equals(key)) {
                    action = ACTION.RESUME;
                } else {
                    action = ACTION.START;
                }
            }
        return action;
    }

    // 再生ボタン押下時の再生曲キー取得
    public String getPlayMusicKeyOnPlayClicked() {
        String key = "";
        if (!displayMusicNames.isEmpty()) {
            if (!SELECT_MUSIC.isEmpty()) {
                key = SELECT_MUSIC;
            } else {
                key = displayMusicNames.get(0);
            }
        }
        return key;
    }

    // 前ボタン押下時の挙動判定
    public ACTION getActionOnPrevClicked() {
        ACTION action = ACTION.STOP;
        ToggleButton playButton = (ToggleButton) activity.findViewById(R.id.playButton);
        if (playButton.isChecked()) {
            if (mp.getCurrentPosition() > GO_PREV_TRACK_TIME) {
                action = ACTION.RESTART;
            } else if (!displayMusicNames.isEmpty()) {
                action = ACTION.GO_PREV;
            }
        }
        return action;
    }

    // 次ボタン押下時の挙動判定
    public ACTION getActionOnNextClicked() {
        ACTION action = ACTION.STOP;
        ToggleButton playButton = (ToggleButton) activity.findViewById(R.id.playButton);
        if (playButton.isChecked() && !displayMusicNames.isEmpty()) {
            action = ACTION.GO_NEXT;
        }
        return action;
    }

    public void startMusicPlayer(String key) throws Exception {
        mp.setOnCompletionListener(null);
        mp.reset();
        mp.setDataSource(musicItems.get(key).getAbsolutePath());
        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mp.start();
                setListener();
                activity.musicSeekBar.setTotalTime(mp.getDuration());
                activity.musicSeekBar.setPlayTime(0);
            }
        });
        mp.prepareAsync();
        PLAYING_MUSIC = key;
    }

    public void stopMusicPlayer() throws Exception {
        mp.stop();
        PLAYING_MUSIC = "";
    }

    public ACTION getActionOnCompletion() {
        ACTION action;
        if (activity.musicPlayerButton.isLoopStatusONE()) {
            action = ACTION.RESTART;
        } else {
            if (displayMusicNames.isEmpty()) {
                action = ACTION.STOP;
            } else {
                if (getNextTrackNo() == null && activity.musicPlayerButton.isLoopStatusOFF()) {
                    action = ACTION.STOP;
                } else {
                    action = ACTION.GO_NEXT;
                }
            }
        }
        return action;
    }

    public Integer getPrevTrackNo() {
        Integer prevTrackNo = null;
        if (!displayMusicNames.isEmpty()) {
            if(!displayMusicNames.contains(PLAYING_MUSIC)) {
                prevTrackNo = 0;
            } else {
                for (int i = 0; i < displayMusicNames.size(); i++) {
                    if (displayMusicNames.get(i).equals(PLAYING_MUSIC)) {
                        prevTrackNo = i - 1;
                        break;
                    }
                }
            }
            if (prevTrackNo < 0) {
                prevTrackNo = null;
            }
        }
        return prevTrackNo;
    }

    public Integer getNextTrackNo() {
        Integer nextTrackNo = null;
        if (!displayMusicNames.isEmpty()) {
            if(!displayMusicNames.contains(PLAYING_MUSIC)) {
                nextTrackNo = 0;
            } else {
                for (int i = 0; i < displayMusicNames.size(); i++) {
                    if (displayMusicNames.get(i).equals(PLAYING_MUSIC)) {
                        nextTrackNo = i + 1;
                        break;
                    }
                }
            }
            if (nextTrackNo > displayMusicNames.size() - 1) {
                nextTrackNo = null;
            }
        }
        return nextTrackNo;
    }

    public void showTrackNo() {
        TextView currentTrackText = (TextView) activity.findViewById(R.id.currentTrack);
        int currentTrackNo = 0;
        if (displayMusicNames != null && displayMusicNames.contains(PLAYING_MUSIC)) {
            currentTrackNo = displayMusicNames.indexOf(PLAYING_MUSIC) + 1;
        }
        showTrackNo(currentTrackText, currentTrackNo);
    }

    public void showTrackNo(final TextView currentTrackText, final int currentTrackNo) {
        currentTrackText.post(new Runnable() {
            @Override
            public void run() {
                currentTrackText.setText(currentTrackNo + " / " + displayMusicNames.size());
            }
        });
    }
}
