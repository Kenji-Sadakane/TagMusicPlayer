package com.keepingrack.tagmusicplayer;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;

import com.keepingrack.tagmusicplayer.bean.MusicItem;
import com.keepingrack.tagmusicplayer.bean.RelateTag;
import com.keepingrack.tagmusicplayer.external.db.logic.MusicTagsLogic;
import com.keepingrack.tagmusicplayer.external.file.MusicFile;
import com.keepingrack.tagmusicplayer.layout.GrayPanel;
import com.keepingrack.tagmusicplayer.layout.KeyWord;
import com.keepingrack.tagmusicplayer.layout.MusicField;
import com.keepingrack.tagmusicplayer.layout.MusicPlayerButton;
import com.keepingrack.tagmusicplayer.layout.MusicSeekBar;
import com.keepingrack.tagmusicplayer.layout.RelateTagField;
import com.keepingrack.tagmusicplayer.layout.SearchSwitch;
import com.keepingrack.tagmusicplayer.layout.TagInfoDialog;
import com.keepingrack.tagmusicplayer.layout.MsgView;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicLinearLayout;
import com.keepingrack.tagmusicplayer.layout.musicField.MusicScrollView;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, Runnable {

//    private static final int REQUEST_EXTERNAL_STORAGE = 1;
//    private static String[] PERMISSIONS_STORAGE = {
//            Manifest.permission.READ_EXTERNAL_STORAGE,
//            Manifest.permission.WRITE_EXTERNAL_STORAGE
//    };
//    private static int CURRENT_PERMISSION;

    public static final String BASE_DIR = "/storage/sdcard1/PRIVATE/SHARP/CM/MUSIC";

    public static int DISPLAY_WIDTH;
    public static Map<String, MusicItem> musicItems = new ConcurrentHashMap<>();
    public static Set<String> tagKinds = new CopyOnWriteArraySet<>();
    public static List<String> musicKeys = new CopyOnWriteArrayList<>();
    public static List<String> displayMusicNames = new CopyOnWriteArrayList<>();
    public static List<RelateTag> relateTags = new CopyOnWriteArrayList<>();
    public static String PLAYING_MUSIC = "";
    public static MediaPlayer mp = new MediaPlayer();
    public static MainActivity activity;

    public KeyWord keyWord = new KeyWord(this);
    public GrayPanel grayPanel;
    public MsgView msgView;
    public MusicField musicField = new MusicField(this);
    private MusicFile musicFile = new MusicFile(this);
    public MusicLinearLayout musicLinearLayout;
    public MusicPlayer musicPlayer = new MusicPlayer(this);
    public MusicPlayerButton musicPlayerButton = new MusicPlayerButton(this);
    public MusicScrollView musicScrollView;
    public MusicTagsLogic musicTagsLogic = new MusicTagsLogic(this);
    public SearchSwitch searchSwitch = new SearchSwitch(this);
    public ShuffleMusicList shuffleMusicList = new ShuffleMusicList(this);
    public MusicSeekBar musicSeekBar = new MusicSeekBar(this);
    public RelateTagField relateTagField = new RelateTagField(this);
    public TagInfoDialog tagInfoDialog = new TagInfoDialog(this);
    public Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //// デフォルトコード開始
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //// デフォルトコード終了

        // 権限確認
//        verifyStoragePermissions(this);
//        if (CURRENT_PERMISSION != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }

        try {
            // タグ、楽曲表示
            final ProgressDialog progressDialog = startLoading();
            activity = this;
            grayPanel = (GrayPanel) findViewById(R.id.grayPanel);
            msgView = (MsgView) findViewById(R.id.msgView);
            musicLinearLayout = (MusicLinearLayout) findViewById(R.id.linearLayout);
            musicScrollView = (MusicScrollView) findViewById(R.id.scrollView);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        musicTagsLogic.selectAndReflectTags();
                        musicLinearLayout.createContents();
                        musicLinearLayout.changeMusicList();
                    } catch (Exception ex) {
                        msgView.outErrorMessage(ex);
                    } finally {
                        endLoading(progressDialog);
                    }
                }
            }).start();
            //
            // listener
            setListener();
            //
            Thread currentThread = new Thread(this);
            currentThread.start();
        } catch (Exception ex) {
            msgView.outErrorMessage(ex);
        }
    }

    private ProgressDialog startLoading() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((View) findViewById(R.id.grayPanel)).setVisibility(View.VISIBLE);
            }
        });
        ProgressDialog progressDialog = showProgressDialog();
        return progressDialog;
    }

    private ProgressDialog showProgressDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    private void endLoading(final ProgressDialog progressDialog) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                ((View) findViewById(R.id.grayPanel)).setVisibility(View.GONE);
                progressDialog.dismiss();
            }
        });
    }

    private void update() {
        try {
            if (!PLAYING_MUSIC.isEmpty()) {
                stopMusic();
            }
            final ProgressDialog progressDialog = startLoading();
            ((EditText) findViewById(R.id.editText)).setText("");
            relateTagField.initializeTagField();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 楽曲ファイル捜索
                        musicFile.readMusicFilesAndDatabase();
                        // DB更新
                        musicTagsLogic.deleteAll();
                        musicTagsLogic.insertAll();
                        // 楽曲リスト(画面部品)作成
                        musicLinearLayout.createContents();
                        musicPlayer.showTrackNo();
                    } catch (Exception ex) {
                        msgView.outErrorMessage(ex);
                    } finally {
                        endLoading(progressDialog);
                    }
                }
            }).start();
        } catch (Exception ex) {
            msgView.outErrorMessage(ex);
        }
    }

