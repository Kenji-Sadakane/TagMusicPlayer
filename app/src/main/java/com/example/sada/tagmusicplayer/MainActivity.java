package com.example.sada.tagmusicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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

import com.example.sada.tagmusicplayer.bean.RelateTag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements OnNavigationItemSelectedListener, Runnable {

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static int CURRENT_PERMISSION;

    private static final String BASE_DIR = "/storage/sdcard1/PRIVATE/SHARP/CM/MUSIC";
    public static final String TAG_INFO_PATH = "/storage/sdcard1/PRIVATE/SHARP/CM/MUSIC/tagInfo.tsv";

    public static Map<String, MusicItem> musicItems = new LinkedHashMap<>();
    public static List<String> tagKinds = new ArrayList<>();
    public static List<String> displayMusicNames = new ArrayList<>();
    public static List<RelateTag> relateTags = new ArrayList<>();
    public static String SELECT_MUSIC = "";
    public static String PLAYING_MUSIC = "";
    public static MediaPlayer mp = new MediaPlayer();

    public KeyWord keyWord = new KeyWord(this);
    public MusicField musicField = new MusicField(this);
    private MusicFile musicFile = new MusicFile(this);
    public MusicPlayer musicPlayer = new MusicPlayer(this);
    public MusicPlayerButton musicPlayerButton = new MusicPlayerButton(this);
    public SearchSwitch searchSwitch = new SearchSwitch(this);
    public ShuffleMusicList shuffleMusicList = new ShuffleMusicList(this);
    public MusicSeekBar musicSeekBar = new MusicSeekBar(this);
    public RelateTagField relateTagField = new RelateTagField(this);
    public TagInfoFile tagInfoFile = new TagInfoFile();
    public TagInfoDialog tagInfoDialog = new TagInfoDialog(this);

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
        verifyStoragePermissions(this);
        if (CURRENT_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        try {
            // タグ、楽曲表示
            displayContents();
            //
            // listener
            setListener();
            //
            Thread currentThread = new Thread(this);
            currentThread.start();
        } catch (Exception ex) {
            musicField.outErrorMessage(ex);
        }
    }

    private void displayContents() throws Exception {
        // 楽曲リスト取得
        musicFile.readMusicFiles(BASE_DIR);
        // タグ情報取得
        tagInfoFile.readTagInfo();
        // 楽曲リスト(画面部品)作成
        musicField.createContents();
        // キーワードに応じて表示内容切替
        musicField.changeMusicList();
        // 楽曲非選択化
        musicField.unselectedMusic();
        // 楽曲再生停止
        if (!PLAYING_MUSIC.isEmpty()) {
            stopMusic();
        }
        // トラック番号表示
        musicPlayer.showTrackNo();
    }

    private void setListener() {
        // テキストボックス
        keyWord.setListener();
        // シークバー
        musicSeekBar.setOnSeekBarChangeListener();
        // スクロール時
        musicField.setListener();
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
            musicField.outErrorMessage(ex);
        }
    }

    public void playMusic(String key) throws Exception {
        musicField.resetMusicRowBackGround();
        musicPlayer.startMusicPlayer(key);
        musicPlayer.showTrackNo();
        musicField.selectMusic(key);
        musicField.scrollMusicView();
        musicField.setMusicRowBackGround();
        musicPlayerButton.playButtonCheck(true);
    }

    public void stopMusic() throws Exception {
        musicField.resetMusicRowBackGround();
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
                    musicField.scrollMusicView();
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
            musicField.outErrorMessage(ex);
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
            musicField.outErrorMessage(ex);
        }
    }

    public void onLoopClicked(View v) {
        musicPlayerButton.clickLoopButton();
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        CURRENT_PERMISSION = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (CURRENT_PERMISSION != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

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
                displayContents();
                return true;
            } else if (id == R.id.action_exit) {
                finishApp();
                return true;
            }
        } catch (Exception ex) {
            musicField.outErrorMessage(ex);
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