//    private void displayContents(boolean doSearchMusic) throws Exception {
//        measureDisplayWidth();
//        if (doSearchMusic) {
//            // 楽曲ファイル捜索
//            musicFile.readMusicFilesAndDatabase();
////            TagInfoFile tagInfoFile = new TagInfoFile();
////            tagInfoFile.readTagInfo();
//            // DB更新
//            musicTagsLogic.deleteAll();
//            musicTagsLogic.insertAll();
//        } else {
//            // DBより楽曲、タグ情報取得
//            musicTagsLogic.selectAndReflectTags();
//        }
//        // 楽曲リスト(画面部品)作成
//        musicField.createContents();
//        // キーワードに応じて表示内容切替
//        musicField.changeMusicList();
//        // 楽曲非選択化
//        musicField.unselectedMusic();
//        // 楽曲再生停止
//        if (!PLAYING_MUSIC.isEmpty()) {
//            stopMusic();
//        }
//        // トラック番号表示
//        musicPlayer.showTrackNo();
//    }

    private void setListener() {
        // テキストボックス
        keyWord.setListener();
        // シークバー
        musicSeekBar.setOnSeekBarChangeListener();
        // TAG/WORD 切り替え
        searchSwitch.setOnCheckedChangeListener();
    }

    @Override
    public void run() {
        try {
            while (true) {
                // 再生時間更新
                if (!PLAYING_MUSIC.isEmpty()) { musicSeekBar.setPlayTime(); }
                // シークバー＆ボタン表示
                if (!musicSeekBar.visible && !keyWord.focusOn) {
                    musicSeekBar.hiddenTime++;
                    if (musicSeekBar.hiddenTime > 2) {
                         musicSeekBar.visible();
                    }
                }
                Thread.sleep(1000);
            }
        } catch (InterruptedException ex) {
            //
        }
    }

    // 関連タグ表示切り替え時
    public void onRelateTagChangeLinkClicked(View v) {
        relateTagField.onRelateTagChangeLinkClicked();
    }

    // shuffle 切り替え時
    public void onShuffleClicked(View v) {
        shuffleMusicList.exec();
    }

    // play ボタン押下時
    public void onPlayClicked(View v) {
        try {
            switch (musicPlayer.getActionOnPlayClicked()) {
                case PAUSE:
                    mp.pause();
                    break;
                case RESUME:
                    mp.start();
                    break;
                case START:
                    playMusic(musicPlayer.getPlayMusicKeyOnPlayClicked());
                    shuffleMusicList.exec();
                    break;
                case NOTHING:
                    stopMusic();
                    break;
            }
        } catch (Exception ex) {
            msgView.outErrorMessage(ex);
        }
    }

    public void playMusic(String key) throws Exception {
        musicLinearLayout.resetMusicRowBackGround();
        musicPlayer.startMusicPlayer(key);
        musicPlayer.showTrackNo();
        musicLinearLayout.selectMusicAndDeselectOldMusic(key);
        musicScrollView.scrollMusicView();
        musicLinearLayout.setMusicRowBackGround();
        musicPlayerButton.playButtonCheck(true);
    }

    public void stopMusic() throws Exception {
        musicLinearLayout.resetMusicRowBackGround();
        musicPlayerButton.playButtonCheck(false);
        musicPlayer.stopMusicPlayer();
        musicPlayer.showTrackNo();
        musicSeekBar.setTotalTime(0);
        musicSeekBar.setPlayTime(0);
    }

//    // クリアボタン押下時
//    public void onClearClicked(View v) {
//        ((EditText) findViewById(R.id.editText)).setText("");
//        keyWord.afterTextChangedProcess();
//    }

    // 検索ボタン押下時
    public void onSearchClicked(View v) {
        keyWord.execSearch();
    }

    // 前ボタン押下時
    public void onPrevClicked(View v) {
        try {
            switch (musicPlayer.getActionOnPrevClicked()) {
                case RESTART:
                    mp.seekTo(0);
                    musicScrollView.scrollMusicView();
                    break;
                case STOP:
                    stopMusic();
                    break;
                case GO_PREV:
                    Integer prevTrackNo = musicPlayer.getPrevTrackNo();
                    if (prevTrackNo == null) prevTrackNo = 0;
                    stopMusic();
                    playMusic(displayMusicNames.get(prevTrackNo));
                    break;
            }
        } catch (Exception ex) {
            msgView.outErrorMessage(ex);
        }
    }

    // 次ボタン押下時
    public void onNextClicked(View v) {
        try {
            switch (musicPlayer.getActionOnNextClicked()) {
                case STOP:
                    stopMusic();
                    break;
                case GO_NEXT:
                    Integer nextTrackNo = musicPlayer.getNextTrackNo();
                    if (nextTrackNo == null) nextTrackNo = 0;
                    playMusic(displayMusicNames.get(nextTrackNo));
            }
        } catch (Exception ex) {
            msgView.outErrorMessage(ex);
        }
    }

    public void onLoopClicked(View v) {
        musicPlayerButton.clickLoopButton();
    }

//    public static void verifyStoragePermissions(Activity activity) {
//        // Check if we have write permission
//        CURRENT_PERMISSION = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
//
//        if (CURRENT_PERMISSION != PackageManager.PERMISSION_GRANTED) {
//            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(
//                    activity,
//                    PERMISSIONS_STORAGE,
//                    REQUEST_EXTERNAL_STORAGE
//            );
//        }
//    }

    // 戻る押下時処理
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            goHome();
        }
    }

    // ホーム画面移行
    private void goHome() {
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainActivity.this.startActivity(homeIntent);
    }

    // アプリ終了
    private void finishApp() throws Exception {
        if (!PLAYING_MUSIC.isEmpty()) {
            stopMusic();
        }
        super.onBackPressed();
        super.finish();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        measureDisplayWidth();
    }

    private void measureDisplayWidth() {
        Point outSize = new Point();
        WindowManager wm = (WindowManager) this.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getSize(outSize);
        DISPLAY_WIDTH = outSize.x;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        try {
            if (id == R.id.action_settings) {
                return true;
            } else if (id == R.id.action_update) {
                // 更新ボタン押下
                update();
                return true;
            } else if (id == R.id.action_exit) {
                // 終了ボタン押下
                finishApp();
                return true;
            }
        } catch (Exception ex) {
            msgView.outErrorMessage(ex);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
